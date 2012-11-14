package ca.ubc.cpsc310.parkme.server;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserInfo {
	
	@PrimaryKey
	@Persistent
	private String username;
	@Persistent
	private String userType;
	@Persistent
	private double maxRadius;
	@Persistent
	private double maxPrice;
	@Persistent
	private double minTime;
	
	
	
	public UserInfo(String username, String usertype, double maxPrice,
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
