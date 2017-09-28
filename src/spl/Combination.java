package spl;

import java.util.ArrayList;
import java.util.HashMap;

public class Combination {

	private ArrayList<Product> products = new ArrayList<Product>();
	private String name;
	private HashMap<Asset, ArrayList<Operation>> operations = new HashMap<Asset, ArrayList<Operation>>();
	private double cost;

	

	public Combination(ArrayList<Product> products, String name) {
		super();
		this.products = products;
		this.name = name;
	}



	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public HashMap<Asset, ArrayList<Operation>> getOperations() {
		return operations;
	}

	public void setOperations(HashMap<Asset, ArrayList<Operation>> operations) {
		this.operations = operations;
	}



	public double getCost() {
		return cost;
	}



	public void setCost(double cost) {
		this.cost = cost;
	}
	
	
	
}
