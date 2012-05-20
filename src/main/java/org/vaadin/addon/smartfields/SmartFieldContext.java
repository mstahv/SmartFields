package org.vaadin.addon.smartfields;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Field;

public class SmartFieldContext {

	private static SmartFieldContext defaultContext = new SmartFieldContext();

	public static SmartFieldContext get() {
		return defaultContext;
	}

	private HashMap<Class<?>, String[]> classToEditableProperties = new HashMap<Class<?>, String[]>();
	private HashMap<Class<?>, String[]> classToVisibleProperties = new HashMap<Class<?>, String[]>();

	private Map<Class<?>, Map<Class<?>, Map<String, Class<? extends Field>>>> overriddenTypes = new HashMap<Class<?>, Map<Class<?>, Map<String, Class<? extends Field>>>>();

	public static final Field HIDDEN_FIELD = (Field) Proxy.newProxyInstance(
			SmartFieldFactory.class.getClassLoader(),
			new Class[] { Field.class }, new InvocationHandler() {
				@Override
				public Object invoke(Object arg0, Method arg1, Object[] arg2)
						throws Throwable {
					return null;
				}
			});

	/**
	 * Sets which properties of given type are displayed editable in smart
	 * fields.
	 * 
	 * @param class1 the type of which editable properties are being defined
	 * @param properties
	 */
	public void setEditableProperties(Class<?> class1, String... properties) {
		classToEditableProperties.put(class1, properties);
	}
	
	public void setVisibleProperties(Class<?> class1, String... properties) {
		classToVisibleProperties.put(class1, properties);
	}

	/**
	 * @param class1
	 * @return editabe property ids, null if not defined when field should
	 *         display all properties as editable
	 */
	public String[] getEditableProperties(Class<?> class1) {
		String[] strings = classToEditableProperties.get(class1);
		if (strings != null) {
			return strings;
		}
		SmartField annotation = class1.getAnnotation(SmartField.class);
		if (annotation != null) {
			String[] editableProperties = annotation.editableProperties();
			if (!(editableProperties.length == 1 && editableProperties[0] == SmartField.ALL)) {
				return editableProperties;
			}
		}
		return null;
	}

	/**
	 * This method can be used to configure custom field types for specific
	 * fields.
	 * 
	 * @param parentType
	 *            the bean type for which properties this field type is applied
	 * @param type
	 *            the property type for which field type is applied, mandatory
	 * @param propertyId
	 *            property id for which the type is applied, null if should be
	 *            default for this type
	 * @param field
	 *            the field to be used
	 */
	public void setFieldType(Class<?> parentType, Class<?> type,
			String propertyId, Class<? extends Field> field) {
		Map<Class<?>, Map<String, Class<? extends Field>>> map2 = overriddenTypes
				.get(type);
		if (map2 == null) {
			map2 = new HashMap<Class<?>, Map<String, Class<? extends Field>>>();
			overriddenTypes.put(parentType, map2);
		}
		Map<String, Class<? extends Field>> map = map2.get(type);
		if (map == null) {
			map = new HashMap<String, Class<? extends Field>>();
			map2.put(type, map);
		}
		map.put(propertyId, field);
	}

	public Class<? extends Field> getFieldType(Class<?> parentType,
			Class<?> type, String propertyId) {
		Map<Class<?>, Map<String, Class<? extends Field>>> typeToPropertyToFieldType = overriddenTypes
				.get(parentType);
		if (typeToPropertyToFieldType != null) {
			Map<String, Class<? extends Field>> map = typeToPropertyToFieldType
					.get(type);
			if (map != null) {
				Class<? extends Field> class1 = map.get(propertyId);
				if (class1 == null) {
					class1 = map.get(null);
				}
				return class1;
			}
		}
		return null;
	}

	public String[] getVisibleProperties(Class<?> type) {
		String[] visibleProperties = classToVisibleProperties.get(type);
		if (visibleProperties != null) {
			return visibleProperties;
		}
		SmartField annotation = type.getAnnotation(SmartField.class);
		if (annotation != null) {
			visibleProperties = annotation.visibleProperties();
			if (!(visibleProperties.length == 1 && visibleProperties[0] == SmartField.ALL)) {
				return visibleProperties;
			}
		}
		return null;
	}

}
