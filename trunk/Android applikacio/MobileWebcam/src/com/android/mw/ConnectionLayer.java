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
    private String serverAddress = "192.168.1.100";
    private int port = 4567;
    
    /** Declares the s socket. */
    public ConnectionLayer(Activity activity){
    	s = new Socket();
      	/*serverAddress = options[0];
    	try{
    	port = Integer.parseInt(options[1]);
    	}catch (Exception e) {
    		Log.d(TAG, e.getMessage());
		}*/
    }
    
    /** Gets the server address. */
    public String getServerAddress() {
		return serverAddress;
	}

    /** Sets the server address. */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	/** Gets the port. */
	public int getPort() {
		return port;
	}

	/** Sets the port. */
	public void setPort(int port) {
		this.port = port;
	}

	/** Sets the file descriptor. */
	public FileDescriptor getOutput(){
    	ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(s);
    	return pfd.getFileDescriptor();
    }
    
	/** Synchronization 1. */
    public synchronized void send(Object s) throws IOException{
    	os.writeObject(s);
    	os.flush();
    	 Log.d(TAG, "Sending...");
    }
    
    /** Synchronization 2. */
    public synchronized Object read() throws Exception{
    	 Log.d(TAG, "Reading...");
    	return is.readObject();
    }  
    
    /** Opens the connection. */
    public void open () throws IOException {
    	//SocketAddress socketAddress = new InetSocketAddress(serverAddress, port);              
        //s.connect(socketAddress);
    	s = new Socket(serverAddress,port);
        
        os = new ObjectOutputStream(s.getOutputStream());
        is = new ObjectInputStream(s.getInputStream());
        Log.d(TAG, "Opening connection...");
    }
    
    /** Closes the connection. */
    public void close () throws IOException {
    	s.close();
    	 Log.d(TAG, "Closeing connection...");
    }
}