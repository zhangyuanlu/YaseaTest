package zyl.com.yaseatest;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayActivity extends Activity{

    private List<IMediaPlayer> players=new ArrayList<>();
    private int type=MainActivity.TYPE_PLAY_SINGLE;
    private String url1,url2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        /*
        type=getIntent().getIntExtra("type",type);
        url1=getIntent().getStringExtra("url1");

        SurfaceView surfaceView1=findViewById(R.id.surfaceview1);
        surfaceView1.setVisibility(View.VISIBLE);
        initIjkPlayer(surfaceView1,url1);

        if(type==MainActivity.TYPE_PLAY_COUPLE){
            url2=getIntent().getStringExtra("url2");
            SurfaceView surfaceView2=findViewById(R.id.surfaceview2);
            surfaceView2.setVisibility(View.VISIBLE);
            initIjkPlayer(surfaceView2,url2);
        }*/
        initView();
    }
    private void initView(){
        SurfaceView surfaceView1=findViewById(R.id.surfaceview1);
        SurfaceView surfaceView2=findViewById(R.id.surfaceview2);
        SurfaceView surfaceView3=findViewById(R.id.surfaceview3);
        SurfaceView surfaceView4=findViewById(R.id.surfaceview4);
        initIjkPlayer(surfaceView1,"rtmp://192.168.43.12:1935/live/room1");
        initIjkPlayer(surfaceView2,"rtmp://192.168.43.12:1935/live/room2");
        initIjkPlayer(surfaceView3,"rtmp://192.168.43.12:1935/live/room3");
        initIjkPlayer(surfaceView4,"rtmp://192.168.43.12:1935/live/room4");
    }
    private void initIjkPlayer(SurfaceView surfaceView,String url){
        IjkMediaPlayer ijkMediaPlayer=new IjkMediaPlayer();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fps", 30);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 1024);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 10);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probsize", "4096");
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", "2000000");
        final IMediaPlayer mediaPlayer=ijkMediaPlayer;
        players.add(mediaPlayer);

        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                if(mediaPlayer!=null){
                    mediaPlayer.start();
                }
            }
        });
        final SurfaceHolder surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(mediaPlayer!=null){
                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.prepareAsync();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(players!=null){
            for (IMediaPlayer player:players){
                if(player.isPlaying()){
                    player.stop();
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(players!=null){
            for (IMediaPlayer player:players){
                if(!player.isPlaying()){
                    player.start();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(players!=null){
            for (IMediaPlayer player:players){
                player.release();
            }
        }
    }
}
