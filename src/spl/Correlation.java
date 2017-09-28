package spl;

public class Correlation {

	private Feature feature;
	private double correlationDegree;

	public Correlation(Feature feature) {
		super();
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public double getCorrelationDegree() {
		return correlationDegree;
	}

	public void setCorrelationDegree(double correlationDegree) {
		this.correlationDegree = correlationDegree;
	}
	
	
}
