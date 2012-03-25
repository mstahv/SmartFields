package org.vaadin.addon.smartfields;

import com.vaadin.data.Item;
import com.vaadin.ui.Form;

/**
 * SubFormConfigurators and be hooked to SmartFieldFactory to customize how
 * customized various subforms that are created by e.g.
 * {@link PopupEditableCollectionField}.
 */
public interface SubFormConfigurator {

	/**
	 * Creates a Form for this type. The given item should be assigned into the
	 * form. If null is returned a default {@link Form} with "parent"
	 * {@link SmartFieldFactory} is used.
	 * 
	 * @param item
	 * @return a custom form or null if default behavior is ok
	 */
	Form createForm(Item item);

	/**
	 * This method is called just before the form is added to UI.
	 * 
	 * @param form
	 */
	void postConfigureForm(Form form);

}
