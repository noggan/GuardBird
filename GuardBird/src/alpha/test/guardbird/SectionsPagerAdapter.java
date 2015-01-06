package alpha.test.guardbird;

import java.util.Locale;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

	/**
	 * 
	 */
	private final MainActivity mainActivity;
	private final int NR_OF_TABS = 3;

	public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
		super(fm);
		this.mainActivity = mainActivity;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
		case 0:
			return new MasterDeviceFragment();
		case 1:
			// placeholder for SlaveDeviceFragment
			return SlaveDeviceFragment.newInstance();
		case 2:
			// placeholder for OptionsFragment
			return OptionsFragment.newInstance(position + 1);
		}
		return null;
	}

	@Override
	public int getCount() {
		return NR_OF_TABS;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.mainActivity.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return this.mainActivity.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return this.mainActivity.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}