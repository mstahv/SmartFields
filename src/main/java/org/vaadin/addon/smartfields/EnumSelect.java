package org.vaadin.addon.smartfields;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.ui.NativeSelect;

public class EnumSelect extends NativeSelect {
	
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		if(newDataSource != null) {
			removeAllItems();
			Class<?> type = newDataSource.getType();
			List<?> asList = Arrays.asList(type.getEnumConstants());
			for (Object object : asList) {
				addItem(object);
			}
		}
		super.setPropertyDataSource(newDataSource);
	}

}
