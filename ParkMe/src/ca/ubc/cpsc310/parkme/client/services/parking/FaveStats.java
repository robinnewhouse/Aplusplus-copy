package ca.ubc.cpsc310.parkme.client.services.parking;

import java.io.Serializable;

public class FaveStats implements Serializable{
	
	String parkingID;
	Long count;
	
	public FaveStats() {
		
	}
	
	public FaveStats(String parkingID, Long count) {
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
