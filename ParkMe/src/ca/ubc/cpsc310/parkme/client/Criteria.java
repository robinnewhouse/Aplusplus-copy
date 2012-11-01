package ca.ubc.cpsc310.parkme.client;

import java.io.Serializable;

import com.google.maps.gwt.client.LatLng;

public class Criteria implements Serializable {
	private double radius;
	private double maxPrice;
	private double minTime;
	private double ctrLat;
	private double ctrLng;

	public Criteria () {
		
	}
	
	public Criteria (double radius, double maxPrice, double minTime, double ctrLat, double ctrLng) {
		this.radius = radius;
		this.maxPrice = maxPrice;
		this.minTime = minTime;
		this.ctrLat = ctrLat;
		this.ctrLng = ctrLng;
	}
	
	public double getCtrLat() {
		return ctrLat;
	}

	public void setCtrLat(double ctrLat) {
		this.ctrLat = ctrLat;
	}

	public double getCtrLng() {
		return ctrLng;
	}

	public void setCtrLng(double ctrLng) {
		this.ctrLng = ctrLng;
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
