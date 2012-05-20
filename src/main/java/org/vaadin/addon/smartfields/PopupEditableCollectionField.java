package org.vaadin.addon.smartfields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.vaadin.addon.customfield.CustomField;
import org.vaadin.addon.smartfields.util.Util;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * A simple field for Collection type where items are listed in a table, clicked
 * item is opened in a floating editor.
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
public class PopupEditableCollectionField extends CustomField implements HasFieldFactory {

	AbsoluteLayout layout = new AbsoluteLayout();

	Button add = new Button("+");

	private Table table = new Table();
	private Collection collection;
	private BeanItemContainer bc;
	private Class elementType;

	private String[] visibleProperties;

	private SmartFieldFactory fieldFactory;
	
	private String closeCaption = "Close";
	private String editorWindowCaption = null;

	public PopupEditableCollectionField() {
		setCompositionRoot(layout);
		setWidth("100%");
		setHeight("300px");
		table.setSizeFull();
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
					editBean(bc.getItem(newBean));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		table.addGeneratedColumn("DELETE", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, final Object itemId,
					Object columnId) {
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
		});

		table.addListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				editBean(event.getItem());
			}
		});

	}

	@Override
	public Class<?> getType() {
		Property propertyDataSource = getPropertyDataSource();
		if(propertyDataSource != null) {
			return propertyDataSource.getType();
		}
		return Collection.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
		collection = (Collection) newDataSource.getValue();
		elementType = Util
				.introspectElementTypeFromCollectionProperty((MethodProperty<?>) newDataSource);
		bc = new BeanItemContainer((Class) elementType, collection);

		table.setContainerDataSource(bc);
		if(visibleProperties == null) {
			visibleProperties = getFieldFactory().getContext().getVisibleProperties(elementType);
		}
		if(visibleProperties != null) {
			table.setVisibleColumns(getVisibleProperties());
		}

	}

	private Object[] getVisibleProperties() {
		ArrayList<String> arrayList = new ArrayList<String>(visibleProperties.length + 1);
		arrayList.addAll(Arrays.asList(visibleProperties));
		arrayList.add("DELETE");
		return arrayList.toArray();
	}

	protected void editBean(Item object) {
		Window window = new Window();
		window.setCaption(getEditorWindowCaption());
		window.center();
		window.setWidth("90%");
		window.setHeight("90%");
		Form form = createForm(object);
		window.addComponent(form);
		Window w = getWindow();
		if(w.getParent() != null) {
			w = w.getParent();
		}
		w.addWindow(window);
		fieldFactory.postConfigureForm(elementType, form);
		form.focus();
	}

	protected Form createForm(Item item) {
		Form form = fieldFactory.createForm(elementType, item);
		Button button = new Button(getCloseCaption());
		button.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Window w = event.getButton().getWindow();
				w.getParent().removeWindow(w);
			}});
		form.getFooter().addComponent(button);
		return form;
	}

	@Override
	public void setFieldFactory(SmartFieldFactory smartFieldFactory) {
		this.fieldFactory = smartFieldFactory;
	}
	
	public SmartFieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public String getCloseCaption() {
		return closeCaption;
	}

	public void setCloseCaption(String closeCaption) {
		this.closeCaption = closeCaption;
	}

	public String getEditorWindowCaption() {
		if(editorWindowCaption == null) {
			return "Edit " + elementType.getSimpleName();
		}
		return editorWindowCaption;
	}

	public void setEditorWindowCaption(String editorWindowCaption) {
		this.editorWindowCaption = editorWindowCaption;
	}

	public void setVisibleProperties(String... visibleProperties) {
		this.visibleProperties = visibleProperties;
	}

}
