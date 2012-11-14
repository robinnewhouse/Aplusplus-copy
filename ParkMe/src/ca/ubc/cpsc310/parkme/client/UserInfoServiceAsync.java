package ca.ubc.cpsc310.parkme.client;

import javax.jdo.PersistenceManager;

import ca.ubc.cpsc310.parkme.server.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

	void setUserInfo(String usertype, double maxprice, double mintime, double maxradius, AsyncCallback<Void> callback);

	void getUserInfo(AsyncCallback<UserInfo> callback);

}
