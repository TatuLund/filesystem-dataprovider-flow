package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.selection.SingleSelect;

/**
 * FtpSelect is a simple single file/directory selector component
 *
 * @since 2.1.0
 * 
 * @author Tatu Lund
 * */
@Tag("div")
public class FtpSelect extends AbstractField<FtpSelect,FtpFile> implements HasSize, HasValidation, HasComponents, SingleSelect<FtpSelect, FtpFile>, FileSelectStyles {
	private String filesText = "files";
	
    private Tree<FtpFile> tree = new Tree<>();
    private Component content;
    
	private FtpFile selectedFile = null;
	private String filter = null;
	private FtpData root = null;

	private String errorMessage = defaultErrorMessage;
	private Div errorLabel = new Div();
	private Label label = new Label();
	private Label required = new Label("*");

	private FTPClient client;
	
	/**
	 * Constructor
	 * 
	 * @since 2.1.0
	 * 
	 * @param client The FTP client where to browse
	 */
	public FtpSelect(FTPClient client) {
		this(client,null);
	}

	/**
	 * Alternative constructor with filter
	 * 
	 * @since 2.1.0
	 * 
	 * @param client The FTP client
	 * @param filter Set filter used for filename extension
	 */
	public FtpSelect(FTPClient client, String filter) {
		super(null);
		this.client = client;
		if (filter != null) this.filter = filter;
		getElement().getStyle().set("display", "flex");
		getElement().getStyle().set("flex-direction", "column");
		setErrorLabelStyles(errorLabel);
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

	private Component setupTree() {
		if (filter != null) {
			root = new FtpData(client, filter, false);
		} else {
			root = new FtpData(client, false);
		}
    	FtpDataProvider fileSystem = new FtpDataProvider(root);
        tree.setDataProvider(fileSystem);

        tree.addHierarchyColumn(FtpFile::getName,file -> FileTypeResolver.getIcon(file),file -> getFileDescription(file));
        setupTreeStyles(tree);		
        tree.addSelectionListener(event -> {
        	selectedFile = null;
        	event.getFirstSelectedItem().ifPresent(file -> {
        		selectedFile = file;
        		this.setValue(selectedFile);
        	});
        });	
        setSizeFull();
        
        return tree;
	}

	private String getFileDescription(FtpFile file) {
		String desc = "";
		if (!file.isDirectory()) {			
			Date date = Date.from(file.getTimestamp().toInstant());
			long size = file.getSize();
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
	protected void setPresentationValue(FtpFile value) {
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
}

