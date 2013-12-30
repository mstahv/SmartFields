package org.vaadin.addon.smartfields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.vaadin.addon.smartfields.util.Util;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 * Selects a set of beans from available choices defined by {@link BeanProvider}
 * . Should work with virtually any collection type - not just with sets, but
 * same bean can be only once in the collection.
 * 
 * @see #setProvider(BeanProvider)
 * 
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class SubSetSelector extends CustomField implements HasFieldFactory {

	private Class<?> type;
	private BeanProvider provider;
	private ComboBox cb = new ComboBox();
	private Table table = new Table();
	private Collection selected;
	private BeanItemContainer availableContainer;
	private Button newEntity = new Button("Add new");
	private Class<?> elementType;
	private SmartFieldFactory ff;
	private List<String> visibleProperties;
	private HorizontalLayout toprow;
	private VerticalLayout verticalLayout;

	public SubSetSelector() {
		setHeight("300px");
		verticalLayout = new VerticalLayout();

		toprow = new HorizontalLayout();
		toprow.addComponent(cb);
		toprow.addComponent(newEntity);
		verticalLayout.addComponent(toprow);
		verticalLayout.addComponent(table);
		verticalLayout.setHeight("100%");
		verticalLayout.setExpandRatio(table, 1);
		table.setWidth("100%");
		table.setHeight("100%");
		table.setPageLength(5);
		table.addGeneratedColumn("Remove", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, final Object itemId,
					Object columnId) {
				Button button = new Button(getDeleteButtonCaption());
				button.addListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						availableContainer.addItem(itemId);
						table.removeItem(itemId);
						selected.remove(itemId);
						// fire value change
						fireValueChange(true);
					}
				});
				button.setStyleName(Reindeer.BUTTON_SMALL);
				return button;
			}

		});
		cb.setInputPrompt("Add to selection...");
		cb.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				if (event.getProperty().getValue() != null) {
					Object pojo = event.getProperty().getValue();
					cb.setValue(null);
					availableContainer.removeItem(pojo);
					table.addItem(pojo);
					selected.add(pojo);
					// fire value change
					fireValueChange(true);
				}
			}
		});
		cb.setImmediate(true);

		newEntity.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				addEntity();
			}

		});

	}
	
	public String getAddInputPrompt() {
		return cb.getInputPrompt();
	}
	
	public void setAddInputPrompt(String inputPrompt) {
		cb.setInputPrompt(inputPrompt);
	}
	
	public void setNewEntitiesAllowed(boolean allowed) {
		if(isNewEntitiesAllowed() != allowed) {
			if(allowed) {
				toprow.addComponent(newEntity);
			} else {
				toprow.removeComponent(newEntity);
			}
		}
	}
	
	public boolean isNewEntitiesAllowed() {
		return newEntity.getParent() != null;
	}
	

	protected String getDeleteButtonCaption() {
		return "-";
	}

	/**
	 * @see Table#setColumnHeaders(String[])
	 * @param headers
	 */
	public void setColumnHeaders(String... headers) {
		table.setColumnHeaders(headers);
	}

	/**
	 * @see Table#setColumnHeader(Object, String)
	 * 
	 * @param propertyId
	 * @param header
	 */
	public void setColumnHeader(Object propertyId, String header) {
		table.setColumnHeader(propertyId, header);
	}

	/**
	 * @return the reference to the Table used by this field internally.
	 *         Modifying this object directly might cause odd behavior.
	 */
	public Table getTable() {
		return table;
	}

	protected void addEntity() {
		try {
			final Object newInstance = elementType.getConstructor()
					.newInstance();

			String caption = "Add new " + elementType.getSimpleName();
			final Window window = new Window();
			window.setCaption(caption);
			window.setWidth("70%");
			window.setHeight("70%");
			window.center();

			Form form = new Form();
			form.setFormFieldFactory(getFieldFactory());
			String[] editableProperties = ff.getContext()
					.getEditableProperties(elementType);
			if (editableProperties == null) {
				form.setItemDataSource(new BeanItem(newInstance));
			} else {
				form.setItemDataSource(new BeanItem(newInstance),
						Arrays.asList(editableProperties));
			}

			Button button = new Button("Save and add");
			button.addListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					getProvider().persist(elementType, newInstance);
					selected.add(newInstance);
					table.addItem(newInstance);
					getUI().removeWindow(window);
					// fire value change
					fireValueChange(true);
				}
			});
			Button cancel = new Button("Cancel");
			cancel.addListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					getUI().removeWindow(window);
				}
			});
			form.getFooter().addComponent(button);
			form.getFooter().addComponent(cancel);

			window.setContent(form);

			getUI().addWindow(window);
			form.focus();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private SmartFieldFactory getFieldFactory() {
		if (ff == null) {
			ff = new SmartFieldFactory();
		}
		return ff;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		type = newDataSource.getType();
		elementType = Util
				.introspectElementTypeFromCollectionProperty((MethodProperty<?>) newDataSource);
		Collection available = getProvider().getPossibleSelections(elementType);
		selected = (Collection) newDataSource.getValue();
		available.removeAll(selected);

		availableContainer = new BeanItemContainer(elementType, available);
		cb.setContainerDataSource(availableContainer);
		table.setContainerDataSource(new BeanItemContainer(elementType,
				selected));
		if (visibleProperties != null) {
			table.setVisibleColumns(visibleProperties.toArray());
		}

		super.setPropertyDataSource(newDataSource);
	}

	@Override
	public void setFieldFactory(SmartFieldFactory smartFieldFactory) {
		ff = smartFieldFactory;
		setProvider(smartFieldFactory.getBeanProvider());
	}

	public BeanProvider getProvider() {
		return provider;
	}

	public void setProvider(BeanProvider provider) {
		this.provider = provider;
	}

	@Override
	protected void fireValueChange(boolean repaintIsNotNeeded) {
		super.fireValueChange(repaintIsNotNeeded);
	}

	public void setVisibleProperties(String... visible) {
		visibleProperties = new ArrayList<String>();
		for (String string : visible) {
			visibleProperties.add(string);
		}
		visibleProperties.add("Remove");
	}

	public String getNewEntityCaption() {
		return newEntity.getCaption();
	}

	public void setNewEntityCaption(String newEntityCaption) {
		newEntity.setCaption(newEntityCaption);
	}

	@Override
	protected Component initContent() {
		return verticalLayout;
	}
}
