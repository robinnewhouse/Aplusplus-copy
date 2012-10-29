package ca.ubc.cpsc310.parkme.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ParkingFave {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private User user;
	@Persistent
	private String parkingID;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getParkingID() {
		return parkingID;
	}

	public void setParkingID(String parkingID) {
		this.parkingID = parkingID;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Persistent
	private Date createDate;

	public ParkingFave(User user, String parkingID) {
		this.user = user;
		this.parkingID = parkingID;
		this.createDate = new Date();
	}
	
}
