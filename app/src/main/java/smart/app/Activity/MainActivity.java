package smart.app.Activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.HashMap;

import smart.app.Adapter.FragmentAdapter;
import smart.app.Fragment.FragmentMap;
import smart.app.Fragment.FragmentMyself;
import smart.app.Fragment.FragmentStation;
import smart.app.Network.MyBroadcaseReceiver;
import smart.app.R;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    //UI Object
    public TextView txt_map;
    public TextView txt_station;
    public TextView txt_myself;

    FragmentManager fManager;
    private MyBroadcaseReceiver myBroadcaseReceiver;

    public ViewPager viewPager;
    public HashMap<Integer, android.support.v4.app.Fragment> fragmentList = new HashMap<>();
    public FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        bindViews();
        txt_map.performClick();   //模拟一次点击，既进去后选择第一项
        initViewPager();
    }

    //UI组件初始化与事件绑定
    private void bindViews() {
        txt_map = findViewById(R.id.txt_map);
        txt_station = findViewById(R.id.txt_station);
        txt_myself = findViewById(R.id.txt_myself);
        viewPager = findViewById(R.id.viewpager_a);
        fManager = getSupportFragmentManager();

        //填充数据
        fragmentList.put(0, new FragmentMap());
        fragmentList.put(1, new FragmentStation());
        fragmentList.put(2, new FragmentMyself());

        adapter = new FragmentAdapter(fManager, fragmentList);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        txt_map.setOnClickListener(this);
        txt_station.setOnClickListener(this);
        txt_myself.setOnClickListener(this);

    }

    public void initViewPager() {
        viewPager.addOnPageChangeListener(new ViewPagetOnPagerChangedLisenter());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        txt_map.setSelected(true);
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
                viewPager.setCurrentItem(0);
                break;
            case R.id.txt_station:
                viewPager.setCurrentItem(1);
                break;
            case R.id.txt_myself:
                viewPager.setCurrentItem(2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 实例化BroadcastReceiver子类 & IntentFilter
        myBroadcaseReceiver = new MyBroadcaseReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 设置接收广播的类型
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // 调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(myBroadcaseReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBroadcaseReceiver);
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
}
