package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.SearchHistoryService;
import ca.ubc.cpsc310.parkme.client.NotLoggedInException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class SearchHistoryServiceImpl extends RemoteServiceServlet implements SearchHistoryService {

	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void addSearchString(String str) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		//TEMP TO CHECK IF RPC IS WORKING
		/*
		try {
			pm.makePersistent(new SearchString(getUser(), str));
		} finally {
			pm.close();
		}
		*/
	}

	public ArrayList<String> getHist() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		ArrayList<String> searchStrings=new ArrayList<String>();;
		//TEMP TO CHECK IF RPC IS WORKING
		/*
		try {
			Query q = pm.newQuery(SearchString.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			ArrayList<SearchString> pHist = (ArrayList<SearchString>) q.execute(getUser());
			for (int i = 0; i < pHist.size(); i++) {	
				searchStrings.add(pHist.get(i).getSearchString());
			}
		} finally {
			pm.close();
		}
		*/
		//TEMP TO CHECK IF RPC IS WORKING
		searchStrings.add("Robson and Georgia");
		searchStrings.add("Macdonald and Broadway");
		searchStrings.add("V6E 1V9");
		
		return searchStrings;
	}
	

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

	
	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

}
