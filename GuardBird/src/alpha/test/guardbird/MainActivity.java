package alpha.test.guardbird;

import java.util.ArrayList;
import java.util.UUID;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
	static final int REQUEST_ENABLE_BT_FROM_LIST_DEVICES = 1;
	static final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	static final UUID MY_UUID = UUID.fromString("58ad247e-3fd9-46ed-9e19-207b2ab07d6f");
	static final int REQUEST_ENABLE_BT_FROM_SLAVE_DEVICE = 2;
	static final int SERVER_ACCEPT_TIMEOUT = 20000;
	static final String START_BT_SERVER = "LISTEN";
	static final String START_BT_CLIENT = "CONNECT";
	static BluetoothDevice rBtDevice;
	private static ArrayList<BluetoothDevice> mArrayList = new ArrayList<BluetoothDevice>();
	private OnItemClickListener listener;
	private Uri notification;
	private Ringtone alarm;

	private ArrayAdapter<BluetoothDevice> mArrayAdapter;

	public ArrayAdapter<BluetoothDevice> getListAdapter(){
		return mArrayAdapter;
	}

	public OnItemClickListener getListener(){
		return listener;
	}

	SectionsPagerAdapter mSectionsPagerAdapter;

	private int indexOfCheckedBtDevice = -1;

	ViewPager mViewPager;


	//Maybe all this in a background service the entire time
	private final BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Toast.makeText(getApplicationContext(),"\n  Device: " + device.getName() + ", " + device 
						,Toast.LENGTH_LONG).show();
				//mArrayList.add(device.getName() + "\n" + device.getAddress());
				mArrayList.add(device);
				mArrayAdapter.notifyDataSetChanged();
			}    
		}
	};

	@SuppressLint("Wakelock") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("GuardBird", "MainActivity.onCreate");
		/*SharedPreferences prefs = this.getSharedPreferences(
			      "alpha.test.guardbird", Context.MODE_PRIVATE);
		prefs.edit().putBoolean("SERVICE_RUNNING", false).apply();*/
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | 
				PowerManager.ACQUIRE_CAUSES_WAKEUP, "GuardBird");
		
		notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		alarm = RingtoneManager.getRingtone(getApplicationContext(), notification);
		
		boolean startAlarm = false;
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			Log.i("GuardBird", "MainActivity.onCreate, extras != null");
			wl.acquire();
			startAlarm = extras.getBoolean("ALARM");
		}
	
		if(startAlarm){
			Log.i("GuardBird", "MainActivity.onCreate, startAlarm == true");
			this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			Log.i("GuardBird", "MainActivity.onCreate, Setting content view");
			setContentView(R.layout.activity_main);
			startAlarm();
			wl.release();
		}
		else{
			setContentView(R.layout.activity_main);
		}
		
		mArrayAdapter = new ArrayAdapter<BluetoothDevice>(this,
				android.R.layout.simple_list_item_single_choice, mArrayList);

		
		listener = new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MasterDeviceFragment.checkSelectedItem(position);
				MasterDeviceFragment.setConnectivityBtnClickable(true);
				indexOfCheckedBtDevice = position;
			}
		};

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		//FOR DEBUGGING
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		//END OF DEBUGGING
		registerReceiver(mReceiver, filter);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(
					actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		unregisterReceiver(mReceiver);
		btAdapter.cancelDiscovery();
	}
	
	private void startAlarm(){
		Log.i("GuardBird", "MainActivity.startAlarm");
		alarm.play();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You're bag is stolen!! (MAYBE)")
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				alarm.stop();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateListOfBtDevices(){
		mArrayAdapter.clear();
		btAdapter.cancelDiscovery();
		btAdapter.startDiscovery();
		mArrayAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		Log.i("GuardBird", "onActivityResult");
		//the request was sent from the list devices so a discovery should be started
		if(requestCode == REQUEST_ENABLE_BT_FROM_LIST_DEVICES){
			Log.i("GuardBird", "onActivityResult, request from listBtDevices");
			if(resultCode == RESULT_OK){
				Log.i("GuardBird", "onActivityResult, request from listBtDevices ok");
				//start searching for other devices
				updateListOfBtDevices();
			}
			else if(resultCode == RESULT_CANCELED){
				Log.i("GuardBird", "On_activity_result, result cancelled");
			}
		}
		else if(requestCode == REQUEST_ENABLE_BT_FROM_SLAVE_DEVICE){
			Toast.makeText(getApplicationContext(), "HERE", 
					Toast.LENGTH_LONG).show();
			if(resultCode == RESULT_CANCELED){
				Toast.makeText(getApplicationContext(), "Bluetooth was unable to activate", 
						Toast.LENGTH_LONG).show();
			}
			else{
				startBtServerService();
			}
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	//method that handles toggling of connectivity of the master device
	public void toggleMasterDevice(View view){
		Log.i("GuardBird", "toggleMasterDevice");
		btAdapter.cancelDiscovery();
		if (btAdapter == null) {
			return;
		}
		if(!(indexOfCheckedBtDevice < 0)){
			rBtDevice = mArrayAdapter.getItem(indexOfCheckedBtDevice);
			Log.i("GuardBird", "toggleMasterDevice, one marked bt device");
		}
		startBtClientService();
	}

	public void listBtDevices(View view){
		if(btAdapter == null){
			Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_LONG).show();
			return;
		}
		if (!btAdapter.isEnabled()) {
			Log.i("GuardBird", "listBtDevices, no bluetooth enabled");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			Log.i("GuardBird", "listBtDevices, intent created");
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_FROM_LIST_DEVICES);
			Log.i("GuardBird", "listBtDevices, intent started");
			//connect to the selected device
		}
		else{
			//start searching for other devices
			Log.i("GuardBird", "listBtDevices, bluetooth enabled");
			updateListOfBtDevices();
		}
	}

	//method that handles toggling of the slave device
	public void toggleSlaveDevice(View view){
		if (btAdapter == null) {
			return;
		}
		if(btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
		{
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT_FROM_SLAVE_DEVICE);
		}
		else{
			startBtServerService();
		}
	}

	private void startBtServerService(){
		Log.i("GuardBird", "startBtServerService");
		Intent mServiceIntent = new Intent(this, BluetoothConnection.class);
		mServiceIntent.putExtra("IS_MASTER_DEVICE", false);
		Log.i("GuardBird", "startBtServerService, starting service");
		startService(mServiceIntent);
		Log.i("GuardBird", "startBtServerService, service started");
	}

	private void startBtClientService(){
		Log.i("GuardBird", "startBtClientService");
		SharedPreferences prefs = this.getSharedPreferences(
			      "alpha.test.guardbird", Context.MODE_PRIVATE);
		final Intent mServiceIntent = new Intent(this, BluetoothConnection.class);
		if(prefs.getBoolean("SERVICE_RUNNING", false)){
			Log.i("GuardBird", "startBtClientService, service is running. Stopping!");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you want to disable the GuardBird connection?")
			.setCancelable(false)
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.i("GuardBird", "Canceled stopping service!!");
				}
			})
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
						Log.i("GuardBird", "Stopping service!!");
						stopService(mServiceIntent);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		else{
			Log.i("GuardBird", "startBtClientService, service is not running. Starting!");
			mServiceIntent.putExtra("IS_MASTER_DEVICE", true);
			startService(mServiceIntent);
		}
	}
}
