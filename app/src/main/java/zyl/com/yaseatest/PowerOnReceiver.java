package zyl.com.yaseatest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PowerOnReceiver extends BroadcastReceiver {
    private static final String TAG="PowerOnReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        Intent service=new Intent(context,ConnectWifi.class);
        Log.e(TAG,"action="+action);
        if(action.equals("android.intent.action.BOOT_COMPLETED")){
            service.setAction(ConnectWifi.ACTION_POWER_ON);
            notifyPowerOn(context);
            context.startService(service);
        }else if(action.equals("android.intent.action.ACTION_SHUTDOWN")){

        }
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())){
            //拿到wifi的状态值
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_NEW_STATE,0);
            Log.i(TAG,"wifiState = "+ wifiState);
            switch (wifiState){
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }
        //监听wifi的连接状态即是否连接的一个有效的无线路由
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())){
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (parcelableExtra != null){
                // 获取联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                //获取的State对象则代表着连接成功与否等状态
                NetworkInfo.State state = networkInfo.getState();
                //判断网络是否已经连接
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                Log.i(TAG, "isConnected:" + isConnected);
                if (isConnected) {
                    service.setAction(ConnectWifi.ACTION_READY_PUBLISHER);
                    notifyPowerOn(context);
                    context.startService(service);
                } else {

                }
            }
        }

        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.i(TAG, getConnectionType(info.getType()) + "连上");
                        service.setAction(ConnectWifi.ACTION_READY_PUBLISHER);
                        notifyPowerOn(context);
                        context.startService(service);
                    }
                } else {
                    Log.i(TAG, getConnectionType(info.getType()) + "断开");
                }
            }
        }
    }
    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "3G网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }
    private void notifyPowerOn(Context context){
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder=new Notification.Builder(context.getApplicationContext());
        int defaults=0;
        defaults|=Notification.FLAG_SHOW_LIGHTS;
        defaults|=Notification.DEFAULT_LIGHTS;
        builder.setDefaults(defaults);
        builder.setTicker("Hello");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(1000,builder.build());
    }

}
