package com.android.mw;

// TODO: Ezt ki kell javitani, mert igy teljesen folosleges!
public class testConnectionLayer {
	
	private String address = "192.168.0.1";
	private int port = 4096;
	private ConnectionLayer test;
	
	
	protected void SetUp() {
		//test = new ConnectionLayer(this);
		test.setPort(port);
		test.setServerAddress(address);	
	}
	
	public  void main(String args[]) {
	    //junit.textui.TestRunner.run(TestCourse.class);
		SetUp();
		if (test.getPort() != port) {
			 System.out.println(
					  "*** Error in port setting!");
		}
		if (test.getServerAddress() != address) {
			 System.out.println(
					  "*** Error in server address settings!");
		}
	}
	
}
