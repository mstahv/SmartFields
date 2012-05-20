package org.vaadin.addon.smartfields;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.addon.smartfields.util.Util;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class SmartFieldFactory implements FormFieldFactory, TableFieldFactory {

	private BeanProvider containerProvider;

	private Map<Class<?>, SubFormConfigurator> subFormConfigurator = new HashMap<Class<?>, SubFormConfigurator>();

	private boolean immediate = true;

	private SmartFieldContext context;

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		Class<?> type = item.getItemProperty(propertyId).getType();
		Field field;

		field = createSmartField(type, item, propertyId);
		if (field == SmartFieldContext.HIDDEN_FIELD) {
			return null;
		}
		configureFieldFactory(field);
		if (field == null) {
			field = DefaultFieldFactory.createFieldByPropertyType(type);
			if (field instanceof TextField) {
				TextField tf = (TextField) field;
				tf.setNullRepresentation("");
			}
		}
		field.setCaption(DefaultFieldFactory
				.createCaptionByPropertyId(propertyId));
		if (field instanceof AbstractComponent) {
			AbstractComponent ac = (AbstractComponent) field;
			ac.setImmediate(isImmediate());
		}
		return field;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	private void configureFieldFactory(Field field) {
		if (field != null && field instanceof HasFieldFactory) {
			HasFieldFactory hff = (HasFieldFactory) field;
			hff.setFieldFactory(this);
		}
	}

	@SuppressWarnings("rawtypes")
	protected Field createSmartField(Class<?> type, Item item, Object propertyId) {
		try {
			BeanItem bi = (BeanItem) item;
			Class<? extends Object> parentType = bi.getBean().getClass();
			Class<? extends Field> fieldType = getContext().getFieldType(
					parentType, type, propertyId.toString());
			if (fieldType != null) {
				try {
					Field field = fieldType.getConstructor().newInstance();
					return field;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}

			// TODO relocate this to context and move visibleProperties
			// configuration to a separate method
			SmartField sf = getAnnotation(bi, propertyId);
			if (sf != null) {
				if (sf.hidden()) {
					return SmartFieldContext.HIDDEN_FIELD;
				}
				Class<? extends Field> value = sf.value();
				try {
					Constructor<? extends Field> constructor = value
							.getConstructor();
					Field field = constructor.newInstance();
					return field;
				} catch (Exception e) {
					// TODO support for bean-propertyid constructor
				}
			}
			if (Map.class.isAssignableFrom(type)) {
				return new MapField();
			}
			if (Collection.class.isAssignableFrom(type)) {
				return new CommaSeparatedCollectionField();
			}
			if (type.isEnum()) {
				return new EnumSelect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static SmartField getAnnotation(BeanItem<?> bi, Object propertyId) {
		try {
			Method method = Util.getMethod((MethodProperty<?>) bi
					.getItemProperty(propertyId));
			return method.getAnnotation(SmartField.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Field createField(Container container, Object itemId,
			Object propertyId, Component uiContext) {
		Item item = container.getItem(itemId);
		Class<?> type = item.getItemProperty(propertyId).getType();
		Field field;

		field = createSmartField(type, item, propertyId);
		if (field == SmartFieldContext.HIDDEN_FIELD) {
			return null;
		}
		configureFieldFactory(field);
		if (field == null) {
			field = DefaultFieldFactory.createFieldByPropertyType(type);
			if (field instanceof TextField) {
				TextField tf = (TextField) field;
				tf.setNullRepresentation("");
			}
		}
		field.setCaption(DefaultFieldFactory
				.createCaptionByPropertyId(propertyId));
		return field;
	}

	public BeanProvider getBeanProvider() {
		return containerProvider;
	}

	public void setBeanProvider(BeanProvider containerProvider) {
		this.containerProvider = containerProvider;
	}


	public void setSubFormConfigurator(Class<?> class1, SubFormConfigurator conf) {
		subFormConfigurator.put(class1, conf);
	}

	public SubFormConfigurator getSubFormConfigurator(Class<?> class1) {
		return subFormConfigurator.get(class1);
	}

	public Form createForm(Class<?> elementType, Item item) {
		SubFormConfigurator cnf = getSubFormConfigurator(elementType);
		if (cnf != null) {
			Form form = cnf.createForm(item);
			if (form != null) {
				return form;
			}
		}
		Form form = new Form();
		form.setFormFieldFactory(this);
		String[] propertyOrder = getContext().getEditableProperties(elementType);
		if (propertyOrder == null) {
			form.setItemDataSource(item);
		} else {
			form.setItemDataSource(item, Arrays.asList(propertyOrder));
		}
		return form;
	}

	public void postConfigureForm(Class<?> elementType, Form form) {
		SubFormConfigurator cnf = getSubFormConfigurator(elementType);
		if (cnf != null) {
			cnf.postConfigureForm(form);
		}
		// TODO Auto-generated method stub
	}

	public SmartFieldContext getContext() {
		if (context != null) {
			return context;
		} else {
			return SmartFieldContext.get();
		}
	}

	public void setContext(SmartFieldContext context) {
		this.context = context;
	}

}
