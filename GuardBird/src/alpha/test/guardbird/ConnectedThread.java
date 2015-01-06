package alpha.test.guardbird;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    //private final MainActivity mainActivity;
 
    public ConnectedThread(BluetoothSocket socket) {
    	Log.i("GuardBird", "ConnectedThread.ConnectedThread");
        mmSocket = socket;
        //this.mainActivity = mainActivity;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    @SuppressLint("NewApi") public void run() {
    	Log.i("GuardBird", "ConnectedThread.run");
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        manageConnection();
    	//Toast.makeText(this.mainActivity.getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
        
        //BluetoothDevice remoteDevice = mmSocket.getRemoteDevice();
        // Keep listening to the InputStream until an exception occurs
        /*while (true) {
        	/*try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                Toast.makeText(mainActivity.getApplicationContext(), new String(buffer, Charset.defaultCharset()), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                break;
            }
        }*/
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
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
    	Log.i("GuardBird", "ConnectedThread.write");
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
