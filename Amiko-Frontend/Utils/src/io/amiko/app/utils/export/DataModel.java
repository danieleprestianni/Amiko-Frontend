package io.amiko.app.utils.export;

public class DataModel{
	private int value;
	private String name;
	private String surname;
	
	public DataModel(){
		
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	@Override
	public String toString(){
		return this.getName() + "-" + getValue();
	}
	
}
