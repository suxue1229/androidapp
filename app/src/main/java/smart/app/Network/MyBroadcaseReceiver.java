package smart.app.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.annotation.SuppressLint;

import smart.app.Interface.EventHandle;


@SuppressLint("NewApi")
public class MyBroadcaseReceiver extends BroadcastReceiver {

    EventHandle eventHandle;
    public void setOnCallListener(EventHandle eventHandle){ //接口对象构造(接口没有实例化,因为没有具体方法)
        this.eventHandle=eventHandle;
    }
    /*
     * 触发接口的回调方法，相当于button的点击触发，在主类方法调用该方法触发回调
     */
    public void onCall(boolean isNetConnected){
        eventHandle.onNetChange(isNetConnected);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
    }

}
