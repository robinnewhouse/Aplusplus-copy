package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.NotLoggedInException;
import ca.ubc.cpsc310.parkme.client.services.history.SearchHistoryService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class SearchHistoryServiceImpl extends RemoteServiceServlet implements SearchHistoryService {

	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void addSearchString(String str) throws NotLoggedInException {
		System.out.println("Starting SearchHistoryServiceImpl.addSearchString()");
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			pm.makePersistent(new SearchRecord(getUser(),str));
		} finally {
			pm.close();
		}		
		System.out.println("Finishing SearchHistoryServiceImpl.addSearchString()");
	}

	public List<String> getHist() throws NotLoggedInException {
		System.out.println("Starting SearchHistoryServiceImpl.getHist()");
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		List<String> searchStrings = new ArrayList<String>();
		try {
			Query q = pm.newQuery(SearchRecord.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			List<SearchRecord> pHist = (List<SearchRecord>) q.execute(getUser());
			for(SearchRecord rec: pHist)
				searchStrings.add(rec.getSearchString());			
		} finally {
			pm.close();
		}
		//*/
		/**
		//TEMP TO CHECK IF RPC IS WORKING
		searchStrings.add("Robson and Georgia");
		searchStrings.add("Macdonald and Broadway");
		searchStrings.add("V6E 1V9");
		**/
		System.out.println("Finishing SearchHistoryServiceImpl.getHist()");
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

	@Override
	public void clear() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Starting SearchHistoryServiceImpl.clear()");
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			Query q = pm.newQuery(SearchRecord.class);

			List<SearchRecord> fullHistory = (List<SearchRecord>) q.execute();
			for (SearchRecord histString : fullHistory) {
					pm.deletePersistent(histString);
					System.out.println("Deleted " + histString.getSearchString() + " from favorites.");
			}
		} 
		catch(Exception exception) {
			throw exception;
		}
		finally {
			pm.close();
		}
		System.out.println("Finishing SearchHistoryServiceImpl.clear()");
	}

}
