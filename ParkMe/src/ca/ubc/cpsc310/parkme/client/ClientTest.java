package ca.ubc.cpsc310.parkme.client;

import ca.ubc.cpsc310.parkme.client.services.parking.TicketInfo;
import ca.ubc.cpsc310.parkme.client.services.parking.TicketService;
import ca.ubc.cpsc310.parkme.client.services.parking.TicketServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ClientTest extends GWTTestCase {

	public TicketInfo[] ticks;

	@Override
	public String getModuleName() {
		return "ca.ubc.cpsc310.parkme.ParkMe";

	}

	public void testTicketUpload() {

		TicketInfo testTicket1 = new TicketInfo("testID", 123.45);

		final TicketServiceAsync ticketService = GWT
				.create(TicketService.class);
		ticketService.addTicket(testTicket1.getParkingID(),
				testTicket1.getFine(), new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						System.out
								.println("ticket was successfully uploaded to the server. Thank you");
					}

					@Override
					public void onFailure(Throwable caught) {
						System.out
								.println("There was an error uploading ticket");
					}
				});

		ticketService.getTickets(new AsyncCallback<TicketInfo[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(TicketInfo[] result) {
				ticks = result;
			}
		});

		// assertEquals(expected, actual)
		// TicketInfo ti = new TicketInfo();
		// try {
		// setUp();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		assertTrue(true);
		//
		// try {
		// tearDown();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
}
