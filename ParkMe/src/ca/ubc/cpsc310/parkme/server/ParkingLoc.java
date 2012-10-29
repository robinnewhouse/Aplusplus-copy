package ca.ubc.cpsc310.parkme.server;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import ca.ubc.cpsc310.parkme.client.ParkingLocation;

/**
 * 
 * @author Alyanna Uy
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ParkingLoc {

	@PrimaryKey
	@Persistent
	private String parkingID;
	@Persistent
	private Double price;
	@Persistent
	private Double limit;
	@Persistent
	private Double startLat;
	@Persistent
	private Double startLong;
	@Persistent
	private Double endLat;
	@Persistent
	private Double endLong;
	@Persistent
	private String street;
	@Persistent
	private String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public ParkingLoc() {

	}

	public String getParkingID() {
		return parkingID;
	}

	public void setParkingID(String parkingID) {
		this.parkingID = parkingID;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getLimit() {
		return limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	public Double getStartLat() {
		return startLat;
	}

	public void setStartLat(Double startLat) {
		this.startLat = startLat;
	}

	public Double getStartLong() {
		return startLong;
	}

	public void setStartLong(Double startLong) {
		this.startLong = startLong;
	}

	public Double getEndLat() {
		return endLat;
	}

	public void setEndLat(Double endLat) {
		this.endLat = endLat;
	}

	public Double getEndLong() {
		return endLong;
	}

	public void setEndLong(Double endLong) {
		this.endLong = endLong;
	}

	public ParkingLocation convertToPL() {
		return new ParkingLocation(parkingID, price,
				limit, startLat, startLong, endLat, endLong, street, color);
	}
	
}