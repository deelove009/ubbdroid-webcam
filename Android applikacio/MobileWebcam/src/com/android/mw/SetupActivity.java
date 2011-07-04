package com.android.mw;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetupActivity extends Activity{
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button button;
	private TextView textview;
	private EditText sadress;
	private EditText port;
//private DataHandler dh;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        setContentView(R.layout.setup);
        sadress = (EditText) findViewById(R.id.editText1);
        port= (EditText) findViewById(R.id.editText2);
		//dh = new DataHandler(this);
		//String s= dh.loadOptions();
		//String[] options = dh.loadOptions().split(":");
       // sadress.setText(options[0]);
       // port.setText(options[1]);
        
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//APPLY		
				//dh.saveOptions(sadress.getText()+":"+port.getText());
				SetupActivity.this.finish();
			}
		});
		
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//CANCEL	
				SetupActivity.this.finish();
			}
		});		
	}	
	
	/*@Override
	public void onBackPressed() {
		SetupActivity.this.finish();
	}*/
}
