package com.android.mw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getSimpleName();
	public static final int DIALOG_LOADING = 0;
	
	private Button button;
	private CheckBox checkbox;
	private EditText user;
	private EditText pass;
	private LoginActivity self;

	// Default values. Will be overwritten if there are previous values in the
	// shared preferences.
	private String serverAddress = "192.168.1.100";
	private int port = 4567;

	private ConnectionLayer connectionLayer;
	private SharedPreferences settings;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.loginpage);

		user = (EditText) findViewById(R.id.editText1);
		pass = (EditText) findViewById(R.id.editText2);

		// Retrieve the saved values if any
		settings = getSharedPreferences(C.PREFS, MODE_PRIVATE);
		serverAddress = settings.getString(C.KEY_SERVER_ADDR, serverAddress);
		port = settings.getInt(C.KEY_SERVER_PORT, port);
		user.setText(settings.getString(C.KEY_USER, ""));
		pass.setText(settings.getString(C.KEY_PASS, ""));

		connectionLayer = new ConnectionLayer(this);
		connectionLayer.setServerAddress(serverAddress);
		connectionLayer.setPort(port);

		self = this;

		checkbox = (CheckBox) findViewById(R.id.checkBox1);
		// Set the value of the checkbox
		checkbox.setChecked(settings.getBoolean(C.KEY_SAVE, false));
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(C.KEY_SAVE, isChecked);
				if (!isChecked) {
					editor.remove(C.KEY_USER);
					editor.remove(C.KEY_PASS);
				}
				editor.commit();
			}
		});

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			/** Logging in and error handling. */
			public void onClick(View v) {
				showDialog(DIALOG_LOADING);
				// Saving user data if specified by the user
				String user1 = user.getText().toString();
				String passwd = pass.getText().toString();
				if (checkbox.isChecked()) {
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(C.KEY_USER, user1);
					editor.putString(C.KEY_PASS, passwd);
					editor.commit();
				}
				new AuthenticatorTask(connectionLayer, user1, passwd, LoginActivity.this).execute();
			}
		});

		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			/** Opens the Option activity. */
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						SetupActivity.class);

				// Send the values for the EditText fields
				intent.putExtra(C.KEY_SERVER_ADDR, serverAddress);
				intent.putExtra(C.KEY_SERVER_PORT, port);

				startActivityForResult(intent, C.REQUEST_SETUP);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case C.REQUEST_SETUP:
			if (resultCode == RESULT_OK) {
				// The Settings activity finished, update the values for the
				// server
				// address and port
				serverAddress = data.getStringExtra(C.KEY_SERVER_ADDR);
				port = data.getIntExtra(C.KEY_SERVER_PORT, port);

				connectionLayer.setServerAddress(serverAddress);
				connectionLayer.setPort(port);

				SharedPreferences.Editor editor = settings.edit();
				editor.putString(C.KEY_SERVER_ADDR, serverAddress);
				editor.putInt(C.KEY_SERVER_PORT, port);
				editor.commit();
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/** The Destroy method */
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			connectionLayer.open();
			connectionLayer.send(new Integer(6));
			connectionLayer.close();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LOADING:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("Connecting. Please wait...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			
			return dialog;
		}
		return null;
	}
	
	public void onAuthOk() {
		String user1 = user.getText().toString();
		Intent intent = new Intent(LoginActivity.this,
				MainActivity.class);
		intent.putExtra("user", user1)
				.putExtra(C.KEY_SERVER_ADDR, serverAddress)
				.putExtra(C.KEY_SERVER_PORT, port);
		startActivity(intent);
	}
	
	public void onAuthBad() {
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(self).create();
		alertDialog.setTitle("Authentification failed");
		alertDialog.setMessage("Bad username or password!");
		alertDialog.show();
	}
	
	public void onConnectionError(Exception e) {
		Log.d(TAG, "connection error: " + e.toString());
		AlertDialog alertDialog;
		alertDialog = new AlertDialog.Builder(self).create();
		alertDialog.setTitle("Conection");
		alertDialog.setMessage("Server is unreachable!");
		alertDialog.show();
	}
}