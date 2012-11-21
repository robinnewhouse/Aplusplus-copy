package ca.ubc.cpsc310.parkme.client.services.user;

import ca.ubc.cpsc310.parkme.client.Criteria;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

	void setUserInfo(UserInfoClient userInfoClient, AsyncCallback<Void> callback);

	void getUserInfo(AsyncCallback<UserInfoClient> callback);

	void setCriteria(double radius, double maxPrice, double minTime,
			UserInfoClient userInfo, AsyncCallback<Void> callback);

	void getType(UserInfoClient userInfo, AsyncCallback<String> callback);

	void setType(String type, UserInfoClient userInfo,
			AsyncCallback<Void> callback);

	void getAvgCriteria(AsyncCallback<Criteria> callback);

}
