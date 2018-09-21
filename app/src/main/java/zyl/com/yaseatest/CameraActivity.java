package zyl.com.yaseatest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.faucamp.simplertmp.RtmpHandler;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;

public class CameraActivity extends Activity implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {
    private SrsCameraView srsCameraView1,srsCameraView2;
    private SrsPublisher mPublisher1,mPublisher2;
    private String url1,url2;
    private PowerManager.WakeLock wakeLock=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);

        keepScreenOn(true);
        initView();
        initPublisher();
    }
    private void keepScreenOn(boolean on) {
        if (on) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if(wakeLock==null) {
                wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, "==KeepScreenOn==");
            }
            wakeLock.acquire();

        } else {
            if (wakeLock != null) {
                wakeLock.release();
                wakeLock = null;
            }
        }
    }
    private void initView(){
        srsCameraView1=findViewById(R.id.cameraview1);
        srsCameraView1.setCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
        srsCameraView2=findViewById(R.id.cameraview2);
    //    srsCameraView2.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }
    private void initPublisher(){
        int type=getIntent().getIntExtra("type",MainActivity.TYPE_CAMERA_BACK);
        if(type==MainActivity.TYPE_CAMERA_FRONT){
            srsCameraView1.setVisibility(View.GONE);
            //声明推流摄像头展示界面对象
            mPublisher2 = new SrsPublisher(srsCameraView2);
            url2=getIntent().getStringExtra("url2");
            srsPublisher(mPublisher2,url2,true);
        }else if(type==MainActivity.TYPE_CAMERA_BACK){
            srsCameraView2.setVisibility(View.GONE);
            mPublisher1 = new SrsPublisher(srsCameraView1);
            url1=getIntent().getStringExtra("url1");
            srsPublisher(mPublisher1,url1,true);
        }else {
            url1=getIntent().getStringExtra("url1");
            url2=getIntent().getStringExtra("url2");
            mPublisher1 = new SrsPublisher(srsCameraView1);
            srsPublisher(mPublisher1,url1,true);
            mPublisher2 = new SrsPublisher(srsCameraView2);
            srsPublisher(mPublisher2,url2,true);
        }
    }
    private void srsPublisher(SrsPublisher srsPublisher,String url,boolean hardEncode){
        srsPublisher.setSendVideoOnly(true);
        //设置编码消息处理
        srsPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        //设置RTMP消息处理
        srsPublisher.setRtmpHandler(new RtmpHandler(this));
        //设置记录消息处理
        srsPublisher.setRecordHandler(new SrsRecordHandler(this));
        //设置展示界面大小
        srsPublisher.setPreviewResolution(640, 480);
        //设置横屏推流 1为竖屏 2为横屏
        srsPublisher.setScreenOrientation(1);
        //设置输出界面大小
        srsPublisher.setOutputResolution(640, 480);
        //设置视频高清模式
        srsPublisher.setVideoHDMode();
        //打开摄像头
        srsPublisher.startCamera();
        //选择软编码/硬编码
        if(hardEncode){
            srsPublisher.switchToHardEncoder();
        }else{
            srsPublisher.switchToSoftEncoder();
        }
        //开始推流
        srsPublisher.startPublish(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        keepScreenOn(false);
        if(mPublisher1!=null){
            mPublisher1.stopPublish();
            srsCameraView1.stopCamera();
        }
        if(mPublisher2!=null){
            mPublisher2.stopPublish();
            srsCameraView2.stopCamera();
        }
    }

    @Override
    public void onNetworkWeak() {

    }

    @Override
    public void onNetworkResume() {

    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRtmpConnecting(String msg) {

    }

    @Override
    public void onRtmpConnected(String msg) {

    }

    @Override
    public void onRtmpVideoStreaming() {

    }

    @Override
    public void onRtmpAudioStreaming() {

    }

    @Override
    public void onRtmpStopped() {

    }

    @Override
    public void onRtmpDisconnected() {

    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {

    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {

    }

    @Override
    public void onRtmpSocketException(SocketException e) {

    }

    @Override
    public void onRtmpIOException(IOException e) {

    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {

    }

    @Override
    public void onRecordPause() {

    }

    @Override
    public void onRecordResume() {

    }

    @Override
    public void onRecordStarted(String msg) {

    }

    @Override
    public void onRecordFinished(String msg) {

    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {

    }

    @Override
    public void onRecordIOException(IOException e) {

    }
}
