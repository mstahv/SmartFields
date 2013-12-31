package org.vaadin.addon.smartfields;

import java.util.Collection;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;
import com.vaadin.ui.NativeSelect;

public class TSelect<T> extends CustomComponent implements Field<T> {

	private CaptionGenerator<T> captionGenerator;
	
	private AbstractSelect select;

	public TSelect(T... options) {
		setOptions(options);
		setCompositionRoot(getSelect());
	}

	public TSelect(String caption) {
		setCaption(caption);
		setCompositionRoot(getSelect());
	}

	public TSelect(String caption, Collection<T> listAllStyles) {
		this(caption);
		setOptions(listAllStyles);
	}

	protected AbstractSelect getSelect() {
		if (select == null) {
			select = new NativeSelect() {
				@SuppressWarnings("unchecked")
				@Override
				public String getItemCaption(Object itemId) {
					return TSelect.this.getCaption((T) itemId);
				}
			};
		}
		return select;
	}
	
	protected String getCaption(T option) {
		if(captionGenerator != null) {
			return captionGenerator.getCaption(option);
		}
		return option.toString();
	}

	private Class<? extends T> type;

	@SuppressWarnings("unchecked")
	public T getValue() {
		return (T) getSelect().getValue();
	}

	@Override
	public void focus() {
		getSelect().focus();
	}

	// public void setValue(T newFieldValue)
	// throws com.vaadin.data.Property.ReadOnlyException,
	// ConversionException {
	// getSelect().setValue(newValue);
	// super.setValue(newFieldValue);
	// }

	public void setOptions(T... values) {
		getSelect().removeAllItems();
		for (T t : values) {
			getSelect().addItem(t);
		}
	}

	// @Override
	// protected Component initContent() {
	// return getSelect();
	// }

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends T> getType() {
		if (type == null) {
			try {
				type = (Class<? extends T>) ((Container.Sortable) select
						.getContainerDataSource()).firstItemId().getClass();
			} catch (Exception e) {
			}
		}
		return type;
	}

	public void setType(Class<? extends T> type) {
		this.type = type;
	}

	@Override
	public boolean isInvalidCommitted() {
		return getSelect().isInvalidCommitted();
	}

	@Override
	public void setInvalidCommitted(boolean isCommitted) {
		getSelect().setInvalidCommitted(isCommitted);
	}

	@Override
	public void commit() throws SourceException, InvalidValueException {
		getSelect().commit();
	}

	@Override
	public void discard() throws SourceException {
		getSelect().discard();
	}

	@Override
	public void setBuffered(boolean buffered) {
		getSelect().setBuffered(buffered);
	}

	@Override
	public boolean isBuffered() {
		return getSelect().isBuffered();
	}

	@Override
	public boolean isModified() {
		return getSelect().isModified();
	}

	@Override
	public void addValidator(Validator validator) {
		getSelect().addValidator(validator);
	}

	@Override
	public void removeValidator(Validator validator) {
		getSelect().removeValidator(validator);
	}

	@Override
	public void removeAllValidators() {
		getSelect().removeAllValidators();
	}

	@Override
	public Collection<Validator> getValidators() {
		return getValidators();
	}

	@Override
	public boolean isValid() {
		return getSelect().isValid();
	}

	@Override
	public void validate() throws InvalidValueException {
		getSelect().validate();
	}

	@Override
	public boolean isInvalidAllowed() {
		return getSelect().isInvalidAllowed();
	}

	@Override
	public void setInvalidAllowed(boolean invalidValueAllowed)
			throws UnsupportedOperationException {
		getSelect().setInvalidAllowed(invalidValueAllowed);
	}

	@Override
	public void setValue(T newValue) throws ReadOnlyException {
		getSelect().setValue(newValue);
	}

	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		getSelect().addValueChangeListener(listener);
	}

	@Override
	public void addListener(ValueChangeListener listener) {
		getSelect().addValueChangeListener(listener);
	}

	@Override
	public void removeValueChangeListener(ValueChangeListener listener) {
		getSelect().removeValueChangeListener(listener);
	}

	@Override
	public void removeListener(ValueChangeListener listener) {
		getSelect().removeValueChangeListener(listener);
	}

	@Override
	public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		getSelect().valueChange(event);
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		getSelect().setPropertyDataSource(newDataSource);
	}

	@Override
	public Property getPropertyDataSource() {
		return getSelect().getPropertyDataSource();
	}

	@Override
	public int getTabIndex() {
		return getSelect().getTabIndex();
	}

	@Override
	public void setTabIndex(int tabIndex) {
		getSelect().setTabIndex(tabIndex);
	}

	@Override
	public boolean isRequired() {
		return getSelect().isRequired();
	}

	@Override
	public void setRequired(boolean required) {
		getSelect().setRequired(required);
	}

	@Override
	public void setRequiredError(String requiredMessage) {
		getSelect().setRequiredError(requiredMessage);
	}

	@Override
	public String getRequiredError() {
		return getSelect().getRequiredError();
	}

	public CaptionGenerator<T> getCaptionGenerator() {
		return captionGenerator;
	}

	public void setCaptionGenerator(CaptionGenerator<T> captionGenerator) {
		this.captionGenerator = captionGenerator;
	}

	@SuppressWarnings("deprecation")
	public void setOptions(Collection<T> listAllStyles) {
		getSelect().setContainerDataSource(new BeanItemContainer<T>(listAllStyles));
	}

}