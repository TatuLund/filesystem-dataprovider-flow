package org.vaadin.filesystemdataprovider;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;

interface FileSelectStyles {
	String defaultErrorMessage = "Validation error";

	final static String LIST_BORDER = "1px var(--lumo-primary-color) solid";
	final static String LIST_BORDER_ERROR = "1px var(--lumo-error-color) solid";
	final static String LIST_BACKGROUND_ERROR = "var(--lumo-error-color-10pct)";
	final static String LIST_BACKGROUND = "var(--lumo-contrast-10pct)";

	static int GIGA = 1024*1024*1024;
	static int MEGA = 1024*1024;
	static int KILO = 1024;

	default void setLabelStyles(Label label) {
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
    
	default void setRequiredStyles(Label required) {
		required.getStyle().set("color","var(--lumo-primary-color)");
	}
	
	default void setErrorLabelStyles(Div errorLabel) {
		errorLabel.getStyle().set("color", "var(--lumo-error-text-color)");
		errorLabel.getStyle().set("font-size", "var(--lumo-font-size-xs)");
		errorLabel.getStyle().set("line-height", "var(--lumo-line-height-xs)");
		errorLabel.getStyle().set("will-change", "max-height");
		errorLabel.getStyle().set("transition", "0.4s max-height");
		errorLabel.getStyle().set("max-height", "5em");
		errorLabel.getStyle().set("align-self", "flex-end");
	}

	default void setValidIndicator(Tree tree, Div errorLabel) {
		tree.getStyle().set("border", LIST_BORDER);		
		tree.getStyle().set("background", LIST_BACKGROUND);
		errorLabel.setVisible(false);
	}

	default void setInvalidIndicator(Tree tree, Div errorLabel) {
		tree.getStyle().set("border", LIST_BORDER_ERROR);
		tree.getStyle().set("background", LIST_BACKGROUND_ERROR);
		errorLabel.setVisible(true);
	}

	default String getDefaultErrorMessage() {
		return defaultErrorMessage;
	}

	default void setupTreeStyles(Tree tree, SelectionMode mode) {
		tree.setSelectionMode(mode);
        tree.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        tree.setWidth("100%");
        tree.setHeight("100%");
		tree.getStyle().set("border", LIST_BORDER);
		tree.getStyle().set("background", LIST_BACKGROUND);
	}
}

