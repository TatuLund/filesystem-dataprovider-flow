package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.flow.data.provider.hierarchy.TreeData;

/**
 * This is the data model class for FilesystemDataProvider, which is just a TreeDataProvider
 * using FilesystemData 
 * 
 * @author Tatu Lund
 *
 */
public class FilesystemData extends TreeData<File> {

    private FilenameFilter filter = null;
	private boolean recursive;
	
	/**
	 * Consruct new FilesystemData based on root folder recursively
	 * Note: Symlinks are not followed
	 * 
	 * @param root Root folder
	 */
	public FilesystemData(File root) {
		super();
        this.addRootItems(root);
        constructFileSystem(root);
	}

	/**
	 * Construct new FilesystemData with given folder either
	 * recursive or non-recursive
	 * Note: Symlinks are not followed in recursive mode
	 * 	 
	 * @param root Root folder
	 * @param recursive Set to false to disable recursive search
	 */
	public FilesystemData(File root, boolean recursive) {
		super();
        this.addRootItems(root);
        setRecursive(recursive);
        if (recursive) {
        	constructFileSystem(root);
        } else {
        	if (root.isDirectory()) {
        		this.addItems(root, getChildrenFromFilesystem(root));
        	}
        }        
	}
	
	/**
	 * Construct new FilesystemData with given folder either
	 * recursive or non-recursive
	 * Note: Symlinks are not followed in recursive mode
	 * Give file extension filter, only files with extension are included
	 * 
	 * @param root Root folder
	 * @param recursive Set to false to disable recursive search
	 * @param filter The file extension as string
	 */
	public FilesystemData(File root, String filter, boolean recursive) {
		super();
        this.addRootItems(root);
        setRecursive(recursive);
        this.filter = new FileExtensionFilter(filter);
        if (recursive) {
        	constructFileSystem(root);
        } else {
        	if (root.isDirectory() && !Files.isSymbolicLink(root.toPath())) {
        		this.addItems(root, getChildrenFromFilesystem(root));
        	}
        }        
	}
	
	// Recursive filesystem crawler function
	private void constructFileSystem(File root) {
        if (root.isDirectory()) {
        	List<File> files = getChildrenFromFilesystem(root);
        	addItems(root, files);
        	for (File file : files) {
        		if (file.isDirectory()) constructFileSystem(file);
        	}
        }        		
	}
	
	/**
	 * Helper method used internally for fetching files lazily from
	 * FilesystemDataProvider
	 * 
	 * @param item The folder 
	 * @return List of files in the folder
	 */
    public List<File> getChildrenFromFilesystem(File item) {
        if (!item.isDirectory()) {
        	return new LinkedList<File>();
        }
        File[] f;
        if (filter != null) {
            f = item.listFiles(filter);
        } else {
            f = item.listFiles();
        }

        if (f == null) {
            return new LinkedList<File>();
        }

        final List<File> l = Arrays.asList(f);
        Collections.sort(l);

        return l;
    }

    public class FileExtensionFilter implements FilenameFilter, Serializable {

        private final String filter;

        /**
         * Constructs a new FileExtensionFilter using given extension.
         *
         * @param fileExtension
         *            the File extension without the separator (dot).
         */
        public FileExtensionFilter(String fileExtension) {
            filter = "." + fileExtension;
        }

        /**
         * Allows only files with the extension and directories.
         *
         * @see java.io.FilenameFilter#accept(File, String)
         */
        @Override
        public boolean accept(File dir, String name) {
            if (name.endsWith(filter)) {
                return true;
            }
            return new File(dir, name).isDirectory();
        }

    }

    // Used internally, does not have impact post construct, hence private
    private void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
    
	/**
	 * Return current recursive / non-recursive state	
	 * 
	 * @return True if FilesystemData is recursive
	 */
    public boolean isRecursive() {
        return recursive;
    }
  
    /**
     * Set new file name filter
     * 
     * @param filter The filter
     */
    public void setFilter(FilenameFilter filter) {
        this.filter = filter;
    }

    /**
     * Set new file name filter
     * 
     * @param extension Filter as string
     */
    public void setFilter(String extension) {
        filter = new FileExtensionFilter(extension);
    }
	
}
