package com.android.mw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.android.mw.R;
//import com.android.service.ConnectionService;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private static final String TAG = LoginActivity.class.getSimpleName();
	private Button button;
	private CheckBox checkbox;
	private EditText user;
	private EditText pass;
	private LoginActivity self;
	private Activity activity;

	private ConnectionLayer connectionLayer;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.loginpage);
		
		connectionLayer = new ConnectionLayer(this);

		self = this;
		//checkbox = (CheckBox) findViewById(R.id.checkBox1);
		user = (EditText) findViewById(R.id.editText1);
		pass = (EditText) findViewById(R.id.editText2);

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			/** Logging in and error handling. */
			public void onClick(View v) {
				
				try {
					
				 connectionLayer.open();
					connectionLayer.send(new Integer(1));
					String user1=user.getText().toString();
					connectionLayer.send(user1);
					//connectionLayer.send(user.getText().toString());
					connectionLayer.send(pass.getText().toString());					
					if (((Integer) connectionLayer.read()).equals(2)) {
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						intent = intent.putExtra("user", user1);
						startActivity(intent);
						//startActivity(intent.putExtra("user", user1));
					} else {
						AlertDialog alertDialog;
						alertDialog = new AlertDialog.Builder(self).create();
						alertDialog.setTitle("Authentification failed");
						alertDialog.setMessage("Bad username or password!");
						alertDialog.show();
					}
					connectionLayer.close();
					
					
				} catch (Exception e) {
					Log.d(TAG, "connection error");
					AlertDialog alertDialog;
					alertDialog = new AlertDialog.Builder(self).create();
					alertDialog.setTitle("Conection");
					alertDialog.setMessage("Server is unreachable!");
					alertDialog.show();
				}
				/*Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);*/
			}
		});

		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			/** Opens the Option activity. */
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						SetupActivity.class);
				startActivity(intent);
			}
		});
	}
	/** The Distroy method */
	public void onDistroy() {
		try {
			connectionLayer.open();
			connectionLayer.send(new Integer(6));
			connectionLayer.close();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}
}