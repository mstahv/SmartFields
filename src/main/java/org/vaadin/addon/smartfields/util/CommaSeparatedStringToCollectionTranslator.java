package org.vaadin.addon.smartfields.util;

import java.util.Collection;
import java.util.Iterator;

import org.vaadin.addon.propertytranslator.PropertyTranslator;

import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;

@SuppressWarnings("serial")
public class CommaSeparatedStringToCollectionTranslator extends
		PropertyTranslator {

	private Class<?> elemType;

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		elemType = Util
				.introspectElementTypeFromCollectionProperty((MethodProperty<?>) newDataSource);
		super.setPropertyDataSource(newDataSource);
	}

	@Override
	public Object translateFromDatasource(Object arg0) {
		Collection c = (Collection) arg0;
		StringBuilder sb = new StringBuilder();
		for (Iterator iterator = c.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			sb.append(object);
			if (iterator.hasNext()) {
				sb.append(getSeparator());
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	@Override
	public Object translateToDatasource(Object arg0) throws Exception {
		String[] split = arg0.toString().split(getSeparator());
		Collection value = (Collection) getPropertyDataSource().getValue();
		value.clear();
		for (String string : split) {
			value.add(Util.convertToObject(string.trim(), elemType));
		}
		return value;
	}

	private String getSeparator() {
		return ",";
	}

}
