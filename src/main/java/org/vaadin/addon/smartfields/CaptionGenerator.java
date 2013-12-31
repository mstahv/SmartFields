package org.vaadin.addon.smartfields;

public interface CaptionGenerator<T> {
	
	public String getCaption(T option);
	
}