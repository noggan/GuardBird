package alpha.test.guardbird;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

class ServerThread extends Thread {
    /**
	 * 
	 */
	//private final MainActivity mainActivity;
	private final BluetoothServerSocket mmServerSocket;
	private ConnectedThread connectedThread;
	//private final BluetoothAdapter btAdapter;

    public ServerThread() {
    	//this.mainActivity = mainActivity;
		//Toast.makeText(this.mainActivity.getApplicationContext(), "Creating serverthread", Toast.LENGTH_SHORT).show();
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = MainActivity.btAdapter.listenUsingRfcommWithServiceRecord("GuardBird_slave", MainActivity.MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        Log.i("GuardBird", "ServerThread.run");
        //Toast.makeText(this.mainActivity.getApplicationContext(), "Started listening", Toast.LENGTH_SHORT).show();
        try {
            socket = mmServerSocket.accept(MainActivity.SERVER_ACCEPT_TIMEOUT);
        } catch (IOException e) {
        	//Toast.makeText(this.mainActivity.getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
        	Log.i("GuardBird", "ServerThread.run, something went wrong");
        	cancel();
        	//break;
        }
        // If a connection was accepted
        if (socket != null) {
        	Log.i("GuardBird", "ServerThread.run, connection accepted");
        	//Toast.makeText(this.mainActivity.getApplicationContext(), "Connection accepted", Toast.LENGTH_SHORT).show();
        	connectedThread = new ConnectedThread(socket);
        	connectedThread.run();
           //manageConnectedSocket(socket);
	        try {
	        	mmServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
    	Log.i("GuardBird", "ServerThread.cancel");
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}