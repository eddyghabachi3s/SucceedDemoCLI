package spl;

import java.util.HashMap;

public class FeatureModel {

	private HashMap<String, Feature> features;

	public FeatureModel() {
		super();
		features = new HashMap<String, Feature>();
	}

	public HashMap<String, Feature> getFeatures() {
		return features;
	}

	public void setFeatures(HashMap<String, Feature> features) {
		this.features = features;
	}
	
	
	
}
