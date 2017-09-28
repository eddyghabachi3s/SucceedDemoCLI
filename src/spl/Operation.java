package spl;

import java.util.ArrayList;

public class Operation {

	private String description;
	private double cost;
	private ArrayList<Action> actions;
	
	public Operation() {
		super();
		actions = new ArrayList<Action>();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public ArrayList<Action> getActions() {
		return actions;
	}
	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}
	
	
	
}
