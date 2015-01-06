package alpha.test.guardbird;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class BluetoothConnection extends IntentService {

	private static final int GB_ID = 1337;
	private Runnable btThread;
	private boolean isMasterDevice;
	private boolean screenOn = true;


	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				Log.i("GuardBird","Disconnected, MAKE NOISE");
				if(screenOn){
					if(isMasterDevice && BluetoothAdapter.getDefaultAdapter().isEnabled()){
						Intent activityIntent = new Intent(context, MainActivity.class);
						activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						activityIntent.putExtra("ALARM", true);
						startActivity(activityIntent);
					}
					else{
						Log.i("GuardBird", "SlaveDevice");
						final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
						if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
							Log.i("GuardBird", "SlaveDevice, gps enabled");
							Runnable task = new Runnable() {
						        @Override
						        public void run() {
						        	final Intent mServiceIntent = new Intent(getBaseContext(), LocationTracking.class);
						    		startService(mServiceIntent);
						    		Log.i("GuardBird", "SlaveDevice, launching service");
						        }
							};
							new Handler(Looper.getMainLooper()).post(task);
							Log.i("GuardBird", "SlaveDevice, posted to handler");
						}
					}
				}
				else{
					Log.i("GuardBird", "AlarmManager");
					if(isMasterDevice && BluetoothAdapter.getDefaultAdapter().isEnabled()){
						AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
						Intent alarmIntent = new Intent(context, AlarmReceiver.class);
						PendingIntent sender = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
						am.set(AlarmManager.RTC_WAKEUP, 0, sender);
					}
					//else start location service
				}
				stopSelf();
			}
			else if(Intent.ACTION_SCREEN_ON.equals(action)){
				Log.i("GuardBird", "screen on");
				screenOn = true;
			}
			else if(Intent.ACTION_SCREEN_OFF.equals(action)){
				Log.i("GuardBird", "screen off");
				screenOn = false;
			}
		}
	};

	public BluetoothConnection() {
		super("BluetoothConnection");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("GuardBird", "BluetoothConnection.onStartCommand");
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		startForeground(GB_ID, new Notification());
		registerReceiver(mReceiver, filter);
		SharedPreferences prefs = this.getSharedPreferences(
			      "alpha.test.guardbird", Context.MODE_PRIVATE);
		prefs.edit().putBoolean("SERVICE_RUNNING", true).apply();
		return super.onStartCommand(intent,flags,startId);
	}

	public void onDestroy(){
		Log.i("GuardBird", "Service terminated");
		SharedPreferences prefs = this.getSharedPreferences(
			      "alpha.test.guardbird", Context.MODE_PRIVATE);
		prefs.edit().putBoolean("SERVICE_RUNNING", false).apply();
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {
		Log.i("GuardBird", "BluetoothConnection.onHandleIntent");
		// Gets data from the incoming Intent
		isMasterDevice = workIntent.getBooleanExtra("IS_MASTER_DEVICE", true);
		Log.i("GuardBird", "BluetoothConnection.onHandIntent, isMasterDevice = " + isMasterDevice);
		if(isMasterDevice){
			Log.i("GuardBird", "BluetoothConnection.onHandleIntent, client side");
			btThread = new ClientThread();
			btThread.run();
			Log.i("GuardBird", "BluetoothConnection.onHandleIntent, client thread started");
		}
		else{
			Log.i("GuardBird", "BluetoothConnection.onHandleIntent, server side");
			btThread = new ServerThread();
			btThread.run();
			Log.i("GuardBird", "BluetoothConnection.onHandleIntent, server thread started");
		}
	}
}
