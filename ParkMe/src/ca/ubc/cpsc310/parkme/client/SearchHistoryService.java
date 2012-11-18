package ca.ubc.cpsc310.parkme.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchHistoryService extends RemoteService {
	public void addSearchString(String str) throws NotLoggedInException;
	public ArrayList<String> getHist() throws NotLoggedInException;
}
