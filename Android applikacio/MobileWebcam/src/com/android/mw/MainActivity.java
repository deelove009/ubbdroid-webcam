package com.android.mw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.w3c.dom.Text;

import com.android.mw.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.provider.MediaStore;
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
		super.onCreate(savedInstanceState);
		
		cl = new ConnectionLayer(this);
		Bundle extras = getIntent().getExtras();
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
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (ps) {
			mCamera.stopPreview();
		}
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);
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
		// TODO Auto-generated method stub
		mCamera = Camera.open();
		try {
			cl.open();
			cl.send(new Integer(4));
			String user1=getIntent().getExtras().getString("user");
			cl.send(user1);
			mCamera.setPreviewCallback(new PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					if (streaming) {
						try {							
							boolean checked = true;
							checkbox.setChecked(checked);
							Camera.Parameters parameters = camera.getParameters();

		                    int width = parameters.getPreviewSize().width;
		                    int height = parameters.getPreviewSize().height;

		                    ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		                    Rect rect = new Rect(0, 0, width, height); 
		                    YuvImage yuvimage=new YuvImage(data,ImageFormat.NV21,width,height,null);
		                    yuvimage.compressToJpeg(rect, 50, outstr);
		                    cl.send(outstr.toByteArray());							
						} catch (Exception e) {
							Log.d(TAG, e.getMessage());	
						}
					}else{
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
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		try {
			//cl.close();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
		ps = false;
		mCamera.release();
		Log.d(TAG, "Closeing...");
	}	
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "Click " + !streaming);
		streaming = !streaming;		
	}

	public void onDistroy() {
		try {
			streaming=false;
			cl.close();
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}
}