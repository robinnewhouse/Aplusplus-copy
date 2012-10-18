package ca.ubc.cpsc310.parkme.client;

import java.io.Serializable;

public class Criteria implements Serializable {
	private double radius;
	private double maxPrice;
	private double minTime;

	public Criteria () {
		
	}
	
	public Criteria (double radius, double maxPrice, double minTime) {
		this.radius = radius;
		this.maxPrice = maxPrice;
		this.minTime = minTime;
	}
	
	public double getRadius() {
		return radius;
	}
	public void setRadius(double radius) {
		this.radius = radius;
	}
	public double getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public double getMinTime() {
		return minTime;
	}
	public void setMinTime(double minTime) {
		this.minTime = minTime;
	}
}
