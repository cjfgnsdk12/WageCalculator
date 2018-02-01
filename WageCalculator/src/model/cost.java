package model;

import javafx.beans.property.StringProperty;

public class cost {
	private StringProperty name;
	private StringProperty cost;
	
	public cost(StringProperty name, StringProperty cost) {
		super();
		this.name = name;
		this.cost = cost;
	}
	
	public final StringProperty nameProperty() {
		return this.name;
	}
	
	public final String getName() {
		return this.nameProperty().get();
	}
	
	public final void setName(final String name) {
		this.nameProperty().set(name);
	}
	
	public final StringProperty costProperty() {
		return this.cost;
	}
	
	public final String getCost() {
		return this.costProperty().get();
	}
	
	public final void setCost(final String cost) {
		this.costProperty().set(cost);
	}
	
/*	private String name;
	private String cost;
	
	
	public cost(String name, String cost) {
		super();
		this.name = name;
		this.cost = cost;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCost() {
		return cost;
	}
	public void setCost(String cost) {
		this.cost = cost;
	}
	*/
	
	
}
