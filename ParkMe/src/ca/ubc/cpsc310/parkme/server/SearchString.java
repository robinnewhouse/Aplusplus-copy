package ca.ubc.cpsc310.parkme.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SearchString {
	@PrimaryKey
	@Persistent
	String searchString;
	@Persistent
	User user;
	
	public SearchString(User user, String searchString){
		this.user=user;
		this.searchString=searchString;		
	}
	
	public String getSearchString() {
		return searchString;
	}
}
