package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.vaadin.filesystemdataprovider.FilesystemData.FileExtensionFilter;

import com.vaadin.flow.data.provider.hierarchy.TreeData;

/**
 * This is the data model class for FtpDataProvider, which is just a TreeDataProvider
 * using FtpData 
 * 
 * @since 2.1.0
 * @author Tatu Lund
 *
 */
public class FtpData extends TreeData<FtpFile> {

    private FTPFileFilter filter = null;
	private boolean recursive;
	private FTPClient client;
	
	/**
	 * Consruct new FtpData based on root folder recursively
	 * Note: Symlinks are not followed
	 * 
	 * @param client FTP client
     * @since 2.1.0
	 */
	public FtpData(FTPClient client) {
		super();
		this.client = client;
		FtpFile root = new FtpFile();
        root.setPath("/");
        root.setRoot(true);
        this.addRootItems(root);
        constructFileSystem(root);
	}

	/**
	 * Construct new FtpData with given folder either
	 * recursive or non-recursive
	 * Note: Symlinks are not followed in recursive mode
	 * 	 
	 * @param client FTP client
	 * @param recursive Set to false to disable recursive search
     * @since 2.1.0
	 */
	public FtpData(FTPClient client, boolean recursive) {
		super();
		this.client = client;
		FtpFile root = new FtpFile();
        root.setPath("/");
        root.setRoot(true);
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
	 * Construct new FtpData with given folder either
	 * recursive or non-recursive
	 * Note: Symlinks are not followed in recursive mode
	 * Give file extension filter, only files with extension are included
	 * 
	 * @param client FTP client
	 * @param recursive Set to false to disable recursive search
	 * @param filter The file extension as string
     * @since 2.1.0
	 */
	public FtpData(FTPClient client, String filter, boolean recursive) {
		super();
		this.client = client;
		FtpFile root = new FtpFile();
        root.setPath("/");
        root.setRoot(true);
        this.addRootItems(root);
        setRecursive(recursive);
        this.filter = new FileExtensionFilter(filter);
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
     * @since 2.1.0
	 */
	public FtpData(FtpFile root, String filter, boolean recursive) {
		super();
        this.addRootItems(root);
        setRecursive(recursive);
        this.filter = new FileExtensionFilter(filter);
        if (recursive) {
        	constructFileSystem(root);
        } else {
        	if (root.isDirectory() && !root.isSymbolicLink()) {
        		this.addItems(root, getChildrenFromFilesystem(root));
        	}
        }        
	}

	// Recursive filesystem crawler function
	private void constructFileSystem(FtpFile root) {
        if (root.isDirectory()) {
        	List<FtpFile> files = getChildrenFromFilesystem(root);
        	addItems(root, files);
        	for (FtpFile file : files) {
        		if (file.isDirectory()) constructFileSystem(file);
        	}
        }        		
	}	

	/**
	 * Helper method used internally for fetching files lazily from
	 * FtpDataProvider
	 * 
	 * @param path The folder
	 * @return List of files in the folder
     * @since 2.1.0
	 */
    public List<FtpFile> getChildrenFromFilesystem(FtpFile item) {
    	String path = item.getPath();
        if (!item.isDirectory()) {
        	return new LinkedList<FtpFile>();
        }
        List<FtpFile> f = new LinkedList<>();
        if (filter == null) {
            try {
				FTPFile[] files = client.listFiles(path);
				for (FTPFile file : files) {
			    	FtpFile newFile = new FtpFile(file);
					newFile.setPath(path+newFile.getName()+"/");
					f.add(newFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        } else {
            try {
				FTPFile[] files = client.listFiles(path, filter);
				for (FTPFile file : files) {
			    	FtpFile newFile = new FtpFile(file);
					newFile.setPath(path+newFile.getName()+"/");
					f.add(newFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        Collections.sort(f);

        return f;
    }

    public class FileExtensionFilter implements FTPFileFilter, Serializable {

        private final String filter;

        /**
         * Constructs a new FileExtensionFilter using given extension.
         *
         * @param fileExtension
         *            the File extension without the separator (dot).
         * @since 2.1.0
         */
        public FileExtensionFilter(String fileExtension) {
            filter = "." + fileExtension;
        }

        /**
         * Allows only files with the extension and directories.
         *
         * @see java.io.FilenameFilter#accept(File, String)
         * @since 2.1.0
         */
        @Override
        public boolean accept(FTPFile file) {
            if (file.getName().endsWith(filter)) {
                return true;
            }
            return file.isDirectory();
        }

    }

    // Used internally, does not have impact post construct, hence private
    private void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
    
	/**
	 * Return current recursive / non-recursive state	
	 * 
	 * @return True if FtpData is recursive
     * @since 2.1.0
	 */
    public boolean isRecursive() {
        return recursive;
    }
  
    /**
     * Set new file name filter
     * 
     * @param filter The filter
     * @since 2.1.0
     */
    public void setFilter(FTPFileFilter filter) {
        this.filter = filter;
    }

    /**
     * Set new file name filter
     * 
     * @param extension Filter as string
     * @since 2.1.0
     */
    public void setFilter(String extension) {
        filter = new FileExtensionFilter(extension);
    }

}
