package org.vaadin.addon.smartfields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.util.converter.Converter;

public class CollectionStringConverter implements Converter<String, Collection> {

	@Override
	public Collection convertToModel(String value,
			Class<? extends Collection> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		String[] split = value.split(",");
		return new ArrayList<String>(Arrays.asList(split));
	}

	@Override
	public String convertToPresentation(Collection value,
			Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		return StringUtils.join(value, ",");
	}

	@Override
	public Class<Collection> getModelType() {
		return Collection.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
