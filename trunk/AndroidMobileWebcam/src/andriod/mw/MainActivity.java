package andriod.mw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import andriod.service.ConnectionService;
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
			}
		});
		
		 button = (Button) findViewById(R.id.button3);
		 button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					connectionService.IsBoundable();
				}
			});
		 
		button = (Button) findViewById(R.id.button2);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				textview.setText("Disconnecting");
			}
		});
		
		
    }

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
        Log.d(TAG,"Blinding to service...");
        isBound = true;
        //connectionService.IsBoundable();
    }


    private void doUnbindService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
    
    
    public void onDistroy(){
    	doUnbindService();
    }
}