package spl;

import java.util.HashMap;

public class Feature {
	
	private String name;
	private HashMap<String, Product> products;
	private int featurePropagationDegree;
	private double featureCorrelationDegree;
	
	public Feature(String name) {
		super();
		this.name = name;
		this.products = new HashMap<String, Product>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Product> getProducts() {
		return products;
	}

	public void setProducts(HashMap<String, Product> products) {
		this.products = products;
	}

	public int getFeaturePropagationDegree() {
		return featurePropagationDegree;
	}

	public void setFeaturePropagationDegree(int featurePropagationDegree) {
		this.featurePropagationDegree = featurePropagationDegree;
	}

	public double getFeatureCorrelationDegree() {
		return featureCorrelationDegree;
	}

	public void setFeatureCorrelationDegree(double featureCorrelationDegree) {
		this.featureCorrelationDegree = featureCorrelationDegree;
	}
	
}
