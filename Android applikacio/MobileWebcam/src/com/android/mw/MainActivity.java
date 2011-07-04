package com.android.mw;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements
		SurfaceHolder.Callback {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	Button myButton;
	MediaRecorder mediaRecorder;
	SurfaceHolder surfaceHolder;
	boolean recording;
	ConnectionLayer cl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setRequestedOrientation(
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		recording = false;
		cl = new ConnectionLayer(this);
		try{
			cl.open();
			cl.send(new Integer(4));
			//cl.close();
		}catch (Exception e) {
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Conection");
			alertDialog.setMessage(e.getMessage());
			alertDialog.show();
		}

		mediaRecorder = new MediaRecorder();
		initMediaRecorder();
		mediaRecorder.setOutputFile(cl.getOutput());
		
		setContentView(R.layout.main);

		SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = myVideoView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		myButton = (Button) findViewById(R.id.mybutton);
		myButton.setOnClickListener(myButtonOnClickListener);
	}

	private Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (recording) {
				mediaRecorder.stop();
				try {
					cl.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//mediaRecorder.release();				
				myButton.setText("START");
				
			} else {
				mediaRecorder.start();
				recording = true;
				myButton.setText("STOP");
			}
		}
	};

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		prepareMediaRecorder();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
	}

	private void initMediaRecorder() {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		CamcorderProfile camcorderProfile_HQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		mediaRecorder.setProfile(camcorderProfile_HQ);
		//mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		//mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		//mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);	    
		//mediaRecorder.setVideoSize(400, 280);
	}

	private void prepareMediaRecorder() {
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onDistroy() {
		//this.finish();
		try{
			mediaRecorder.release();
			cl.close();
		}catch (Exception e) {
			Log.e(TAG, "nem zarja le a ConnetionLayert");
		}
	}
}