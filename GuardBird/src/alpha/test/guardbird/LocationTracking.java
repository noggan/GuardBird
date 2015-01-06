package alpha.test.guardbird;

import android.app.IntentService;
import android.app.Notification;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationTracking extends IntentService {

	private LocationManager locationManager;
	private String provider;
	
	public LocationTracking() {
		super("LocationTracking");
		Log.i("GuardBird", "LocationTracking.LocationTracking");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("GuardBird", "LocationTracking.onStartCommand");
		return super.onStartCommand(intent,flags,startId);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("GuardBird", "LocationTracking.onHandleIntent");
		/*Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);*/
	}
	
	public void onLocationChanged(Location location) {
		Log.i("GuardBird", "LocationTracking.onLocationChanged, Location changed");
	    int lat = (int) (location.getLatitude());
	    int lng = (int) (location.getLongitude());
	    Log.i("GuardBird", "LocationTracking.onLocationChanged, changed to lat: " + lat + " long: " + lng);
	  }

}
