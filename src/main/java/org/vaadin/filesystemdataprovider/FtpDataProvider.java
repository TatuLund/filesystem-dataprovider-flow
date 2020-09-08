package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.function.SerializablePredicate;


/**
 * FtpDataProvider is data provider that can map file system to hierarchical
 * data, so that it can be used e.g. with Tree and TreeGrid
 * 
 * @author Tatu Lund
 * @since 2.1.0
 *
 */
public class FtpDataProvider extends TreeDataProvider<FtpFile> {

    private boolean recursive;
    FtpData treeData;
    
    /**
     * Construct new FtpDataProvider with given FilesystemData 
     * 
     * @param treeData The data model
     * @since 2.1.0
     */
    public FtpDataProvider(FtpData treeData) {
    	super(treeData);
    	recursive = treeData.isRecursive();
    	this.treeData = treeData;
    }

    /**
     * Get the count of children based on query
     * 
     * @param query A query
     * 
     * @return The count of children
     * @since 2.1.0
     */
    @Override
    public int getChildCount(
            HierarchicalQuery<FtpFile, SerializablePredicate<FtpFile>> query) {    	
    	final FtpFile parent = query.getParentOptional().orElse(treeData.getRootItems().get(0));
    	if (parent.isFile()) return 0;
    	else return (int) fetchChildren(query).count();
    }    
    
    /**
     * Check if the file has children or not
     * 
     * @param item The File 
     * 
     * @return True if the File has children (i.e. it is a non empty directory)
     * @since 2.1.0
     */
    @Override
    public boolean hasChildren(FtpFile item) {
    	if (!isInMemory()) {
    		return item.isDirectory() && !treeData.getChildrenFromFilesystem(item).isEmpty();
    	} else {
    		return super.hasChildren(item);
    	}
    }

    /**
     * Return the files in directory as a Stream based on query
     *  
     * @param query A query
     * @return A stream of Files
     * @since 2.1.0
     */
    @Override
    public Stream<FtpFile> fetchChildren(
            HierarchicalQuery<FtpFile, SerializablePredicate<FtpFile>> query) {
    	if (!isInMemory()) {    		
        	FtpFile parent = query.getParentOptional().orElse(treeData.getRootItems().get(0));
			if (treeData.getChildren(parent).isEmpty()) {
	        	List<FtpFile> files = treeData.getChildrenFromFilesystem(parent);
				treeData.addItems(parent, files);
				return files.stream();
			} else {
				return super.fetchChildren(query);
			}
    	} else {
    		return super.fetchChildren(query);
    	}
    }
    
    /**
     * FtpDataProvider is fully in-memory if it is constructed
     * recursively, otherwise it is progressively lazy
     * 
     * @return boolean value
     * @since 2.1.0
     */
	@Override
	public boolean isInMemory() {
		return recursive;
	}

}
