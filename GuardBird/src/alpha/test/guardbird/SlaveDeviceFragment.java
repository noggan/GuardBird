package alpha.test.guardbird;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// fragment for the  slave device tab
public class SlaveDeviceFragment extends Fragment {

	public static SlaveDeviceFragment newInstance() {
		return new SlaveDeviceFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_slave_device, container, false);
		return rootView;
	}
}