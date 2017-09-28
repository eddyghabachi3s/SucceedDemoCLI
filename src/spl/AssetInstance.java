package spl;

import java.util.ArrayList;
import java.util.HashMap;

public class AssetInstance {

	private int instanceNo;
	private String sha5;
	private Asset asset;
	private ArrayList<Product> products;
	private double assetInstanceCorrelationDegree;
	private HashMap<Feature, FeatureToAssetInstanceCorrelation> correlations;
	
	public AssetInstance(int instanceNo, String sha5, Asset asset) {
		super();
		this.instanceNo = instanceNo;
		this.sha5 = sha5;
		this.asset = asset;
		this.products = new ArrayList<Product>();
		this.correlations = new  HashMap<Feature, FeatureToAssetInstanceCorrelation>();
	}
	public int getInstanceNo() {
		return instanceNo;
	}
	public void setInstanceNo(int instanceNo) {
		this.instanceNo = instanceNo;
	}
	public String getSha5() {
		return sha5;
	}
	public void setSha5(String sha5) {
		this.sha5 = sha5;
	}
	public Asset getAsset() {
		return asset;
	}
	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	public ArrayList<Product> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	public double getAssetInstanceCorrelationDegree() {
		return assetInstanceCorrelationDegree;
	}
	public void setAssetInstanceCorrelationDegree(double assetInstanceCorrelationDegree) {
		this.assetInstanceCorrelationDegree = assetInstanceCorrelationDegree;
	}
	public HashMap<Feature, FeatureToAssetInstanceCorrelation> getCorrelations() {
		return correlations;
	}
	public void setCorrelations(HashMap<Feature, FeatureToAssetInstanceCorrelation> correlations) {
		this.correlations = correlations;
	}
	
	
	
}
