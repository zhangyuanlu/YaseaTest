package zyl.com.yaseatest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private Button bt_push,bt_pull;
    private EditText et_push1,et_pull1,et_push2,et_pull2;
    private RadioGroup radioGroup,group_play;
    public static final int TYPE_CAMERA_FRONT=0;
    public static final int TYPE_CAMERA_BACK=1;
    public static final int TYPE_CAMERA_BOTH=2;
    public static final int TYPE_PLAY_SINGLE=1;
    public static final int TYPE_PLAY_COUPLE=2;
    private int type_camera=TYPE_CAMERA_BACK;
    private int type_play=TYPE_PLAY_SINGLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_pull=findViewById(R.id.bt_pull);
        bt_push=findViewById(R.id.bt_push);
        et_pull1=findViewById(R.id.et_pull1);
        et_push1=findViewById(R.id.et_push1);
        et_pull2=findViewById(R.id.et_pull2);
        et_push2=findViewById(R.id.et_push2);
        radioGroup=findViewById(R.id.radiogroup);
        group_play=findViewById(R.id.group_play);
        radioGroup.setOnCheckedChangeListener(this);
        group_play.setOnCheckedChangeListener(this);
        bt_push.setOnClickListener(this);
        bt_pull.setOnClickListener(this);
        //test
        Intent intent=new Intent(this,FrontCameraService.class);
        startService(intent);
        intent=new Intent(this,BackCameraService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_pull:{
                Intent intent=new Intent(MainActivity.this,PlayActivity.class);
                intent.putExtra("url1",et_pull1.getText().toString());
                if(type_play==TYPE_PLAY_COUPLE){
                    intent.putExtra("url2",et_pull2.getText().toString());
                }
                intent.putExtra("type",type_play);
                startActivity(intent);
                break;
            }
            case R.id.bt_push:{
                Intent intent=new Intent(MainActivity.this,CameraActivity.class);
                if(type_camera==TYPE_CAMERA_BOTH){
                    intent.putExtra("url1",et_push1.getText().toString());
                    intent.putExtra("url2",et_push2.getText().toString());
                }else if(type_camera==TYPE_CAMERA_BACK){
                    intent.putExtra("url1",et_push1.getText().toString());
                }else{
                    intent.putExtra("url2",et_push1.getText().toString());
                }
                intent.putExtra("type",type_camera);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.radiogroup:{
                switch (checkedId){
                    case R.id.radiobutton3:{
                        et_push2.setVisibility(View.VISIBLE);
                        type_camera=TYPE_CAMERA_BOTH;
                        break;
                    }
                    case R.id.radiobutton2:{
                        type_camera=TYPE_CAMERA_BACK;
                        et_push2.setVisibility(View.GONE);
                        break;
                    }
                    case R.id.radiobutton1:{
                        type_camera=TYPE_CAMERA_FRONT;
                        et_push2.setVisibility(View.GONE);
                        break;
                    }
                }
                break;
            }
            case R.id.group_play:{
                switch (checkedId){
                    case R.id.couple_play:{
                        et_pull2.setVisibility(View.VISIBLE);
                        type_play=TYPE_PLAY_COUPLE;
                        break;
                    }
                    case R.id.single_play:{
                        et_pull2.setVisibility(View.GONE);
                        type_play=TYPE_PLAY_SINGLE;
                        break;
                    }
                }
                break;
            }
            default:
                break;
        }
    }
}

