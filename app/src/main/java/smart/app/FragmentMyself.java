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
import android.widget.TextView;

import java.lang.ref.WeakReference;


public class FragmentMyself extends Fragment implements View.OnClickListener {
    // UI Object
    private TextView username;
    private TextView name;
    private TextView nicktname;
    private TextView companyname;
    private TextView departmentname;
    TextView edit;
    TextView exit;
    SwipeRefreshLayout swipeRefresh;
    Accountbean accountbean;

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
    @SuppressWarnings("unchecked")
    Runnable udpUIRunnable = new Runnable() {
        @Override
        public void run() {
            username.setText(HttpService.accountbean.data.getUserName());
            name.setText(HttpService.accountbean.data.getLastName()+HttpService.accountbean.data.getFirstName());
            nicktname.setText(HttpService.accountbean.data.getNickName());
            companyname.setText(HttpService.accountbean.data.getCompany());
            departmentname.setText(HttpService.accountbean.data.getDepartment());
            swipeRefresh.setRefreshing(false);//刷新结束，隐藏刷新进度条
        }
    };

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

        /*  在Android4.0以后，会发现，只要是写在主线程（就是Activity）中的HTTP请求，运行时都会报错，这是因为Android在4.0以后为了防止应用的ANR（Aplication Not Response）异常，Android这个设计是为了防止网络请求时间过长而导致界面假死的情况发生。*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    accountbean=HttpService.accountinfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.post(udpUIRunnable);
            }
        }).start();

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                changeaccountinfo();
            }
        });
    }

    public void changeaccountinfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(HttpService.authorizeaccess(accountbean)) {
                        accountbean=HttpService.accountbean;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.post(udpUIRunnable);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
//        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.edit:
                Intent intent = new Intent(getActivity(), UpdataInfoActivity.class);
//                bundle.putSerializable("accountbean", accountbean);
//                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.exit:
                System.exit(0);
                break;

        }
    }
    @Override
    public void onStart(){
        if(accountbean!=null){
            changeaccountinfo();
        }
        super.onStart();
    }
}