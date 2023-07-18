package org.vaadin.filesystemdataprovider;

import java.util.List;

import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.function.ValueProvider;

// There is no actual Tree component in Vaadin 14 yet, so we substitute with TemplateRenderer
class Tree<T> extends TreeGrid<T> {
	public Column<T> addHierarchyColumn(ValueProvider<T, ?> valueProvider, ValueProvider<T, ?> iconProvider,
			ValueProvider<T, ?> titleProvider) {
		Column<T> column = addColumn(LitRenderer.<T>of("""
                <vaadin-grid-tree-toggle
                    title='${item.title}'
                    @click=${onClick}
                    .leaf=${item.leaf}
                    .expanded=${model.expanded}
                    .level=${model.level}>
                        <vaadin-icon icon='vaadin:${item.icon}' style='padding-right: 10px'></vaadin-icon>
                        ${item.name}
                </vaadin-grid-tree-toggle>
                """).withProperty("leaf", item -> !getDataCommunicator().hasChildren(item))
				.withProperty("title", title -> String.valueOf(titleProvider.apply(title)))
				.withProperty("icon", icon -> fixIconName(String.valueOf(iconProvider.apply(icon))))
				.withProperty("name", value -> String.valueOf(valueProvider.apply(value)))
				.withFunction("onClick", (item) -> {
					if (this.getDataCommunicator().hasChildren(item)) {
						if (this.isExpanded(item)) {
							this.collapse(List.of(item), true);
						} else {
							this.expand(List.of(item), true);
						}
					}

				}));
		final SerializableComparator<T> comparator = (a, b) -> compareMaybeComparables(valueProvider.apply(a),
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