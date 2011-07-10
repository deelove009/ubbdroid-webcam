package com.android.mw;

import android.os.AsyncTask;

/**
 * Asynchron task for doing the server connection in the background, so that the
 * UI thread doesn't get blocked.
 * 
 */
public class AuthenticatorTask extends AsyncTask<Void, Void, Void> {
	public static final int AUTH_OK = 0;
	public static final int AUTH_BAD = 1;
	public static final int CONNECTION_ERROR = 2;

	private ConnectionLayer connectionLayer;
	private String user;
	private String password;
	private LoginActivity la;
	private int status;
	private Exception connException;

	public AuthenticatorTask(ConnectionLayer connectionLayer, String user,
			String password, LoginActivity la) {
		this.connectionLayer = connectionLayer;
		this.user = user;
		this.password = password;
		this.la = la;
	}

	/**
	 * The time-consuming background task, which runs on a separate thread.
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			connectionLayer.open();
			connectionLayer.send(new Integer(1));
			connectionLayer.send(user);
			connectionLayer.send(password);// TODO Auto-generated method stub

			if (((Integer) connectionLayer.read()).equals(2)) {
				status = AUTH_OK;
			} else {
				status = AUTH_BAD;
			}

			connectionLayer.close();
		} catch (Exception e) {
			status = CONNECTION_ERROR;
			connException = e;
		}

		return null;
	}

	/**
	 * Notifies the LoginActivity of the result, running on the UI thread.
	 */
	@Override
	protected void onPostExecute(Void result) {
		la.dismissDialog(LoginActivity.DIALOG_LOADING);
		switch (status) {
		case AUTH_OK:
			la.onAuthOk();
			break;
		case AUTH_BAD:
			la.onAuthBad();
			break;
		default:
			la.onConnectionError(connException);
		}
	}
}
