package ca.ubc.cpsc310.parkme.client;

import java.io.Serializable;

import javax.jdo.annotations.Persistent;

public class UserInfoClient implements Serializable {

	private String username;
	private String userType;
	private double maxRadius;
	private double maxPrice;
	private double minTime;

	public UserInfoClient(String username, String usertype, double maxPrice,
			double minTime, double maxRadius) {
		this.username = username;
		this.userType = usertype;
		this.maxPrice = maxPrice;
		this.minTime = minTime;
		this.maxRadius = maxRadius;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public double getRadius() {
		return maxRadius;
	}
	public void setRadius(double radius) {
		this.maxRadius = radius;
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
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

}
