package org.vaadin.addon.smartfields;

import java.io.Serializable;
import java.util.Collection;

/**
 * TODO consider providing e.g. bean and propertyId as parameters
 *
 */
public interface BeanProvider extends Serializable {
	
	public Collection<?> getPossibleSelections(Class<?> type);

	// TODO proper exception addition not valid
	public void persist(Class<?> elementType, Object newInstance);
}
