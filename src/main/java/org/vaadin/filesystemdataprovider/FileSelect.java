package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.treegrid.TreeGrid;

/**
 * FileSelect is a simple single file/directory selector component
 * 
 * @author Tatu Lund
 * */
public class FileSelect extends CustomField<File> {
	private String filesText = "files";
	
    private TreeGrid<File> tree = new TreeGrid<>();
    private Component content;
    
    private File rootFile;
	private File selectedFile = null;
	private String filter = null;
	private FilesystemData root = null;

	private static int GIGA = 1024*1024*1024;
	private static int MEGA = 1024*1024;
	private static int KILO = 1024;
	
	/**
	 * Constructor
	 * 
	 * @param rootFile The root directory where to browse
	 */
	public FileSelect(File rootFile) {
		this.rootFile = rootFile;
		content = setupTree();
		setSizeFull();
		add(content);
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
		this.rootFile = rootFile;
		this.filter = filter;
		content = setupTree();
		setSizeFull();
		add(content);
	}

	private Component setupTree() {
		if (filter != null) {
			root = new FilesystemData(rootFile, filter, false);
		} else {
			root = new FilesystemData(rootFile, false);
		}
    	FilesystemDataProvider fileSystem = new FilesystemDataProvider(root);
        tree.setDataProvider(fileSystem);

        tree.addHierarchyColumn(File::getName);
        tree.setSelectionMode(SelectionMode.SINGLE);
        tree.addComponentColumn(file -> FileTypeResolver.getIcon(file)).setWidth("80px");
        tree.addColumn(file -> getFileDescription(file));
        tree.addSelectionListener(event -> {
        	selectedFile = null;
        	event.getFirstSelectedItem().ifPresent(file -> {
        		selectedFile = file;
        		this.setValue(selectedFile);
        	});
        });
        tree.setWidth("1000px");
        tree.setHeight("600px");

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
	protected File generateModelValue() {
		return selectedFile;
	}

	@Override
	protected void setPresentationValue(File value) {
		tree.select(value);
	}
	

}
