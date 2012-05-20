package org.vaadin.addon.smartfields;

import java.util.Collection;

import org.vaadin.addon.customfield.CustomField;
import org.vaadin.addon.smartfields.util.Util;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.Reindeer;

/**
 * A simple field for Collection type where items are listed in an editable
 * table.
 * <p>
 * <ul>
 * <li>The field don't really support buffering.
 * </ul>
 * <p>
 * TODO improve error handling
 * 
 * TODO improve localization
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class InlineEditableCollectionField extends CustomField implements
		 HasFieldFactory, ColumnGenerator {

	AbsoluteLayout layout = new AbsoluteLayout();

	Button add = new Button("+");

	private Table table = new Table();
	private Collection collection;
	private BeanItemContainer bc;
	private Class elementType;

	private String[] visibleProperties;

	private SmartFieldFactory fieldFactory;

	public InlineEditableCollectionField() {
		setCompositionRoot(layout);
		setWidth("100%");
		setHeight("300px");
		table.setSizeFull();
		table.setEditable(true);
		layout.addComponent(table);
		layout.addComponent(add, "top:2px; right: 4px;");

		add.addStyleName(Reindeer.BUTTON_SMALL);
		add.addListener(new Button.ClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					Object newBean = elementType.getConstructor().newInstance();
					bc.addItem(newBean);
					collection.add(newBean);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});

	}

	@Override
	public Class<?> getType() {
		Property propertyDataSource = getPropertyDataSource();
		if (propertyDataSource != null) {
			return propertyDataSource.getType();
		}
		return Collection.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		table.removeGeneratedColumn("DELETE");
		super.setPropertyDataSource(newDataSource);
		if(newDataSource == null) {
			table.setContainerDataSource(null);
			return;
		}
		table.setTableFieldFactory(getFieldFactory());
		collection = (Collection) newDataSource.getValue();
		elementType = Util
				.introspectElementTypeFromCollectionProperty((MethodProperty<?>) newDataSource);
		bc = new BeanItemContainer((Class) elementType, collection);

		table.setContainerDataSource(bc);
		visibleProperties = getFieldFactory().getContext().getVisibleProperties(elementType);
		if(visibleProperties == null) {
			visibleProperties = introspectVisiblePropertiesFromDomainClass();
		}
		if (visibleProperties != null) {
			table.setVisibleColumns(visibleProperties);
		}
		table.addGeneratedColumn("DELETE", this);
	}

	private String[] introspectVisiblePropertiesFromDomainClass() {
		@SuppressWarnings("unchecked")
		SmartField annotation = (SmartField) elementType.getAnnotation(SmartField.class);
		if(annotation != null) {
			return annotation.editableProperties();
		}
		return null;
	}


	@Override
	public void setFieldFactory(SmartFieldFactory smartFieldFactory) {
		this.fieldFactory = smartFieldFactory;
	}

	public SmartFieldFactory getFieldFactory() {
		if(fieldFactory == null) {
			fieldFactory = new SmartFieldFactory();
		}
		return fieldFactory;
	}

	@Override
	public Object generateCell(Table source, final Object itemId, Object columnId) {
		Button button = new Button("-");
		button.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				collection.remove(itemId);
				table.removeItem(itemId);
			}
		});
		button.setStyleName(Reindeer.BUTTON_SMALL);
		return button;
	}

}
