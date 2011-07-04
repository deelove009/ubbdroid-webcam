package com.android.mw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import android.app.Activity;
import android.util.Log;

public class DataHandler {
	private static final String TAG = DataHandler.class.getSimpleName();
	
	private Activity activity;
	private String fileName;
	
	public DataHandler(Activity activity){
		this.activity = activity;
		fileName = "data/data/android.mw/op2.cfg";
	}

	public void saveOptions(String s) {
	 
	    try {
	    	@SuppressWarnings("static-access")
			FileOutputStream fOut = activity.openFileOutput(fileName,activity.MODE_WORLD_WRITEABLE);
					OutputStreamWriter osw = new OutputStreamWriter(fOut);
					osw.write(s);
					osw.flush();
					osw.close();
	    } catch (Exception e) {
	    	Log.e(TAG,"Error:"+e.getMessage());
	    }
	}
	
	public String loadOptions() {
		//String fileName = "//sdcard/MobileWebcamFiles/op.cfg";
		try {
			FileInputStream fIn = activity.openFileInput(fileName);
			InputStreamReader isr = new InputStreamReader(fIn);
			BufferedReader r = new BufferedReader(isr);
			return r.readLine();
		}catch (Exception e){
			Log.e(TAG,"Error:"+e.getMessage());
		}
		return "";
	}
}