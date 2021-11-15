package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("grid")
public class GridView extends VerticalLayout {

    public GridView() {
    	File rootFile = new File("C:/Users/Tatu/Documents");
        FilesystemData root = new FilesystemData(rootFile, false);
        FilesystemDataProvider fileSystem = new FilesystemDataProvider(root);
        TreeGrid<File> tree = new TreeGrid<>();
        tree.setDataProvider(fileSystem);     
        tree.addHierarchyColumn(file -> file.getName()).setHeader("Name");
        tree.addColumn(file -> file.length()).setHeader("Size");
        tree.addColumn(file -> {
        	String perms = "";
        	if (file.canExecute()) perms+="E";
        	if (file.canRead()) perms+="R";
        	if (file.canWrite()) perms+="W";
        	return perms;
        }).setHeader("Permissions");
    	tree.addSelectionListener(event -> {
        	File file = event.getFirstSelectedItem().get();
    		Date date = new Date(file.lastModified());
        	if (!file.isDirectory()) {
        		Notification.show(file.getPath()+", "+date+", "+file.length());        		
        	} else {
        		Notification.show(file.getPath()+", "+date);
        	}
    	});
    	tree.setWidth("750px");
    	tree.setHeight("500px");
    	setSizeFull();
    	this.setAlignItems(Alignment.CENTER);
    	this.setJustifyContentMode(JustifyContentMode.CENTER);
        add(tree);
    }
}
