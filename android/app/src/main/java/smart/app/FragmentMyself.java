package smart.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.HashMap;


public class FragmentMyself extends Fragment implements View.OnClickListener {
    // UI Object
    private TextView username;
    private TextView name;
    private TextView nicktname;
    private TextView companyname;
    private TextView departmentname;
    TextView edit;
    Button exit;
    SwipeRefreshLayout swipeRefresh;
    HashMap<String, Object> hashMap;

    private static class handler extends Handler {
        private final WeakReference<FragmentMyself> mActivity;

        private handler(FragmentMyself activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentMyself activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
            }
        }
    }


    private final FragmentMyself.handler handler = new handler(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_me, container, false);
        init(view);
        return view;
    }

    @SuppressWarnings("unchecked")
    // UI组件初始化与事件绑定
    private void init(View view) {
        edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        username = view.findViewById(R.id.usernamevalue);
        name = view.findViewById(R.id.namevalue);
        nicktname = view.findViewById(R.id.nicknamevalue);
        companyname = view.findViewById(R.id.companyname);
        departmentname = view.findViewById(R.id.departmentname);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(this);
        getaccountinfo();

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getaccountinfo();
                    }
                }).start();
            }
        });
    }

    public void getaccountinfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hashMap = HttpService.accountinfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.post(udpUIRunnable);
            }
        }).start();
    }

    @SuppressWarnings("unchecked")
    Runnable udpUIRunnable = new Runnable() {
        @Override
        public void run() {
            username.setText(hashMap.get("username").toString());
            name.setText(hashMap.get("name").toString());
            nicktname.setText(hashMap.get("nickname").toString());
            companyname.setText(hashMap.get("companyname").toString());
            departmentname.setText(hashMap.get("departmentname").toString());
            swipeRefresh.setRefreshing(false);//刷新结束，隐藏刷新进度条

        }
    };


    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.edit:
                Intent intent = new Intent(getActivity(), UpdataInfoActivity.class);
                bundle.putSerializable("accountinfo", hashMap);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.exit:
                System.exit(0);
                break;

        }
    }
}