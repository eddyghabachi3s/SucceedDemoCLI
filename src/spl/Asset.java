package spl;

import java.util.HashMap;

public class Asset {

	private String path;
	private HashMap<String, AssetInstance> assetInstances;
	private HashMap<String, Product> products;
	private int assetJunctionDegree;
	private double assetCorrelationDegree;
	private double assetVariabilityRatio;
	private HashMap<Feature, FeatureToAssetCorrelation> correlations;
	
	
	public Asset(String path) {
		super();
		this.path = path;
		this.assetInstances = new HashMap<String, AssetInstance>();
		this.products = new HashMap<String, Product>();
		this.correlations = new HashMap<Feature, FeatureToAssetCorrelation>();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public HashMap<String, AssetInstance> getAssetInstances() {
		return assetInstances;
	}

	public void setAssetInstances(HashMap<String, AssetInstance> assetInstances) {
		this.assetInstances = assetInstances;
	}

	public HashMap<String, Product> getProducts() {
		return products;
	}

	public void setProducts(HashMap<String, Product> products) {
		this.products = products;
	}

	public int getAssetJunctionDegree() {
		return assetJunctionDegree;
	}

	public void setAssetJunctionDegree(int assetJunctionDegree) {
		this.assetJunctionDegree = assetJunctionDegree;
	}

	public double getAssetCorrelationDegree() {
		return assetCorrelationDegree;
	}

	public void setAssetCorrelationDegree(double assetCorrelationDegree) {
		this.assetCorrelationDegree = assetCorrelationDegree;
	}

	public double getAssetVariabilityRatio() {
		return assetVariabilityRatio;
	}

	public void setAssetVariabilityRatio(double assetVariabilityRatio) {
		this.assetVariabilityRatio = assetVariabilityRatio;
	}

	public HashMap<Feature, FeatureToAssetCorrelation> getCorrelations() {
		return correlations;
	}

	public void setCorrelations(HashMap<Feature, FeatureToAssetCorrelation> correlations) {
		this.correlations = correlations;
	}
	
	
	
}
