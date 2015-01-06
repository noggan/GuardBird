package alpha.test.guardbird;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

class ClientThread extends Thread {
	/**
	 * 
	 */
	
	private BluetoothSocket mmSocket;
	//private ConnectedThread connectedThread;

	public ClientThread(){
		Log.i("GuardBird", "ClientThread.ClientThread");
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = MainActivity.rBtDevice.createRfcommSocketToServiceRecord(MainActivity.MY_UUID);
		} catch (IOException e) {
			Log.i("GuardBird", "ClientThread.ClientThread, problem creating RfCommSocket");
		}
		mmSocket = tmp;
	}

	public void run() {
		Log.i("GuardBird", "ClientThread.run");
		// Cancel discovery because it will slow down the connection
		MainActivity.btAdapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			Log.i("GuardBird", "ClientThread.ClientThread, connecting");
			mmSocket.connect();
			Log.i("GuardBird", "ClientThread.ClientThread, connection accepted");
		} 
		catch (IOException connectException) {
			Log.i("GuardBird", "ClientThread.ClientThread, problem connecting");
			try {
				mmSocket.close();
			} catch (IOException closeException) { }
			return;
		}
		Log.i("GuardBird", "ClientThread.ClientThread, creating ConnectedThread");
		//connectedThread = new ConnectedThread(mmSocket);
		manageConnection();
	}

	private void manageConnection() {
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) { }
	}
}