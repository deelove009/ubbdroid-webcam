package com.android.mw;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Activity;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ConnectionLayer {
	private static final String TAG = ConnectionLayer.class.getSimpleName();
	
	
	private Socket s;
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private String serverAddress = "192.168.0.101";
    private int port = 4567;
        
    public ConnectionLayer(Activity activity){
    	s = new Socket();
      	/*serverAddress = options[0];
    	try{
    	port = Integer.parseInt(options[1]);
    	}catch (Exception e) {
    		Log.d(TAG, e.getMessage());
		}*/
    }
    
    public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public FileDescriptor getOutput(){
    	ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(s);
    	return pfd.getFileDescriptor();
    }
	
    
    public synchronized void send(Object s) throws IOException{
    	os.writeObject(s);
    	os.flush();
    	 Log.d(TAG, "Sending...");
    }   
    public synchronized Object read() throws Exception{
    	 Log.d(TAG, "Reading...");
    	return is.readObject();
    }  
    
    public void open () throws IOException {
    	//SocketAddress socketAddress = new InetSocketAddress(serverAddress, port);              
        //s.connect(socketAddress);
    	s = new Socket(serverAddress,port);
        
        os = new ObjectOutputStream(s.getOutputStream());
        is = new ObjectInputStream(s.getInputStream());
        Log.d(TAG, "Opening connection...");
    }
    
    public void close () throws IOException {
    	s.close();
    	 Log.d(TAG, "Closeing connection...");
    }

}
