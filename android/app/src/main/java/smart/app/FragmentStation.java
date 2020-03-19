package smart.app;

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

public class FragmentStation extends Fragment {
    // UI Object
    TextView txt_topbar = null;
    ListView listView = null;
    Gsonparsebean gsonbean = null;
    Gsonparsebean.datainfo datainfo = null;
    Devicesparsebean devicesparsebean = null;
    MonitorListAdapter parentAdapter = null;
    Sensorsbean sensorsbean;
    ArrayList<Sensorsbean.groupsinfo> sensorlist = null;
    ArrayList<Sensorsbean.groupsinfo.sensorsinfo> list = null;
    ArrayList<String> SensorId = null;
    ArrayList<String> title = null;
    String message = null;
    private Timer timer = new Timer();
    HashMap<String, Object> hashMap;
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
            list = new ArrayList<>();
            sensorlist = new ArrayList<>();
            title = new ArrayList<>();
            SensorId = new ArrayList<>();
            devicesparsebean = HttpService.JsonToObject(message, Devicesparsebean.class);
            sensorsbean = new Sensorsbean(devicesparsebean);
            for (int j = 0; j < sensorsbean.Groups.size(); j++) {
                sensorlist.add(sensorsbean.Groups.get(j));
                title.add(sensorsbean.Groups.get(j).Name);
                for (int i = 0; i < sensorlist.get(j).Sensors.size(); i++) {
                    list.add(sensorlist.get(j).Sensors.get(i));
                    SensorId.add(sensorlist.get(j).Sensors.get(i).Id);
                }
            }
            parentAdapter = new MonitorListAdapter(getActivity(), list, SensorId);
            listView.setAdapter(parentAdapter);
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
        gsonbean = (Gsonparsebean) getActivity().getIntent().getSerializableExtra("gsonobject");
        datainfo = gsonbean.data.get(0);
        Bundle bundle = getArguments();
        if (bundle != null) {
            datainfo = (Gsonparsebean.datainfo) mainActivity.fragmentList.get(1).getArguments().getSerializable("datainfo");
        }
        if (datainfo != null) {
            txt_topbar.setText(datainfo.Name);
        }
        listView = view.findViewById(R.id.mylistview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //刷新数据
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            hashMap = HttpService.devicesdata(datainfo.Id, "institute");
                            message = (String) hashMap.get("str");
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
    public void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    }

}
