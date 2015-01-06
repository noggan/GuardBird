package alpha.test.guardbird;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	@SuppressLint({ "Wakelock", "InlinedApi" }) @Override
	public void onReceive(Context context, Intent intent) {
		Log.i("GuardBird", "AlarmReceiver.onReceive");
		Log.i("GuardBird", "AlarmReceiver.onReceive, creating intent");
		Intent activityIntent = new Intent(context, MainActivity.class);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		activityIntent.putExtra("ALARM", true);
		Log.i("GuardBird", "AlarmReceiver.onReceive, starting intent");
		context.startActivity(activityIntent);
	}
}
