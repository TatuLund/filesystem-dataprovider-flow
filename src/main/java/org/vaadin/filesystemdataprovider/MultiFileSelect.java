package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;
import java.util.Set;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.selection.MultiSelectionListener;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.shared.Registration;

/**
 * MultiFileSelect is a simple multi file/directory selector component
 * 
 * @author Tatu Lund
 */
@Tag("div")
public class MultiFileSelect extends AbstractField<MultiFileSelect, Set<File>>
		implements HasSize, HasValidation, HasComponents, MultiSelect<MultiFileSelect, File>, FileSelectStyles {
	private String filesText = "files";

	private Tree<File> tree = new Tree<>();
	private Component content;

	private File rootFile;
	private Set<File> selectedFiles = null;
	private String filter = null;
	private FilesystemData root = null;

	private String errorMessage = defaultErrorMessage;
	private Div errorLabel = new Div();
	private Label label = new Label();
	private Label required = new Label("*");

	/**
	 * Constructor
	 * 
	 * @param rootFile
	 *            The root directory where to browse
	 */
	public MultiFileSelect(File rootFile) {
		this(rootFile, null);
	}

	/**
	 * Alternative constructor with filter
	 * 
	 * @since 1.1.0
	 * 
	 * @param rootFile
	 *            The root directory where to browse
	 * @param filter
	 *            Set filter used for filename extension
	 */
	public MultiFileSelect(File rootFile, String filter) {
		super(null);
		this.rootFile = rootFile;
		if (filter != null)
			this.filter = filter;
		getElement().getStyle().set("display", "flex");
		getElement().getStyle().set("flex-direction", "column");
		setErrorLabelStyles(errorLabel);
		errorLabel.setVisible(false);
		HorizontalLayout indicators = new HorizontalLayout();
		indicators.setSpacing(false);
		indicators.setMargin(false);
		label.setVisible(false);
		required.setVisible(false);
		indicators.add(required, label);
		setLabelStyles(label);
		setLabelStyles(required);
		content = setupTree();
		add(indicators, content, errorLabel);
	}

	private Component setupTree() {
		if (filter != null) {
			root = new FilesystemData(rootFile, filter, false);
		} else {
			root = new FilesystemData(rootFile, false);
		}
		FilesystemDataProvider fileSystem = new FilesystemDataProvider(root);
		tree.setDataProvider(fileSystem);
		tree.addHierarchyColumn(File::getName, file -> FileTypeResolver.getIcon(file),
				file -> getFileDescription(file));
		setupTreeStyles(tree, SelectionMode.MULTI);
		tree.addSelectionListener(event -> {
			selectedFiles = event.getAllSelectedItems();
			this.setValue(event.getAllSelectedItems());
		});
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
			} else if (size > MEGA) {
				size = size / MEGA;
				unit = "MB";
			} else if (size > KILO) {
				size = size / KILO;
				unit = "KB";
			} else {
				unit = "B";
			}
			desc = file.getName() + ", " + date + ", " + size + " " + unit;
		} else {
			desc = root.getChildrenFromFilesystem(file).size() + " " + filesText;
		}
		return desc;
	}

	/**
	 * Set String used for "files" text, for localization.
	 * 
	 * @since 1.1.0
	 * 
	 * @param filesText
	 *            String for "files" text
	 */
	public void setFilesText(String filesText) {
		this.filesText = filesText;
	}

	@Override
	protected void setPresentationValue(Set<File> value) {
		tree.asMultiSelect().setValue(value);
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
			setInvalidIndicator(tree, errorLabel);
		} else {
			errorLabel.setText(errorMessage);
			setValidIndicator(tree, errorLabel);
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

	@Override
	public Set<File> getValue() {
		return tree.asMultiSelect().getValue();
	}

	@Override
	public Set<File> getSelectedItems() {
		return tree.asMultiSelect().getSelectedItems();
	}

	@Override
	public void updateSelection(Set<File> arg0, Set<File> arg1) {
		tree.asMultiSelect().updateSelection(arg0, arg1);
	}

	@Override
	public Registration addSelectionListener(MultiSelectionListener<MultiFileSelect, File> listener) {
		return addValueChangeListener(event -> listener
				.selectionChange(new MultiSelectionEvent<>(this, this, event.getOldValue(), event.isFromClient())));
	}
}
