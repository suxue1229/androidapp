package smart.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import smart.app.Adapter.ExtendableListViewAdapter;
import smart.app.Network.HttpService;
import smart.app.Activity.MainActivity;
import smart.app.Network.NetBroadcastReceiver;
import smart.app.R;
import smart.app.bean.Devicebean;
import smart.app.bean.Institutebean;

public class FragmentMap extends Fragment implements View.OnClickListener,NetBroadcastReceiver.EventHandle{

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    MapView mMapView;
    public BaiduMap mBaiduMap;

    // UI相关
    LinearLayout linearLayout;////站点搜索输入框
    TextView search;
    ImageView list;
    ExpandableListView expandableListView;
    ImageView mylocation;
    boolean islocation = true;
    boolean isFirstLoc = true; // 是否首次定位
    boolean isVisible = true;//站点列表是否可见
    MyLocationData locData;
    Institutebean institutebean;
    MainActivity mainActivity;
    Institutebean.datainfo infoUtil;
    InfoWindow infoWindow=null;

    ArrayList<String> province=new ArrayList<>();

    ArrayList<String> provincename=new ArrayList<>();
    HashMap<String,ArrayList<Institutebean.datainfo>> station_province;
    private Devicebean devicebean;

    public static NetBroadcastReceiver.EventHandle eventHandle;
    public static LinearLayout mNetErrorView;
    private Timer timer = new Timer();

    private static class MyHandler extends Handler {
        private final WeakReference<FragmentMap> mActivity;

        private MyHandler(FragmentMap activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentMap activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
            }
        }
    }


    private final MyHandler mHandler = new MyHandler(this);
    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            addOverlay(institutebean.data);
            station_province=new HashMap<>();
            for(int i=0;i<institutebean.data.size();i++){
               final Institutebean.datainfo datainfo=institutebean.data.get(i);
                String province_name;
                if(institutebean.data.get(i).Address!=null &&!institutebean.data.get(i).Address.isEmpty()){
                    province_name=institutebean.data.get(i).getProvince(institutebean.data.get(i).Address);
                    provincename.add(province_name);

                    ArrayList<Institutebean.datainfo> infos = station_province.get(province_name); //判断是否已经存在key
                    if(infos == null){ //若不存在则创建List,并存进HashMap
                        infos = new ArrayList<>();
                        station_province.put(province_name, infos);
                    }
                    infos.add(institutebean.data.get(i)); //在key对应的value（list）中添加该key在初始List中的位置
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //刷新数据
                            try {
                                devicebean = HttpService.deviceinfo(datainfo.Id);
                                datainfo.setFlagName(devicebean);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            }
            province=onlyList(provincename);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplication());
        View view = inflater.inflate(R.layout.fg_map, container, false);
        eventHandle=this;
        mainActivity = (MainActivity) getActivity();
        init(view);
        return view;
    }

    private void init(View view) {
        initMap(view);
        mNetErrorView=view.findViewById(R.id.net_status_bar_top);
        mNetErrorView.setOnClickListener(this);

        linearLayout = view.findViewById(R.id.linearLayout1);
        linearLayout.setVisibility(View.VISIBLE);
        search = view.findViewById(R.id.search);
        list = view.findViewById(R.id.list);
        expandableListView = view.findViewById(R.id.expend_list);
        mylocation = view.findViewById(R.id.mylocation);
        mylocation.setOnClickListener(this);
        list.setOnClickListener(this);
    }
    @Override
    public void onNetChange(boolean isNetConnected) {
        //网络状态变化时的操作
        if (!isNetConnected){
            Toast.makeText(getContext(),R.string.net_error_tip,Toast.LENGTH_SHORT).show();
            mNetErrorView.setVisibility(View.VISIBLE);
        }else {
            mNetErrorView.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list:
                if (isVisible) {
                    isVisible = false;
                    expandableListView.setVisibility(View.VISIBLE);
                } else {
                    expandableListView.setVisibility(View.GONE);
                    isVisible = true;
                }
                if(station_province.size()>0){
                    expandableListView.setAdapter(new ExtendableListViewAdapter(getContext(),province,station_province));
                    //设置分组的监听
                    expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            return false;
                        }
                    });
                    //设置子项布局监听
                    expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                            ArrayList<Institutebean.datainfo> station=station_province.get(province.get(groupPosition));
                            search.setText(station.get(childPosition).Name);
                            expandableListView.setVisibility(View.GONE);

                            // 改变地图状态，使地图显示在恰当的缩放大小
                            MapStatus mMapStatus = new MapStatus.Builder().zoom(18.0f).build();
                            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                            mBaiduMap.setMapStatus(mMapStatusUpdate);

                            LatLng latLng = station.get(childPosition).getLatLng(station.get(childPosition).Latitude, station.get(childPosition).Longitude);
                            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                            mBaiduMap.setMapStatus(msu);
                            return true;

                        }
                    });
                    //控制他只能打开一个组
                    expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                        @Override
                        public void onGroupExpand(int groupPosition) {
                            int count = new ExtendableListViewAdapter(getContext(),province,station_province).getGroupCount();
                            for(int i = 0;i < count;i++){
                                if (i!=groupPosition){
                                    expandableListView.collapseGroup(i);
                                }
                            }
                        }
                    });

                }
                break;
            case R.id.mylocation:
                if (islocation) {
                    initLocation();
                    islocation = false;
                } else {
                    // 退出时销毁定位
                    mLocClient.stop();
                    // 关闭定位图层
                    mBaiduMap.setMyLocationEnabled(false);
                    islocation = true;
                }
                break;
            default:
                break;
        }

    }

    private void initMap(View view) {
        // 地图初始化
        mMapView = view.findViewById(R.id.bmapView);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        // 改变地图状态，使地图显示在恰当的缩放大小
        MapStatus mMapStatus = new MapStatus.Builder().zoom(6.0f).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

    private void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("请打开GPS");
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 转到手机设置界面，用户设置GPS
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            });
            dialog.show();
        } else {
            mLocClient.start();
        }
    }


    //显示marker
    private void addOverlay(ArrayList<Institutebean.datainfo> infos) {
        //清空地图
        mBaiduMap.clear();
        //创建marker的显示图标
        BitmapDescriptor bitmap = null;
        LatLng desLatLng = null;
        Marker marker;
        OverlayOptions options;
        ArrayList<LatLng> points = new ArrayList<>();
        for (final Institutebean.datainfo info : infos) {
            if (info.Type.equals("Normal")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.normalpic);
            } else if (info.Type.equals("Station")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.stationpic);
            }
            //获取经纬度
            desLatLng = info.getLatLng(info.Latitude,info.Longitude);
            points.add(desLatLng);

            //设置marker
            options = new MarkerOptions()
                    .position(desLatLng)//设置位置
                    .icon(bitmap)//设置图标样式
                    .zIndex(9) // 设置marker所在层级
                    .draggable(true); // 设置手势拖拽;
            //添加marker
            marker = (Marker) mBaiduMap.addOverlay(options);
            //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
            Bundle bundle = new Bundle();
            //info必须实现序列化接口
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);

            //添加marker点击事件的监听
            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                    View view = inflater.inflate(R.layout.maker_menu, null);
                    view.setPadding(20, 5, 20, 5);
                    TextView tv = view.findViewById(R.id.title);
                    LinearLayout startbutton = view.findViewById(R.id.start);
                    tv.setTextColor(Color.BLACK);
                    //从marker中获取info信息
                    Bundle bundle = marker.getExtraInfo();
                    infoUtil = (Institutebean.datainfo) bundle.getSerializable("info");
                    if (infoUtil.Name != null) tv.setText(infoUtil.Name);
                    startbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            search.setText(infoUtil.Name);
                            Bundle bundle1 = new Bundle();
                            bundle1.putSerializable("datainfo", infoUtil);

                            mainActivity.fragmentStation = new FragmentStation();
                            mainActivity.fragments.add(mainActivity.fragmentStation);
                            mainActivity.fragmentStation.setArguments(bundle1);
                            mainActivity.hideOthersFragment(mainActivity.fragmentStation, true);

                            mainActivity.txt_map.setSelected(false);
                            mainActivity.txt_station.setSelected(true);
                            mainActivity.txt_myself.setSelected(false);
                        }
                    });
                    //显示infowindow
                    infoWindow = new InfoWindow(view, marker.getPosition(), -47);
                    mBaiduMap.showInfoWindow(infoWindow);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBaiduMap.hideInfoWindow();
                        }
                    });
                    return true;
                }
            });
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            locData = new MyLocationData.Builder().latitude(location.getLatitude()).longitude(location.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden){
        if(!hidden){
        // 改变地图状态，使地图显示在恰当的缩放大小
        MapStatus mMapStatus = new MapStatus.Builder().zoom(6.0f).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        }
        super.onHiddenChanged(hidden);
    }
    @Override
    public void onStart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //刷新数据
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                try {
                    institutebean = HttpService.instituteInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.post(sRunnable);
                    }
                }, 0, 30000/* 表示0毫秒之後，每隔5000毫秒執行一次 */);
            }
        }).start();

        super.onStart();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


    public ArrayList onlyList(ArrayList list){
        ArrayList<String> list1 = new ArrayList<>();
        Comparator<Object> com= Collator.getInstance(java.util.Locale.CHINA);//实现中文汉字按首字母排序
        Iterator it = list.iterator();
        while (it.hasNext()){
            String s = (String) it.next();
                if (!list1.contains(s)){    //判断list1中是否包括该元素
                    list1.add(s);
                }
        }
        Collections.sort(list1, com);
        return list1;
    }

}
