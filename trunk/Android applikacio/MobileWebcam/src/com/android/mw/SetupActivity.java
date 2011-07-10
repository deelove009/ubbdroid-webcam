package com.android.mw;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetupActivity extends Activity {
//	private static final String TAG = SetupActivity.class.getSimpleName();
	private Button button;
	private EditText sadress;
	private EditText port;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		setContentView(R.layout.setup);
		sadress = (EditText) findViewById(R.id.editText1);
		port = (EditText) findViewById(R.id.editText2);

		// Fill the values of the EditText widgets.
		if (savedInstanceState == null) {
			// If the Activity is being started by another activity
			Intent intent = getIntent();
			sadress.setText(intent.getStringExtra(C.KEY_SERVER_ADDR));
			port.setText(String.valueOf(intent
					.getIntExtra(C.KEY_SERVER_PORT, 0)));
		} else {
			// TODO: handle saving instance state
		}

		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			/** Close the activity with saving changes. */
			public void onClick(View v) {
				// APPLY
				// Reply with an OK status code and the data from the EditText
				// fields.
				Intent data = new Intent();
				data.putExtra(C.KEY_SERVER_ADDR, sadress.getText().toString());
				data.putExtra(C.KEY_SERVER_PORT,
						Integer.parseInt(port.getText().toString()));
				setResult(RESULT_OK, data);
				SetupActivity.this.finish();
			}
		});

		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			/** Close the activity without saving changes. */
			public void onClick(View v) {
				// CANCEL
				// Reply with a CANCELED result code.
				setResult(RESULT_CANCELED);
				SetupActivity.this.finish();
			}
		});
	}
}