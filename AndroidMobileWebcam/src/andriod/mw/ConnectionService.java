package andriod.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectionService extends Service {
	private static final String TAG = ConnectionService.class.getSimpleName();


    public Socket s;
    public ObjectOutputStream os;
    public ObjectInputStream is;
    @Override
    
    
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    private final IBinder myBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public ConnectionService getService() {
            return ConnectionService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        s = new Socket();
    }

    public void IsBoundable(){
        Toast.makeText(this,"Trying..", Toast.LENGTH_LONG).show();
    }

    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new Connection();
        new Thread(connect).start();
    }
    
    class Connection implements Runnable {

		@Override
		public void run() {
			Log.d(TAG, "Service Started");

			try {
				open();
				send("helo csavo:)");
				/*while(true){
					
				}*/
			}
			catch (Exception e) {
				Log.d(TAG, e.getMessage());				
			}
			finally {
				try {
					close();
				}
				catch (Exception e) {
					Log.d(TAG, e.getMessage());				
				}
			}
			
			
		}
    	
    }
    
    public void send(Object s) throws IOException{
    	os.writeObject(s);
    	os.flush();
    }
    
    
    public void open () throws IOException {
    	SocketAddress socketAddress = new InetSocketAddress("192.168.1.105", 4567);              
            s.connect(socketAddress);
        is = new ObjectInputStream(s.getInputStream());
        os = new ObjectOutputStream(s.getOutputStream());
    }
    
    public void close () throws IOException {
    	s.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            s.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        s = null;
    }
}