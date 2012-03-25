package org.vaadin.addon.smartfields;

import java.util.ArrayList;
import java.util.List;

public class TestBean {

	public enum TestEnum {
		FOO,BAR,CAR
	}
	
	private TestEnum testEnum;
	
	private List<String> listOfStrings = new ArrayList<String>();
	
	private List<Person> persons = new ArrayList<Person>();

	public TestEnum getTestEnum() {
		return testEnum;
	}

	public void setTestEnum(TestEnum testEnum) {
		this.testEnum = testEnum;
	}

	public List<String> getListOfStrings() {
		return listOfStrings;
	}

	public void setListOfStrings(List<String> listOfStrings) {
		this.listOfStrings = listOfStrings;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

}
