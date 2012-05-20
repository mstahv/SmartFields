package org.vaadin.addon.smartfields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.vaadin.addon.customfield.CustomField;
import org.vaadin.addon.smartfields.util.Util;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.PopupVisibilityEvent;
import com.vaadin.ui.PopupView.PopupVisibilityListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.Reindeer;

/**
 * A simple {@link Map} editor.
 * <p>
 * <ul>
 * <li>The field don't really support buffering.
 * <li>Only simple key types are supported.
 * </ul>
 * <p>
 * TODO improve handling of null keys. E.g. allow multiple null keys temporary
 * during editing, but mark duplicate rows with red color.
 * 
 * TODO improve error handling
 *
 * TODO improve localization
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class MapField extends CustomField implements HasFieldFactory {

	AbsoluteLayout layout = new AbsoluteLayout();
	private Class<? extends Map> type;
	private CssLayout addView = new CssLayout();
	private TextField newKey = new TextField("Key for new entry:");

	PopupView add = new PopupView("Add entry", addView);

	private Table table = new Table();
	private Class keyType;
	private Class valueType;
	private Map map;
	private BeanContainer bc;
	private SmartFieldFactory ff;

	public MapField() {
		setCompositionRoot(layout);
		setWidth("100%");
		setHeight("300px");
		table.setSizeFull();
		layout.addComponent(table);
		layout.addComponent(add, "top:3px; right: 4px;");
		table.addGeneratedColumn("DELETE", new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, final Object itemId, Object columnId) {
				Button button = new Button("-");
				button.addListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						map.remove(itemId);
						table.removeItem(itemId);
					}
				});
				button.setStyleName(Reindeer.BUTTON_SMALL);
				return button;
			}
		});

	}

	@SuppressWarnings("unchecked")
	private void init(Class<?> type) {
		this.type = (Class<? extends Map>) type;
		
		add.addListener(new PopupVisibilityListener() {
			@Override
			public void popupVisibilityChange(PopupVisibilityEvent event) {
				if(event.isPopupVisible()) {
					newKey.setValue(null);
					newKey.selectAll();
				}
			}
		});

		addView.addComponent(newKey);
		newKey.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				if(event.getProperty().getValue() != null) {
					addEntry();
					add.setPopupVisible(false);
				} else {
					getWindow().showNotification("Fill in key");
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void addEntry() {
		map.put(Util.convertToObject(newKey.getValue().toString(), keyType), null);
		addEntry(newKey.getValue());
		add.setPopupVisible(false);
	}
	
	@Override
	public Class<?> getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		init(newDataSource.getType());
		super.setPropertyDataSource(newDataSource);
		introspectTypes((MethodProperty<?>) getPropertyDataSource());
		map = (Map) newDataSource.getValue();
		boolean basictype = Util.isBasicType(valueType);
		if (basictype) {
			bc = new BeanContainer(SimpleValueWrapper.class);
		} else {
			bc = new BeanContainer(valueType);
		}

		Set keySet = map.keySet();
		for (Object key : keySet) {
			addEntry(key);
		}

		table.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
		table.setContainerDataSource(bc);
		table.setEditable(true);
		table.setTableFieldFactory(getFieldFactory());
	}

	private TableFieldFactory getFieldFactory() {
		if(ff == null) {
			ff = new SmartFieldFactory();
		}
		return ff;
	}

	@SuppressWarnings("unchecked")
	private void addEntry(Object key) {
		if (Util.isBasicType(valueType)) {
			bc.addItem(key, new SimpleValueWrapper(key, map.get(key)));
		} else {
			bc.addItem(key, map.get(key));
		}
	}

	// TODO move this to Util
	private void introspectTypes(MethodProperty<?> propertyDataSource) {
		try {
			Field field = MethodProperty.class.getDeclaredField("getMethod");

			field.setAccessible(true);
			Method m = (Method) field.get(propertyDataSource);
			Type returnType = m.getGenericReturnType();
			if (returnType instanceof ParameterizedType) {
				ParameterizedType type = (ParameterizedType) returnType;
				Type[] typeArguments = type.getActualTypeArguments();
				keyType = (Class) typeArguments[0];
				valueType = (Class) typeArguments[1];
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public class SimpleValueWrapper {
		private Object key;

		public SimpleValueWrapper(Object key, Object o) {
			this.key = key;
		}

		public Object getValue() {
			return map.get(key);
		}

		@SuppressWarnings("unchecked")
		public void setValue(Object newValue) {
			if(newValue == null) {
				map.put(key, null);
			}
			if(newValue.getClass().isAssignableFrom(valueType)) {
				map.put(key, newValue);
			} else {
				map.put(key, Util.convertToObject(newValue.toString(), valueType));
			}
		}
	}

	@Override
	public void setFieldFactory(SmartFieldFactory smartFieldFactory) {
		ff = smartFieldFactory;
	}

}
