package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.services.history.SearchHistoryOrganizer;
import ca.ubc.cpsc310.parkme.client.services.history.SearchHistoryService;
import ca.ubc.cpsc310.parkme.client.services.user.NotLoggedInException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class SearchHistoryServiceImpl extends RemoteServiceServlet implements SearchHistoryService {

	int MAX_HISTORY = SearchHistoryOrganizer.MAX_HISTORY;


	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void addSearchString(String str) throws NotLoggedInException {
		System.out.println("Starting SearchHistoryServiceImpl.addSearchString()");
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			pm.makePersistent(new SearchString(getUser(), str));
		} finally {
			pm.close();
		}		
		System.out.println("Finishing SearchHistoryServiceImpl.addSearchString()");
	}

	public ArrayList<String> getHist() throws NotLoggedInException {
		System.out.println(" in SearchHistoryServiceImpl.getHist() delete extras loop.  Max History is: "+MAX_HISTORY);
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		ArrayList<String> searchStrings=new ArrayList<String>();;
		try {
			Query q = pm.newQuery(SearchString.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			//		List<SearchString> pHist;
			List<SearchString> pHistResults = (List<SearchString>) q.execute(getUser());

			if (pHistResults.size() > MAX_HISTORY) {

				List<SearchString> pHistToDelete=pHistResults.subList(0, pHistResults.size()-MAX_HISTORY);
				List<SearchString> pHistToReturn=pHistResults.subList(pHistResults.size()-MAX_HISTORY, pHistResults.size());
				System.out.println(" in SearchHistoryServiceImpl.getHist() delete extras loop.  Max History is: "+MAX_HISTORY+
						", pHistResults.size() is: "+pHistResults.size()+", pHistToDelete.size() is: "+pHistToDelete.size()+", pHistToReturn.size() is: "+pHistToReturn.size());

				for(SearchString str: pHistToDelete){
					pm.deletePersistent(str);	

				}
				for (int i = 0; i < pHistToReturn.size(); i++) {	
					searchStrings.add(pHistToReturn.get(i).getSearchString());
				}
			}
			else {
				for (int i = 0; i < pHistResults.size(); i++) {	
					searchStrings.add(pHistResults.get(i).getSearchString());
				}
			}



			/*/THIS DOESN'T WORK - WILL HAVE TO DELETE
			while (pHist.size()>MAX_HISTORY) { //Delete excess History
				System.out.println(" in SearchHistoryServiceImpl.getHist() delete extras loop.  Max History is: "+MAX_HISTORY+
						", pHist.size() is: "+pHist.size());
				SearchString histString = pHist.get(0);
				if(histString==null){
					System.out.println("Null Object Returned in SearchHistoryServiceImpl.getHist()");
				}
				pm.deletePersistent(histString);
				pHist.remove(0);				
			}*/

		} 
		catch(Exception exception) {
			System.out.println("Exception in SearchHistoryServiceImpl.getHist: " + exception.getMessage());
			//throw exception;
		}
		finally {
			pm.close();
		}
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
		//System.out.println("Starting SearchHistoryServiceImpl.clear()");
		checkLoggedIn();
		PersistenceManager pm = PMF.getPersistenceManager();
		try {
			//System.out.println("At start of try");
			Query q = pm.newQuery(SearchString.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			//System.out.println("About to Query");
			List<SearchString> fullHistory = (List<SearchString>) q.execute(getUser());
			//System.out.println("About to Loop");			
			for (SearchString histString : fullHistory) {
				String toDelete = histString.getSearchString();
				//System.out.println("About to delete " + histString.getSearchString() + " from favorites.");
				if(histString==null){
					System.out.println("Null Object Returned in SearchHistoryServiceImpl.clear()");
				}
				else{
					pm.deletePersistent(histString);
				}
				//System.out.println("Deleted " + toDelete + " from favorites.");
			}
			//System.out.println("Finishing Try");
		} 
		catch(Exception exception) {
			System.out.println("Exception in SearchHistoryServiceImpl.clear: " + exception.getMessage());
			//throw exception;
		}
		finally {
			pm.close();
		}
		System.out.println("Finishing SearchHistoryServiceImpl.clear()");
	}

}
