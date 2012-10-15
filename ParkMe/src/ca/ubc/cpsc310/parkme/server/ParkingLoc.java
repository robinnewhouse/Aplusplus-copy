package ca.ubc.cpsc310.parkme.server;


import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


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
	private Long startLat;
	@Persistent
	private Long startLong;
	@Persistent
	private Long endLat;
	@Persistent
	private Long endLong;
	
	public ParkingLoc() {
		
		
		
	}
	
}
