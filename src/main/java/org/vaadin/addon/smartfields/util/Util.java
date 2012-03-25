package org.vaadin.addon.smartfields.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.util.MethodProperty;

public class Util {

	public static Method getMethod(MethodProperty<?> propertyDataSource)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = MethodProperty.class.getDeclaredField("getMethod");
		field.setAccessible(true);

		return (Method) field.get(propertyDataSource);

	}

	public static Class<?> introspectElementTypeFromCollectionProperty(
			MethodProperty<?> propertyDataSource) {
		try {
			Method m = getMethod(propertyDataSource);
			Type returnType = m.getGenericReturnType();
			ParameterizedType type = (ParameterizedType) returnType;
			Type[] typeArguments = type.getActualTypeArguments();
			return (Class) typeArguments[0];
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static Object convertToObject(String string, Class<?> elemType) {
		if (elemType.isAssignableFrom(String.class)) {
			return string;
		} else {
			try {
				Constructor constructor = elemType.getConstructor(String.class);
				return constructor.newInstance(string);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static final Set<Class<?>> SIMPLE_DATA_TYPES = new HashSet<Class<?>>(
			Arrays.asList(Boolean.class, String.class, Integer.class,
					Long.class, Float.class, Double.class, Date.class,
					Number.class));

	public static boolean isBasicType(Class type) {
		return SIMPLE_DATA_TYPES.contains(type);
	}

}
