package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;

/**
 * FileSelect is a simple single file/directory selector component
 * 
 * @author Tatu Lund
 * */
@Tag("div")
public class FileSelect extends AbstractField<FileSelect,File> implements HasSize, HasValidation, HasComponents, SingleSelect<FileSelect, File> {
	// public class FileSelect extends CustomField<File> {
	private String filesText = "files";
	
    private Tree<File> tree = new Tree<>();
    private Component content;
    
    private File rootFile;
	private File selectedFile = null;
	private String filter = null;
	private FilesystemData root = null;

	private static int GIGA = 1024*1024*1024;
	private static int MEGA = 1024*1024;
	private static int KILO = 1024;

	private String errorMessage = "Validation error";
	private Div errorLabel = new Div();
	private Label label = new Label();
	private Label required = new Label("*");

	final static String LIST_BORDER = "1px var(--lumo-primary-color) solid";
	final static String LIST_BORDER_ERROR = "1px var(--lumo-error-color) solid";
	final static String LIST_BACKGROUND_ERROR = "var(--lumo-error-color-10pct)";
	final static String LIST_BACKGROUND = "var(--lumo-contrast-10pct)";
	
	
	// There is no actual Tree component in Vaadin 14 yet, so we substitute with TemplateRenderer
	
	private class Tree<T> extends TreeGrid<T> {
	    public Column<T> addHierarchyColumn(ValueProvider<T, ?> valueProvider, ValueProvider<T, ?> iconProvider, ValueProvider<T, ?> titleProvider) {
	        Column<T> column = addColumn(TemplateRenderer
	                .<T> of("<vaadin-grid-tree-toggle title='[[item.title]]'"
	                        + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
	                        + "<iron-icon icon='vaadin:[[item.icon]]' style='padding-right: 10px'></iron-icon>" 
	                		+ "[[item.name]]"
	                        + "</vaadin-grid-tree-toggle>")
	                .withProperty("leaf",
	                        item -> !getDataCommunicator().hasChildren(item))
	                .withProperty("title", title -> String.valueOf(titleProvider.apply(title)))
	                .withProperty("icon", icon -> fixIconName(String.valueOf(iconProvider.apply(icon))))
	                .withProperty("name",
	                        value -> String.valueOf(valueProvider.apply(value))));
	        final SerializableComparator<T> comparator = 
	                (a, b) -> compareMaybeComparables(valueProvider.apply(a),
	                        valueProvider.apply(b));
	        column.setComparator(comparator);

	        return column;
	    }
	    
	    private String fixIconName(String name) {
	    	String trimmed;
	    	trimmed = name.toLowerCase();
	    	trimmed = trimmed.replace("_", "-");
	    	return trimmed;
	    }
	}	
	
	/**
	 * Constructor
	 * 
	 * @param rootFile The root directory where to browse
	 */
	public FileSelect(File rootFile) {
		this(rootFile,null);
	}

	/**
	 * Alternative constructor with filter
	 * 
	 * @since 1.1.0
	 * 
	 * @param rootFile The root directory where to browse
	 * @param filter Set filter used for filename extension
	 */
	public FileSelect(File rootFile, String filter) {
		super(null);
		this.rootFile = rootFile;
		if (filter != null) this.filter = filter;
		getElement().getStyle().set("display", "flex");
		getElement().getStyle().set("flex-direction", "column");
		setErrorLabelStyles();
		errorLabel.setVisible(false);
		HorizontalLayout indicators = new HorizontalLayout();
		indicators.setSpacing(false);
		indicators.setMargin(false);
        label.setVisible(false);
        required.setVisible(false);
		indicators.add(required,label);
		setLabelStyles(label);
		setLabelStyles(required);
		content = setupTree();
		add(indicators,content,errorLabel);
	}

	private void setLabelStyles(HasStyle label) {
		label.getStyle().set("align-self", "flex-start");
		label.getStyle().set("color","var(--lumo-secondary-text-color)");
		label.getStyle().set("font-weight","500");
		label.getStyle().set("font-size","var(--lumo-font-size-s)");
		label.getStyle().set("transition","color 0.2s");
		label.getStyle().set("line-height","1");
		label.getStyle().set("overflow","hidden");
		label.getStyle().set("white-space","nowrap");
		label.getStyle().set("text-overflow","ellipsis");
		label.getStyle().set("position","relative");
		label.getStyle().set("max-width","100%");
		label.getStyle().set("box-sizing","border-box");
    }   
    
	private void setRequiredStyles() {
		required.getStyle().set("color","var(--lumo-primary-color)");
	}
	
	private void setErrorLabelStyles() {
		errorLabel.getStyle().set("color", "var(--lumo-error-text-color)");
		errorLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		errorLabel.getStyle().set("line-height", "var(--lumo-line-height-xs)");
		errorLabel.getStyle().set("will-change", "max-height");
		errorLabel.getStyle().set("transition", "0.4s max-height");
		errorLabel.getStyle().set("max-height", "5em");
		errorLabel.getStyle().set("align-self", "flex-end");
	}
	
	private Component setupTree() {
		if (filter != null) {
			root = new FilesystemData(rootFile, filter, false);
		} else {
			root = new FilesystemData(rootFile, false);
		}
    	FilesystemDataProvider fileSystem = new FilesystemDataProvider(root);
        tree.setDataProvider(fileSystem);

        tree.addHierarchyColumn(File::getName,file -> FileTypeResolver.getIcon(file),file -> getFileDescription(file));
        tree.setSelectionMode(SelectionMode.SINGLE);
        tree.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        tree.addSelectionListener(event -> {
        	selectedFile = null;
        	event.getFirstSelectedItem().ifPresent(file -> {
        		selectedFile = file;
        		this.setValue(selectedFile);
        	});
        });
        tree.setWidth("100%");
        tree.setHeight("100%");
		tree.getStyle().set("border", LIST_BORDER);
		tree.getStyle().set("background", LIST_BACKGROUND);		
        setSizeFull();
        
        return tree;
	}

	private String getFileDescription(File file) {
		String desc = "";
		if (!file.isDirectory()) {
			Date date = new Date(file.lastModified());
			long size = file.length();
			String unit = "";
			if (size > GIGA) {
				size = size / GIGA;
				unit = "GB";
			}
			else if (size > MEGA) {
				size = size / MEGA;
				unit = "MB";
			}
			else if (size > KILO) {
				size = size / KILO;
				unit = "KB";
			} else {
				unit = "B";        			
			}        			        		
			desc = file.getName()+", "+date+", "+size+ " "+unit;        		
		} else {
			desc = root.getChildrenFromFilesystem(file).size()+" "+filesText;
		}
		return desc;
	}

	/**
	 * Set String used for "files" text, for localization.
	 * 
	 * @since 1.1.0
	 * 
	 * @param filesText String for "files" text
	 */
	public void setFilesText(String filesText) {
		this.filesText = filesText;
	}
	
	@Override
	protected void setPresentationValue(File value) {
		tree.select(value);
	}

	@Override
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;			
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

	@Override
	public void setInvalid(boolean invalid) {
		if (invalid) {
			tree.getStyle().set("border", LIST_BORDER_ERROR);
			tree.getStyle().set("background", LIST_BACKGROUND_ERROR);
			errorLabel.setText(errorMessage);
			errorLabel.setVisible(true);
		} else {
			tree.getStyle().set("border", LIST_BORDER);		
			tree.getStyle().set("background", LIST_BACKGROUND);
			errorLabel.setVisible(false);
		}

	}

	@Override
	public boolean isInvalid() {
		return errorLabel.isVisible();
	}

	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
	    required.setVisible(true);
	}
	
	@Override
	public boolean isRequiredIndicatorVisible() {
		return required.isVisible();
	}	

	public void setLabel(String label) {
        if (label != null) {
        	this.label.setText(label);
        	this.label.setVisible(true);
        } else {
        	this.label.setVisible(false);
        }
    }
}

