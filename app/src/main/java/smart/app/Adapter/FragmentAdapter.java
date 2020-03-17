package smart.app.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.HashMap;

public class FragmentAdapter extends FragmentPagerAdapter {

    private HashMap<Integer, android.support.v4.app.Fragment> mfragmentList;

    public FragmentAdapter(FragmentManager fm, HashMap<Integer, android.support.v4.app.Fragment> fragmentList) {
        super(fm);
        this.mfragmentList = fragmentList;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return mfragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mfragmentList.size();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {

    }
}