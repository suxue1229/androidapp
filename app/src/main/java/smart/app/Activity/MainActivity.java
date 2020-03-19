package smart.app.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import smart.app.Adapter.FragmentAdapter;
import smart.app.Fragment.FragmentMap;
import smart.app.Fragment.FragmentMyself;
import smart.app.Fragment.FragmentStation;
import smart.app.Interface.EventHandle;
import smart.app.Network.MyBroadcaseReceiver;
import smart.app.Network.NetUtil;
import smart.app.R;


public class MainActivity extends FragmentActivity implements View.OnClickListener{
    //UI Object
    public TextView txt_map;
    public TextView txt_station;
    public TextView txt_myself;

    FragmentManager fManager;
    private MyBroadcaseReceiver myBroadcaseReceiver;

    public ViewPager viewPager;
    public HashMap<Integer, android.support.v4.app.Fragment> fragmentList = new HashMap<>();
    public FragmentAdapter adapter;



    private LinearLayout mNetErrorView;
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
        mNetErrorView=findViewById(R.id.net_status_bar_top);
        mNetErrorView.setOnClickListener(this);


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
            case R.id.net_status_bar_top:
                // 跳转到 全部网络设置
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
//         实例化BroadcastReceiver子类 & IntentFilter
        if(myBroadcaseReceiver==null){
            myBroadcaseReceiver = new MyBroadcaseReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        // 设置接收广播的类型
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // 调用Context的registerReceiver（）方法进行动态注册
        registerReceiver(myBroadcaseReceiver, intentFilter);


        myBroadcaseReceiver.setOnCallListener(new EventHandle() {

            @Override
            public void onNetChange(boolean isNetConnected) {
                if (!isNetConnected) {
                    Toast.makeText(getApplicationContext(), R.string.net_error_tip, Toast.LENGTH_LONG).show();
                    mNetErrorView.setVisibility(View.VISIBLE);
                } else {
                    mNetErrorView.setVisibility(View.GONE);
                }
            }
        });

        myBroadcaseReceiver.onCall(NetUtil.isNetConnected(this));  //真正触发回调的方法。

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myBroadcaseReceiver);
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
}
