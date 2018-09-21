package zyl.com.yaseatest;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * 在子线程中执行连网操作
 */
public class ConnectWifi extends IntentService {
    private static final String TAG="ConnectWifi";
    public static final String ACTION_POWER_ON="power_on";
    public static final String ACTION_POWER_OFF="power_off";
    private static final String SSID="Apollo";
    private static final String PWD="zhangyuanlu";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * ConnectWifiService Used to name the worker thread, important only for debugging.
     */
    public ConnectWifi() {
        super("ConnectWifiService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action=intent.getAction();
        if(action.equals(ACTION_POWER_ON)){
            if(!isWifiEnable()){
                if(!setWifiEnable(true)){
                    return;
                }
            }
            if(!isConnectedWifi(SSID)) {
                tryToConnectWifi(SSID, PWD);
            }
            if(isConnectedWifi(SSID)) {
                String ipAddress=getIpAddress();
                Log.e(TAG,"ipAddress="+ipAddress);
                startPublisher(ipAddress);
            }
        }else if(action.equals(ACTION_POWER_OFF)){

        }
    }

    /**
     * 启动后摄开始推流
     */
    private void startPublisher(String ipAddress){
        String URL1="rtmp://"+ipAddress+":1935/live/room1";
        Intent intent=new Intent(this,CameraActivity.class);
        intent.putExtra("url1",URL1);
        intent.putExtra("type",MainActivity.TYPE_CAMERA_BACK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 获得字符串类型的IP地址
     * @return
     */
    private String getIpAddress() {
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo=wifiManager.getDhcpInfo();
        int ip=dhcpInfo.serverAddress;
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }
    /**
     * 判断wifi是否打开
     * @return
     */
    private boolean isWifiEnable(){
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager!=null) {
            return wifiManager.isWifiEnabled();
        }
        return false;
    }

    /**
     * 打开wifi
     * @return
     */
    private boolean setWifiEnable(boolean enable){
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager!=null) {
            return wifiManager.setWifiEnabled(enable);
        }
        return true;
    }
    /**
     * 判断当前是否连接到此ssid
     * @param ssid
     * @return
     */
    private boolean isConnectedWifi(String ssid){
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager!=null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSSID().equals("\""+ssid+"\"");
        }
        return false;
    }

    /**
     * 尝试连接到该ssid
     * @param ssid
     * @param pwd
     * @return
     */
    private boolean tryToConnectWifi(String ssid,String pwd){
        WifiManager wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config=configWifiInfo(ssid,pwd);
        if(config!=null&&wifiManager!=null) {
            int netId = config.networkId;
            if (netId == -1) {
                netId = wifiManager.addNetwork(config);
            }
            return wifiManager.enableNetwork(netId, true);
        }else{
            return false;
        }
    }

    /**
     * 获得该ssid的config信息
     * @param SSID
     * @param password
     * @return
     */
    private WifiConfiguration configWifiInfo( String SSID, String password) {
        int type=0;
        WifiConfiguration config = null;
        WifiManager mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            if(existingConfigs==null){
                return null;
            }
            for (WifiConfiguration existingConfig : existingConfigs) {
                Log.d(TAG,"existingConfig.SSID="+existingConfig.SSID);
                if (existingConfig.SSID.equals(SSID)){
                    config = existingConfig;
                    break;
                }
            }
            List<ScanResult> scanResults=mWifiManager.getScanResults();
            for(ScanResult scanResult:scanResults){
                if(scanResult.SSID.equals(SSID)){
                    type=getType(scanResult);
                }
            }
        }
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
    /**
     *获取热点的加密类型
     */
    private int getType(ScanResult scanResult){
        if (scanResult.capabilities.contains("WPA"))
            return 2;
        else if (scanResult.capabilities.contains("WEP"))
            return 1;
        else
            return 0;
    }
}
