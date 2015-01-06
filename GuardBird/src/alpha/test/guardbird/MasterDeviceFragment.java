package alpha.test.guardbird;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

// fragment for the  master device tab
public class MasterDeviceFragment extends Fragment {
	
	private static Button toggleConnectivityBtn;
	private static ListView listOfDevices;
	
	public static MasterDeviceFragment newInstance() {
		return new MasterDeviceFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//listView.setAdapter(mArrayAdapter);
		View rootView = inflater.inflate(R.layout.fragment_master_device, container, false);
		toggleConnectivityBtn = (Button) rootView.findViewById(R.id.toggle_connection_button);
		listOfDevices = (ListView)rootView.findViewById(R.id.listView_btDevices);
		listOfDevices.setOnItemClickListener(((MainActivity) getActivity()).getListener());
		//ArrayAdapter<String> localArrayAdapter = ((MainActivity) getActivity()).getListAdapter();
		ArrayAdapter<BluetoothDevice> localArrayAdapter = ((MainActivity) getActivity()).getListAdapter();
		listOfDevices.setAdapter(localArrayAdapter);
		return rootView;
	}
	
	public static void setConnectivityBtnClickable(boolean newState){
		toggleConnectivityBtn.setClickable(newState);
	}
	
	public static void checkSelectedItem(int position){
		listOfDevices.setItemChecked(position, true);
	}
}