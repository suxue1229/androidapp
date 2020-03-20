package smart.app.Network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.annotation.SuppressLint;
import smart.app.Activity.MainActivity;


@SuppressLint("NewApi")
public class NetBroadcastReceiver extends BroadcastReceiver {

    public EventHandle eventHandle= MainActivity.eventHandle;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 网络状态发生了变化
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            boolean netWorkState = NetUtil.isNetConnected(context);
            // 接口回调传过去状态的类型!!!!!!!!!
            eventHandle.onNetChange(netWorkState);
        }
    }
    public interface EventHandle{
      void onNetChange(boolean isNetConnected);
  }
}
