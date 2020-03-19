package smart.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.HashMap;

public class MyPagerAdapter extends FragmentPagerAdapter {

    private HashMap<Integer, Fragment> mfragmentList;

    public MyPagerAdapter(FragmentManager fm, HashMap<Integer, Fragment> fragmentList) {
        super(fm);
        this.mfragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
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