package zyl.com.yaseatest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.github.faucamp.simplertmp.RtmpHandler;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;

public class FrontCameraService extends Service implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener{

    private WindowManager windowManager;
    private LinearLayout linearLayout;

    private SrsCameraView srsCameraView;
    private SrsPublisher mPublisher;

    private static final String URL="rtmp://192.168.0.105:1935/live/front";

    public FrontCameraService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFlotView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPublisher!=null){
            mPublisher.stopPublish();
            srsCameraView.stopCamera();
            if(windowManager!=null&&linearLayout!=null){
                windowManager.removeView(linearLayout);
            }
        }
    }

    private void createFlotView(){
        WindowManager.LayoutParams layoutParams=new WindowManager.LayoutParams();
        windowManager=(WindowManager)getApplication().getSystemService(Context.WINDOW_SERVICE);
        layoutParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format= PixelFormat.RGBA_8888;
        layoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity= Gravity.RIGHT|Gravity.BOTTOM;
        layoutParams.x=0;
        layoutParams.y=0;
        layoutParams.width=600;
        layoutParams.height=600;
        LayoutInflater inflater=LayoutInflater.from(getApplicationContext());
        linearLayout= (LinearLayout) inflater.inflate(R.layout.floatview_layout,null);
        windowManager.addView(linearLayout,layoutParams);
        srsCameraView=linearLayout.findViewById(R.id.cameraview);
        srsCameraView.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mPublisher = new SrsPublisher(srsCameraView);
        srsPublisher(mPublisher,URL,true);
    }
    private void srsPublisher(SrsPublisher srsPublisher, String url, boolean hardEncode){
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
    public void onNetworkWeak() {

    }

    @Override
    public void onNetworkResume() {

    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

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
