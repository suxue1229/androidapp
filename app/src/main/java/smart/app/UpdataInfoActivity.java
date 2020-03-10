package smart.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;


public class UpdataInfoActivity extends Activity implements View.OnClickListener {
    // UI Object
    TextView txt_topbar;
    private EditText firstname;
    private EditText lastname;
    private EditText nicktname;
    private EditText companyname;
    private EditText departmentname;
    ImageButton back;
    TextView sure;
    Accountbean account;

    private static class handler extends Handler {
        private final WeakReference<UpdataInfoActivity> mActivity;

        private handler(UpdataInfoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdataInfoActivity activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
            }
        }
    }

    private final UpdataInfoActivity.handler handler = new handler(this);
    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                lastname.setText(HttpService.accountbean.data.getLastName());
                firstname.setText(HttpService.accountbean.data.getFirstName());
                nicktname.setText(HttpService.accountbean.data.getNickName());
                companyname.setText(HttpService.accountbean.data.getCompany());
                departmentname.setText(HttpService.accountbean.data.getDepartment());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_edit_menu);
        init();
    }

    @SuppressWarnings("unchecked")
    // UI组件初始化与事件绑定
    private void init() {
        txt_topbar = findViewById(R.id.txt_topbar);
        lastname = findViewById(R.id.editText1);
        firstname = findViewById(R.id.editText2);
        nicktname = findViewById(R.id.editText3);
        companyname = findViewById(R.id.editText4);
        departmentname = findViewById(R.id.editText5);

        back = findViewById(R.id.back);
        sure = findViewById(R.id.sure);
        back.setOnClickListener(this);
        sure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!TextUtils.isEmpty(firstname.getText().toString().trim()) && !TextUtils.isEmpty(lastname.getText().toString().trim()) && !TextUtils.isEmpty(nicktname.getText().toString().trim()) && !TextUtils.isEmpty(companyname.getText().toString().trim()) && !TextUtils.isEmpty(departmentname.getText().toString().trim())) {
                                if(HttpService.authorizeaccess(account)){
                                    HttpService.accountbean.data.setLastName(lastname.getText().toString().trim());
                                    HttpService.accountbean.data.setFirstName(firstname.getText().toString().trim());
                                    HttpService.accountbean.data.setNickName(nicktname.getText().toString().trim());
                                    HttpService.accountbean.data.setCompany(companyname.getText().toString().trim());
                                    HttpService.accountbean.data.setDepartment(departmentname.getText().toString().trim());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.post(udpUIRunnable);
                    }
                }).start();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    Runnable udpUIRunnable = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(lastname.getText().toString().trim())) {
                lastname.setError("姓 字段是必填");
            } else if (TextUtils.isEmpty(firstname.getText().toString().trim())) {
                firstname.setError("名 字段是必填");
            } else if (TextUtils.isEmpty(nicktname.getText().toString().trim())) {
                nicktname.setError("昵称 字段是必填");
            } else if (TextUtils.isEmpty(companyname.getText().toString().trim())) {
                companyname.setError("单位 字段是必填");
            } else if (TextUtils.isEmpty(departmentname.getText().toString().trim())) {
                departmentname.setError("部门 字段是必填");
            }
        }
    };

    @Override
    public void onStart(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    account=HttpService.accountbean;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.post(sRunnable);
            }
        }).start();

        super.onStart();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}