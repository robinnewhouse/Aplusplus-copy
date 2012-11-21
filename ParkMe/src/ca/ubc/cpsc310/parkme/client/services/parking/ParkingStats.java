package ca.ubc.cpsc310.parkme.client.services.parking;

import java.io.Serializable;

public class ParkingStats implements Serializable{
	
	String parkingID;
	Long count;
	Double fine;
	
	public ParkingStats() {
		
	}
	public ParkingStats(String parkingID, Long count, Double fine) {
		this.parkingID = parkingID;
		this.count = count;
		this.fine = fine;
	}
	public Double getFine() {
		return fine;
	}
	public void setFine(Double fine) {
		this.fine = fine;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	public ParkingStats(String parkingID, Long count) {
		this.parkingID = parkingID;
		this.count = count;
	}

	public String getParkingID() {
		return parkingID;
	}

	public void setParkingID(String parkingID) {
		this.parkingID = parkingID;
	}


	public Long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
}
