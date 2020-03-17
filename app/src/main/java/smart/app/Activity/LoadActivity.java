package smart.app.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import smart.app.Network.HttpService;
import smart.app.R;
import smart.app.bean.Institutebean;
import smart.app.bean.Userbean;

public class LoadActivity extends Activity implements View.OnClickListener {
    // UI Object
    private EditText username;
    private EditText password;
    Button load_bt;
    private CheckBox checkBox1;//记住密码
    private CheckBox checkBox2;//自动登录
    //声明一个SharedPreferences对象和一个Editor对象
    SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    Userbean user;
    String message;

    private static class handler extends Handler {
        private final WeakReference<LoadActivity> mActivity;

        private handler(LoadActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoadActivity activity = mActivity.get();
            if (activity != null) {
                Toast.makeText(activity, (String) msg.obj, Toast.LENGTH_LONG).show();
                super.handleMessage(msg);
            }
        }
    }

    private final LoadActivity.handler handler = new handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.load_main);
        bindViews();
    }

    // UI组件初始化与事件绑定
    private void bindViews() {
        username = findViewById(R.id.editText1);
        password = findViewById(R.id.editText2);
        load_bt = findViewById(R.id.button1);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        //获取preferences和editor对象
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        load_bt.setOnClickListener(this);

        //设置默认不记住密码的状态
        if (preferences.getBoolean("ISCHECK", false)) {
            checkBox1.setChecked(true);
            username.setText(preferences.getString("userName", ""));
            password.setText(preferences.getString("userPassword", ""));
        }
        //判断自动登陆多选框状态
        if (preferences.getBoolean("AUTO_ISCHECK", false)) {
            //设置默认是自动登录状态
            checkBox2.setChecked(true);
            //跳转界面
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (HttpService.authorize(username.getText().toString().trim(),
                                password.getText().toString().trim())) {
                            Institutebean gsonobject = HttpService.instituteInfo();
                            Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("gsonobject", gsonobject);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        //监听记住密码多选框按钮事件
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox1.isChecked()) {
                    editor.putBoolean("ISCHECK", true).commit();
                } else {
                    editor.putBoolean("ISCHECK", false).commit();
                }
            }
        });

        //监听自动登录多选框事件
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox2.isChecked()) {
                    checkBox1.setChecked(true);
                    checkBox1.setEnabled(false);
                    editor.putBoolean("AUTO_ISCHECK", true).commit();
                } else {
                    checkBox1.setEnabled(true);
                    editor.putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        user = new Userbean();
        user.setName(username.getText().toString().trim());
        user.setPassword(password.getText().toString().trim());
        switch (v.getId()) {
            case R.id.button1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (isConnect(getApplicationContext())) {
                            // 验证密码、成功则跳转到MainActivity，否则提示用户名、密码不正确
                            if (user.getName().equals("") || user.getPassword().equals("")) {
                                message = "用户名或者密码不能为空";
                            } else {
                                try {
                                    if (HttpService.authorize(username.getText().toString().trim(),
                                            password.getText().toString().trim())) {
                                        Institutebean gsonobject = HttpService.instituteInfo();
                                        Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("gsonobject", gsonobject);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        if (checkBox1.isChecked()) {
                                            //将用户输入的用户名存入储存中，键为userName
                                            editor.putString("userName", user.getName());
                                            //将用户输入的密码存入储存中，键为userName
                                            editor.putString("userPassword", user.getPassword());
                                            editor.commit();
                                        }
                                    } else {
                                        message = "用户名或密码错误";
                                        editor.remove("userName");
                                        editor.remove("userPassword");
                                        editor.commit();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            message = "网络连接超时，请重新尝试";
                        }
                        Message msg = new Message();
                        msg.obj = message;
                        handler.sendMessage(msg);
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            NetworkInfo.State wifiState;
            NetworkInfo.State mobileState;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (NetworkInfo.State.CONNECTED == wifiState || NetworkInfo.State.CONNECTED == mobileState) {
                return true;
            }
        } catch (Exception e) {
            Log.v("error", e.toString());
        }
        return false;
    }
}
