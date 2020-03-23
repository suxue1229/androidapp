package smart.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import smart.app.Adapter.DeviceAdapter;
import smart.app.Network.HttpService;
import smart.app.Activity.MainActivity;
import smart.app.R;
import smart.app.bean.Devicebean;
import smart.app.bean.Institutebean;
import smart.app.bean.Sensorbean;

public class FragmentStation extends Fragment {
    // UI Object
    TextView txt_topbar = null;
    ListView device_listView = null;
    ListView sensor_listView = null;
    Institutebean institutebean = null;
    Institutebean.datainfo datainfo = null;
    Devicebean devicebean = null;
    DeviceAdapter parentAdapter_status = null;
    DeviceAdapter parentAdapter_value = null;
    Sensorbean sensorbean;
    HashMap<String,ArrayList<Sensorbean.sensorinfos.sensorinfo>> sensorlist = null;
    ArrayList<Sensorbean.sensorinfos.sensorinfo> status_info=null;
    ArrayList<Sensorbean.sensorinfos.sensorinfo> value_info=null;
    private Timer timer = new Timer();
    MainActivity mainActivity;

    private static class MyHandler extends Handler {
        private final WeakReference<FragmentStation> mActivity;

        private MyHandler(FragmentStation activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentStation activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
            }
        }
    }


    private final MyHandler mHandler = new MyHandler(this);
    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            sensorlist = new HashMap<>();
            status_info=new ArrayList<>();
            value_info=new ArrayList<>();
            sensorbean = new Sensorbean(devicebean);
            for (int i = 0; i < sensorbean.sensors.size(); i++) {
                try {
                    for (int j = 0; j < sensorbean.sensors.get(i).sensor.get("status").size(); j++) {
                        status_info.add(sensorbean.sensors.get(i).sensor.get("status").get(j));
                    }
                }catch(Exception e){
                }

                try {
                    for (int n = 0; n < sensorbean.sensors.get(i).sensor.get("value1").size(); n++) {
                        value_info.add(sensorbean.sensors.get(i).sensor.get("value1").get(n));
                    }
                }catch(Exception e){
                }

                try {
                    for (int m = 0; m < sensorbean.sensors.get(i).sensor.get("value2").size(); m++) {
                        value_info.add(sensorbean.sensors.get(i).sensor.get("value2").get(m));
                    }
                }catch(Exception e){
                }

                }

            parentAdapter_status = new DeviceAdapter(getActivity(), status_info);
            parentAdapter_value=new DeviceAdapter(getActivity(),value_info);
            device_listView.setAdapter(parentAdapter_status);
            sensor_listView.setAdapter(parentAdapter_value);
        }
    };


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplication());
        View view = inflater.inflate(R.layout.fg_station, container, false);
        mainActivity = (MainActivity) getActivity();
        init(view);
        return view;
    }

    @SuppressWarnings("unchecked")
    private void init(View view) {
        txt_topbar = view.findViewById(R.id.txt_topbar);
        institutebean = (Institutebean) getActivity().getIntent().getSerializableExtra("gsonobject");
        datainfo = institutebean.data.get(0);
        Bundle bundle = getArguments();
        if (bundle != null) {
            datainfo = (Institutebean.datainfo) bundle.getSerializable("datainfo");
        }
        if (datainfo != null) {
            txt_topbar.setText(datainfo.Name);
        }
        device_listView = view.findViewById(R.id.device_listview);
        sensor_listView=view.findViewById(R.id.sensor_listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //刷新数据
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            devicebean = HttpService.deviceinfo(datainfo.Id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mHandler.post(sRunnable);
                    }
                }, 0, 5000/* 表示0毫秒之後，每隔5000毫秒執行一次 */);
            }
        }).start();
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    }

}
