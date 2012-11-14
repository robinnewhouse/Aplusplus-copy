package ca.ubc.cpsc310.parkme.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import ca.ubc.cpsc310.parkme.client.NotLoggedInException;
import ca.ubc.cpsc310.parkme.client.UserInfoClient;
import ca.ubc.cpsc310.parkme.client.UserInfoService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserInfoServiceImpl extends RemoteServiceServlet implements UserInfoService {

	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	@Override
	public void setUserInfo(UserInfoClient userInfoClient) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			String name = userInfoClient.getUsername();
			String userType = userInfoClient.getUserType();
			double maxPrice = userInfoClient.getMaxPrice();
			double minTime = userInfoClient.getMinTime();
			double maxRadius = userInfoClient.getRadius();
			pm.makePersistent(new UserInfo(name, userType, maxPrice, minTime, maxRadius));
		} finally {
			pm.close();
		}
	}
		

	@Override
	public UserInfoClient getUserInfo() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		UserInfoClient userInfoClient;
		try {
			Query q = pm.newQuery(UserInfo.class, "username == unameParam");
			q.declareParameters("String unameParam");
			q.setOrdering("createDate");
			UserInfo userInfo = (UserInfo) q.execute(getUser().getNickname());
			String name = userInfo.getUsername();
			String userType = userInfo.getUserType();
			double maxPrice = userInfo.getMaxPrice();
			double minTime = userInfo.getMinTime();
			double maxRadius = userInfo.getRadius();
			userInfoClient = new UserInfoClient(name, userType, maxPrice, minTime, maxRadius);

		} finally {
			pm.close();
		}
		return userInfoClient;
	}
	
	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

}
