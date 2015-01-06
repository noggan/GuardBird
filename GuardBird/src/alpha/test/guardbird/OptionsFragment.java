package alpha.test.guardbird;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// fragment for the options tab
public class OptionsFragment extends Fragment {

	public static OptionsFragment newInstance(int sectionNumber) {
		return new OptionsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_options, container, false);
		return rootView;
	}
}