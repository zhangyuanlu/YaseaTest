package zyl.com.yaseatest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PowerOnReceiver extends BroadcastReceiver {
    private static final String TAG="PowerOnReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        Log.e(TAG,"action="+action);
        if(action.equals("android.intent.action.BOOT_COMPLETED")||action.contains("android.net.wifi")){
            Intent service=new Intent(context,ConnectWifi.class);
            service.setAction(ConnectWifi.ACTION_POWER_ON);
            notifyPowerOn(context);
            context.startService(service);
        }else if(action.equals("android.intent.action.ACTION_SHUTDOWN")){

        }
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
