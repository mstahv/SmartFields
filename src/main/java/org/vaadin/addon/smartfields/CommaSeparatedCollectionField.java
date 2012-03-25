package org.vaadin.addon.smartfields;

import java.util.Collection;

import org.vaadin.addon.smartfields.util.CommaSeparatedStringToCollectionTranslator;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

/**
 * This field edits {@link Collection} type in a text field separated by commas.
 * Suits for element types that have String constructor. <p>
 * 
 * Note that the field uses PropertyTranslator to do string <-> Colection conversion, 
 * so when not used in a Form, set value to the translator.
 */
public class CommaSeparatedCollectionField extends TextField {

	public CommaSeparatedCollectionField() {
		setPropertyDataSource(new CommaSeparatedStringToCollectionTranslator());
	}
	
	public Property.Viewer getTranslator() {
		return (Viewer) getPropertyDataSource();
	}

}
