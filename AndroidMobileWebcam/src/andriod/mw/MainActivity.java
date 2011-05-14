package andriod.mw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Button button;
	private TextView textview;
	private boolean isBound;
	private ConnectionService connectionService;
	
	
	 public Socket s;
	    public ObjectOutputStream os;
	    public ObjectInputStream is;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        s= new Socket();
        
        textview = (TextView) findViewById(R.id.textView1);
                
        
        button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textview.setText("Connecting");
				startService(new Intent(MainActivity.this,ConnectionService.class));
				doBindService();
			    new Thread (new Runnable(){
			    	public void run(){
			    		try {
							open();
							send("halo");
						} catch (Exception e) {
							Log.e(TAG, e.getMessage());
						}
			    	}
			    }).start();
			}
		});
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textview.setText("Disconnecting");
				try{
				close();
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		
    }

    public void send(String s) throws IOException{
    	os.writeChars(s);
    	os.flush();
    }
    
    
    public void open () throws IOException {
    	SocketAddress socketAddress = new InetSocketAddress("192.168.1.103", 4567);              
            s.connect(socketAddress);
        is = new ObjectInputStream(s.getInputStream());
        os = new ObjectOutputStream(s.getOutputStream());
    }
    
    public void close () throws IOException {
    	s.close();
    }
   // public void onStart(){
    	//startService(new Intent(MainActivity.this,ConnectionService.class));
        //doBindService();
    //}
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	connectionService = ((ConnectionService.LocalBinder)service).getService();

        }
        public void onServiceDisconnected(ComponentName className) {
        	connectionService = null;
        }
    };
	
    private void doBindService() {
        bindService(new Intent(MainActivity.this, ConnectionService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        isBound = true;
        connectionService.IsBoundable();
    }


    private void doUnbindService() {
        if (isBound) {
            // Detach our existing connection.
            unbindService(serviceConnection);
            isBound = false;
        }
    }
    public void onDistroy(){
    	doUnbindService();
    }
}