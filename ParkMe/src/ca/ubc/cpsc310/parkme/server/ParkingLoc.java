package ca.ubc.cpsc310.parkme.server;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

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

}