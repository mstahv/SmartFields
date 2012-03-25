package org.vaadin.addon.smartfields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

/**
 * An annotation that can be used at bean level to control SmartFieldFactory.
 * 
 * TODO consider splitting annotations to separate project: smaller dependency to domain layer.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface SmartField {
	
    String ALL = "";

	Class<? extends Field> value() default TextField.class;
    
    String[] visibleProperties() default ALL;

    String[] editableProperties() default ALL;

    boolean hidden() default false;
    
}
