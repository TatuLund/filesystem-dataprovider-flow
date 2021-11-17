package org.vaadin.filesystemdataprovider;

import java.io.File;
import java.util.Date;
import java.util.Set;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@Route("multi")
public class MultiView extends VerticalLayout {

	public MultiView() {
		File rootFile3 = new File("C:/");
		MultiFileSelect fileSelect = new MultiFileSelect(rootFile3);
		fileSelect.addValueChangeListener(event -> {
			Set<File> files = fileSelect.getValue();
			for (File file : files) {
				Date date = new Date(file.lastModified());
				if (!file.isDirectory()) {
					Notification.show(file.getPath() + ", " + date + ", " + file.length());
				} else {
					Notification.show(file.getPath() + ", " + date);
				}
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
