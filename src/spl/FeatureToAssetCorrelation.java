package spl;

public class FeatureToAssetCorrelation extends Correlation{

	private Asset asset;
	private CorrelationType correlationType;
	private int correlationTypeDegree;
		
	public FeatureToAssetCorrelation(Feature feature, Asset asset, CorrelationType correlationType,
			int correlationTypeDegree) {
		super(feature);
		this.asset = asset;
		this.correlationType = correlationType;
		this.correlationTypeDegree = correlationTypeDegree;
	}
	public Asset getAsset() {
		return asset;
	}
	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	public CorrelationType getCorrelationType() {
		return correlationType;
	}
	public void setCorrelationType(CorrelationType correlationType) {
		this.correlationType = correlationType;
	}

	public int getCorrelationTypeDegree() {
		return correlationTypeDegree;
	}

	public void setCorrelationTypeDegree(int correlationTypeDegree) {
		this.correlationTypeDegree = correlationTypeDegree;
	}
	
	
	
}
