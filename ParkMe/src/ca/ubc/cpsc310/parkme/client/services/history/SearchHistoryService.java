package ca.ubc.cpsc310.parkme.client.services.history;

import java.util.ArrayList;

import ca.ubc.cpsc310.parkme.client.NotLoggedInException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("SearchHistoryService")
public interface SearchHistoryService extends RemoteService {
	public void addSearchString(String str) throws NotLoggedInException;
	public ArrayList<String> getHist() throws NotLoggedInException;
}
