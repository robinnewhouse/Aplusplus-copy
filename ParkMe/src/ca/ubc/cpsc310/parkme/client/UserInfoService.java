package ca.ubc.cpsc310.parkme.client;

import javax.jdo.PersistenceManager;
import ca.ubc.cpsc310.parkme.server.UserInfo;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userinfo")
public interface UserInfoService extends RemoteService{
	
	void setUserInfo(String usertype, double maxprice, 
			double mintime, double maxradius) throws NotLoggedInException;

	UserInfo getUserInfo() throws NotLoggedInException;
}
