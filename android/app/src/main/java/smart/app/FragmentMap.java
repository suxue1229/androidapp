package smart.app;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class FragmentMap extends Fragment implements View.OnClickListener {

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    LinearLayout linearLayout;////站点搜索输入框
    RelativeLayout relativeLayout;//站点列表
    TextView search;
    ImageView list;
    ImageView mylocation;
    ListView listview;
    boolean islocation = true;
    boolean isFirstLoc = true; // 是否首次定位
    boolean isVisible = true;//站点列表是否可见
    MyLocationData locData;
    UiSettings mUiSettings;
    Gsonparsebean gsonbean;
    InstitutelistAdapter arrayAdapter;
    ArrayList<Gsonparsebean.datainfo> datainfos;
    LinearLayout startbutton;
    MainActivity mainActivity;
    Gsonparsebean.datainfo infoUtil;

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
            datainfos = new ArrayList<>();
            for (int i = 0; i < gsonbean.data.size(); i++) {
                datainfos.add(gsonbean.data.get(i));
            }
            arrayAdapter = new InstitutelistAdapter(getActivity(), datainfos);
            listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE); //表明有选项，如果不设置，缺省为none，即我们点击后仍无反应
            listview.setAdapter(arrayAdapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView txtview = view.findViewById(R.id.textView);
                    String product = txtview.getText().toString();
                    search.setText(product);
                    relativeLayout.setVisibility(View.GONE);

                    //将地图显示在最后一个marker的位置
                    LatLng latLng = transcoordinate(new LatLng(datainfos.get(position).Latitude, datainfos.get(position).Longitude));
                    MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
                    mBaiduMap.setMapStatus(msu);

                }
            });
            arrayAdapter.notifyDataSetChanged();
            addOverlay(gsonbean.data);
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplication());
        View view = inflater.inflate(R.layout.fg_map, container, false);
        mainActivity = (MainActivity) getActivity();
        init(view);
        return view;
    }

    private void init(View view) {
        initMap(view);
        relativeLayout = view.findViewById(R.id.relativelayout);
        relativeLayout.setVisibility(View.GONE);
        linearLayout = view.findViewById(R.id.linearLayout1);
        linearLayout.setVisibility(View.VISIBLE);
        search = view.findViewById(R.id.search);
        list = view.findViewById(R.id.list);
        listview = view.findViewById(R.id.mylistview);
        mylocation = view.findViewById(R.id.mylocation);
        mylocation.setOnClickListener(this);
        list.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.list:
                if (isVisible) {
                    isVisible = false;
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    relativeLayout.setVisibility(View.GONE);
                    isVisible = true;
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
        }

    }

    private void initMap(View view) {
        // 地图初始化
        mMapView = view.findViewById(R.id.bmapView);
        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setZoomGesturesEnabled(true);
        // 改变地图状态，使地图显示在恰当的缩放大小
        MapStatus mMapStatus = new MapStatus.Builder().zoom(18).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    gsonbean = HttpService.JsonToObject((String) HttpService.institutelists().get("str"), Gsonparsebean.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mHandler.post(sRunnable);
            }
        }).start();
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
    private void addOverlay(ArrayList<Gsonparsebean.datainfo> infos) {
        //清空地图
        mBaiduMap.clear();
        //创建marker的显示图标
        BitmapDescriptor bitmap = null;
        LatLng desLatLng = null;
        Marker marker;
        OverlayOptions options;
        for (Gsonparsebean.datainfo info : infos) {
            if (info.Type.equals("Normal")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.normalpic);
            } else if (info.Type.equals("Station")) {
                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.stationpic);
            }
            //获取经纬度
            desLatLng = transcoordinate(new LatLng(info.Latitude, info.Longitude));
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
        }
        //将地图显示在最后一个marker的位置
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(desLatLng);
        mBaiduMap.setMapStatus(msu);
        //添加marker点击事件的监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //从marker中获取info信息
                Bundle bundle = marker.getExtraInfo();
                infoUtil = (Gsonparsebean.datainfo) bundle.getSerializable("info");
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                View view = inflater.inflate(R.layout.maker_menu, null);
                view.setPadding(20, 5, 20, 5);
                TextView tv = view.findViewById(R.id.title);
                tv.setTextColor(Color.BLACK);
                if (infoUtil.Name != null) tv.setText(infoUtil.Name);
                startbutton = view.findViewById(R.id.start);
                startbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("datainfo", infoUtil);
                        mainActivity.fragmentList.get(1).setArguments(bundle1);
                        mainActivity.adapter.notifyDataSetChanged();
                        mainActivity.viewPager.setAdapter(mainActivity.adapter);
                        mainActivity.viewPager.setCurrentItem(1);
                        mainActivity.txt_map.setSelected(false);
                        mainActivity.txt_station.setSelected(true);
                        mainActivity.txt_myself.setSelected(false);
                    }
                });
                LatLng latLng = transcoordinate(new LatLng(infoUtil.Latitude, infoUtil.Longitude));
                //显示infowindow
                InfoWindow infoWindow = new InfoWindow(view, latLng, -47);
                mBaiduMap.showInfoWindow(infoWindow);
                return true;
            }
        });
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

    public LatLng transcoordinate(LatLng lating) {
        //将标准GPS坐标转为百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(lating);
        return converter.convert();
    }
}
