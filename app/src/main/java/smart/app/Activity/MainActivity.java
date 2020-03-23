package smart.app.Activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import smart.app.Fragment.FragmentMap;
import smart.app.Fragment.FragmentMyself;
import smart.app.Fragment.FragmentStation;
import smart.app.Network.NetBroadcastReceiver;
import smart.app.R;


public class MainActivity extends FragmentActivity implements View.OnClickListener{
    //UI Object
    public TextView txt_map;
    public TextView txt_station;
    public TextView txt_myself;


    public List<Fragment> fragments;
    public FragmentManager fManager;
    public FragmentTransaction transition;
    FragmentMap fragmentMap;
    public FragmentStation fragmentStation;
    FragmentMyself fragmentMyself;



    public NetBroadcastReceiver netBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindViews();
    }
    //UI组件初始化与事件绑定
    private void bindViews() {
        txt_map = findViewById(R.id.txt_map);
        txt_station = findViewById(R.id.txt_station);
        txt_myself = findViewById(R.id.txt_myself);


        fManager = getSupportFragmentManager();
        fragments = new ArrayList<>();

        fragmentMap = new FragmentMap();
        fragments.add(fragmentMap);
        txt_map.performClick();   //模拟一次点击，既进去后选择第一项
        hideOthersFragment(fragmentMap, true);
        txt_map.setSelected(true);

        txt_map.setOnClickListener(this);
        txt_station.setOnClickListener(this);
        txt_myself.setOnClickListener(this);


    }

    //重置所有文本的选中状态
    private void setSelected() {
        txt_map.setSelected(false);
        txt_station.setSelected(false);
        txt_myself.setSelected(false);
    }
    @Override
    public void onClick(View v) {
        setSelected();
        switch (v.getId()) {
            case R.id.txt_map:
                hideOthersFragment(fragmentMap, false);
                txt_map.setSelected(true);
                break;
            case R.id.txt_station:
                if (fragmentStation == null) {
                    fragmentStation = new FragmentStation();
                    fragments.add(fragmentStation);
                    hideOthersFragment(fragmentStation, true);
                } else {
                    hideOthersFragment(fragmentStation, false);
                }
                txt_station.setSelected(true);
                break;
            case R.id.txt_myself:
             if (fragmentMyself == null) {
                 fragmentMyself = new FragmentMyself();
                fragments.add(fragmentMyself);
                hideOthersFragment(fragmentMyself, true);
                } else {
                    hideOthersFragment(fragmentMyself, false);
                }
                txt_myself.setSelected(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        // 实例化BroadcastReceiver子类 & IntentFilter
        if(netBroadcastReceiver ==null){
            netBroadcastReceiver = new NetBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        // 设置接收广播的类型
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // 调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(netBroadcastReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netBroadcastReceiver);
        super.onPause();
    }


    class ViewPagetOnPagerChangedLisenter implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setSelected();
            if (position == 0) {
                txt_map.setSelected(true);
            } else if (position == 1) {
                txt_station.setSelected(true);
            } else if (position == 2) {
                txt_myself.setSelected(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
    /**
     * 动态显示Fragment
     *
     * @param showFragment 要增加的fragment
     * @param add          true：增加fragment；false：切换fragment
     */
    public void hideOthersFragment(Fragment showFragment, boolean add) {
        transition = fManager.beginTransaction();
        if (add)
            transition.add(R.id.framelayout,showFragment);
        for (Fragment fragment : fragments) {
            if (showFragment.equals(fragment)) {
                transition.show(fragment);
            } else {
                transition.hide(fragment);
            }
        }
        transition.commit();
    }
}
