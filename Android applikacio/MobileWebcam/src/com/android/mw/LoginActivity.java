package com.android.mw;

import java.io.FileOutputStream;

import com.android.mw.R;
//import com.android.service.ConnectionService;
import android.app.Activity;
import android.app.AlertDialog;

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

	private ConnectionLayer connectionLayer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setContentView(R.layout.loginpage);

		connectionLayer = new ConnectionLayer(this);

		self = this;
		checkbox = (CheckBox) findViewById(R.id.checkBox1);
		user = (EditText) findViewById(R.id.editText1);
		pass = (EditText) findViewById(R.id.editText2);

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				try {
					
				 connectionLayer.open();
					connectionLayer.send(new Integer(1));
					connectionLayer.send(user.getText().toString());
					connectionLayer.send(pass.getText().toString());
					if (((Integer) connectionLayer.read()).equals(2)) {
						Intent intent = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(intent);
					} else {
						AlertDialog alertDialog;
						alertDialog = new AlertDialog.Builder(self).create();
						alertDialog.setTitle("Autentification faild");
						alertDialog.setMessage("Bad user or password!");
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
			}
		});

		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						SetupActivity.class);
				startActivity(intent);
			}
		});

	}
	
	
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
