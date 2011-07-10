package com.android.mw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

public class MainActivity extends Activity implements SurfaceHolder.Callback,
		OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	private CheckBox checkbox;
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private ConnectionLayer cl;
	private boolean ps = false;
	private boolean streaming = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);

		cl = new ConnectionLayer(this);
		Intent intent = getIntent();
		cl.setServerAddress(intent.getStringExtra(C.KEY_SERVER_ADDR));
		cl.setPort(intent.getIntExtra(C.KEY_SERVER_PORT, 0));

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		checkbox = (CheckBox) findViewById(R.id.checkBox1);
		mSurfaceView.setOnClickListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mCamera = Camera.open();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (ps) {
			mCamera.stopPreview();
		}
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();
		ps = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		try {
			cl.open();
			cl.send(new Integer(4));
			String user1 = getIntent().getExtras().getString("user");
			cl.send(user1);
			mCamera.setPreviewCallback(new PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					if (streaming) {
						try {
							boolean checked = true;
							checkbox.setChecked(checked);
							Camera.Parameters parameters = camera
									.getParameters();
							int width = parameters.getPreviewSize().width;
							int height = parameters.getPreviewSize().height;

							ByteArrayOutputStream outstr = new ByteArrayOutputStream();
							Rect rect = new Rect(0, 0, width, height);
							YuvImage yuvimage = new YuvImage(data,
									ImageFormat.NV21, width, height, null);
							yuvimage.compressToJpeg(rect, 50, outstr);
							cl.send(outstr.toByteArray());
						} catch (Exception e) {
							Log.d(TAG, e.toString());
						}
					} else {
						boolean checked = false;
						checkbox.setChecked(checked);
					}
				}
			});
		} catch (Exception e) {
			AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Conection");
			alertDialog.setMessage(e.getMessage());
			alertDialog.show();
			Log.d(TAG, e.getMessage());
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		streaming = false;
		ps = false;
		Log.d(TAG, "Closeing...");
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "Click " + !streaming);
		streaming = !streaming;
	}

	@Override
	public void onBackPressed() {
		streaming = false;
		ps = false;
		super.onBackPressed();
	}

	public void onDestroy() {
		super.onDestroy();
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
		}
		try {
			streaming = false;
			ps = false;
			cl.close();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}
}