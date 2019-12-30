package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class View extends VerticalLayout {

    public View() {
    	File rootFile3 = new File("C:/");
    	FileSelect fileSelect = new FileSelect(rootFile3);
    	fileSelect.addValueChangeListener(event -> {
        	File file = fileSelect.getValue();
    		Date date = new Date(file.lastModified());
        	if (!file.isDirectory()) {
        		Notification.show(file.getPath()+", "+date+", "+file.length());        		
        	} else {
        		Notification.show(file.getPath()+", "+date);
        	}
    	});
    	fileSelect.setWidth("500px");
    	fileSelect.setHeight("500px");
    	setSizeFull();
    	this.setAlignItems(Alignment.CENTER);
    	this.setJustifyContentMode(JustifyContentMode.CENTER);
    	fileSelect.setLabel("Select file");
        add(fileSelect);
    }
}
