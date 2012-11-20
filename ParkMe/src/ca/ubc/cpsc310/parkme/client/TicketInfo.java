package ca.ubc.cpsc310.parkme.client;

import java.io.Serializable;

public class TicketInfo implements Serializable {

	private String parkingID;
	private Double fine;
	
	public TicketInfo(String parkingID, Double fine) {
		this.parkingID = parkingID;
		this.fine = fine;
	}

	public String getParkingID() {
		return parkingID;
	}

	public void setParkingID(String parkingID) {
		this.parkingID = parkingID;
	}

	public Double getFine() {
		return fine;
	}

	public void setFine(Double fine) {
		this.fine = fine;
	}
	
}
