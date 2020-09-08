package org.vaadin.filesystemdataprovider;

import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;

// There is no actual Tree component in Vaadin 14 yet, so we substitute with TemplateRenderer
class Tree<T> extends TreeGrid<T> {
    public Column<T> addHierarchyColumn(ValueProvider<T, ?> valueProvider, ValueProvider<T, ?> iconProvider, ValueProvider<T, ?> titleProvider) {
        Column<T> column = addColumn(TemplateRenderer
                .<T> of("<vaadin-grid-tree-toggle title='[[item.title]]'"
                        + "leaf='[[item.leaf]]' expanded='{{expanded}}' level='[[level]]'>"
                        + "<iron-icon icon='vaadin:[[item.icon]]' style='padding-right: 10px'></iron-icon>" 
                		+ "[[item.name]]"
                        + "</vaadin-grid-tree-toggle>")
                .withProperty("leaf",
                        item -> !getDataCommunicator().hasChildren(item))
                .withProperty("title", title -> String.valueOf(titleProvider.apply(title)))
                .withProperty("icon", icon -> fixIconName(String.valueOf(iconProvider.apply(icon))))
                .withProperty("name",
                        value -> String.valueOf(valueProvider.apply(value))));
        final SerializableComparator<T> comparator = 
                (a, b) -> compareMaybeComparables(valueProvider.apply(a),
                        valueProvider.apply(b));
        column.setComparator(comparator);

        return column;
    }
    
    private String fixIconName(String name) {
    	String trimmed;
    	trimmed = name.toLowerCase();
    	trimmed = trimmed.replace("_", "-");
    	return trimmed;
    }
}