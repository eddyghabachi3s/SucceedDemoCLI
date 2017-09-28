package spl;

import java.util.ArrayList;

public class Action {
	
	private ActionType type;
	private AssetInstance assetInstance;
	private double cost;
	private ArrayList<Feature> featuresToRetain;
	private ArrayList<Feature> featuresToRemove;
	private ArrayList<Product> products;
	
	public Action(ActionType type, AssetInstance assetInstance) {
		super();
		this.type = type;
		this.assetInstance = assetInstance;
	}
	public ActionType getType() {
		return type;
	}
	public void setType(ActionType type) {
		this.type = type;
	}
	public AssetInstance getAssetInstance() {
		return assetInstance;
	}
	public void setAssetInstance(AssetInstance assetInstance) {
		this.assetInstance = assetInstance;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public ArrayList<Feature> getFeaturesToRetain() {
		return featuresToRetain;
	}
	public void setFeaturesToRetain(ArrayList<Feature> featuresToRetain) {
		this.featuresToRetain = featuresToRetain;
	}
	public ArrayList<Feature> getFeaturesToRemove() {
		return featuresToRemove;
	}
	public void setFeaturesToRemove(ArrayList<Feature> featuresToRemove) {
		this.featuresToRemove = featuresToRemove;
	}
	public ArrayList<Product> getProducts() {
		return products;
	}
	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
	
	

}
