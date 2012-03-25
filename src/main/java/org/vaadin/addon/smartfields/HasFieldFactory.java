package org.vaadin.addon.smartfields;

/**
 * Smart fields that generate more fields in e.g. sub forms should implement
 * this interface so the {@link SmartFieldFactory} implementation "inherits" to
 * children.
 * 
 */
public interface HasFieldFactory {

	public void setFieldFactory(SmartFieldFactory smartFieldFactory);

}
