package ca.ubc.cpsc310.parkme.client.services.user;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userinfo")
public interface UserInfoService extends RemoteService {

	void setUserInfo(UserInfoClient userInfoClient) throws NotLoggedInException;

	String getType(UserInfoClient userInfo) throws NotLoggedInException;

	void setType(String type, UserInfoClient userInfo)
			throws NotLoggedInException;

	UserInfoClient getUserInfo() throws NotLoggedInException;

	void setCriteria(double radius, double maxPrice, double minTime,
			UserInfoClient userInfo) throws NotLoggedInException;

	Criteria getAvgCriteria();
}
