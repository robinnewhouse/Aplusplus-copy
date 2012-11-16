package ca.ubc.cpsc310.parkme.server;

import java.util.List;

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

	public void setCriteria(double radius, double maxPrice, double minTime, UserInfoClient userInfo) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			String name = userInfo.getUsername();
			UserInfo user = pm.getObjectById(UserInfo.class, name);
			if (userInfo == null) {
				setUserInfo(userInfo);
			}
			else {
				user.setMaxPrice(maxPrice);
				user.setMinTime(minTime);
				user.setRadius(radius);
			}
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
			List<UserInfo> userInfoList = (List<UserInfo>) q.execute(getUser().getNickname());

			if (userInfoList.size() == 0) {
				System.out.println("No UserInfo found yet");
				userInfoClient = new UserInfoClient(getUser().getNickname(), "driver", 5, 0, 0);
				setUserInfo(userInfoClient);
			}

			else {
				UserInfo userInfo = userInfoList.get(0);
				System.out.println("UserInfo found in database");
				String name = userInfo.getUsername();
				String userType = userInfo.getUserType();
				double maxPrice = userInfo.getMaxPrice();
				double minTime = userInfo.getMinTime();
				double maxRadius = userInfo.getRadius();
				userInfoClient = new UserInfoClient(name, userType, maxPrice, minTime, maxRadius);
			}
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
