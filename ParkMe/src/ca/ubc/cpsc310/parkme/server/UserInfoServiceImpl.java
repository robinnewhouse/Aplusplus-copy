package ca.ubc.cpsc310.parkme.server;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.Fave;
import ca.ubc.cpsc310.parkme.client.NotLoggedInException;
import ca.ubc.cpsc310.parkme.client.UserInfoService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserInfoServiceImpl extends RemoteServiceServlet implements UserInfoService {

	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	@Override
	public void setUserInfo(String usertype, double maxprice,
			double mintime, double maxradius) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(new UserInfo(getUser().getNickname(), usertype, maxprice, mintime, maxradius));
		} finally {
			pm.close();
		}
	}
		

	@Override
	public UserInfo getUserInfo() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		UserInfo userInfo;
		try {
			Query q = pm.newQuery(UserInfo.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			userInfo = (UserInfo) q.execute(getUser().getNickname());

		} finally {
			pm.close();
		}
		return userInfo;
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
