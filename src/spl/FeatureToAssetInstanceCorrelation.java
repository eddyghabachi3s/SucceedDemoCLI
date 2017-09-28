package spl;

public class FeatureToAssetInstanceCorrelation extends Correlation{

	private AssetInstance assetInstance;
	private double featureToAssetCorrelationDegree;
	private double correlationDegree;
	


	public FeatureToAssetInstanceCorrelation(Feature feature, AssetInstance assetInstance,
			double featureToAssetCorrelationDegree) {
		super(feature);
		this.assetInstance = assetInstance;
		this.featureToAssetCorrelationDegree = featureToAssetCorrelationDegree;
	}

	public AssetInstance getAssetInstance() {
		return assetInstance;
	}

	public void setAssetInstance(AssetInstance assetInstance) {
		this.assetInstance = assetInstance;
	}

	public double getFeatureToAssetCorrelationDegree() {
		return featureToAssetCorrelationDegree;
	}

	public void setFeatureToAssetCorrelationDegree(double featureToAssetCorrelationDegree) {
		this.featureToAssetCorrelationDegree = featureToAssetCorrelationDegree;
	}

	public double getCorrelationDegree() {
		return correlationDegree;
	}

	public void setCorrelationDegree(double correlationDegree) {
		this.correlationDegree = correlationDegree;
	}


	
	
}
