package com.example.ziyi.guoguovideo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.EventListener;

public class MainActivity extends AppCompatActivity {

    private Button playButton ;
    private VideoView videoView ;
    private EditText rtspUrl ;
    private RadioButton radioStream;
    private RadioButton radioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rtspUrl = (EditText)findViewById(R.id.url);
        playButton = (Button)findViewById(R.id.start_play);
        radioStream = (RadioButton)findViewById(R.id.radioButtonStream);
        radioFile = (RadioButton)findViewById(R.id.radioButtonFile);
        videoView = (VideoView)this.findViewById(R.id.rtsp_player);

        playButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                if (radioStream.isChecked()) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                Manifest.permission.INTERNET},2);
                    }else{
                        PlayRtspStream(rtspUrl.getEditableText().toString());
                    }
                }
                else if (radioFile.isChecked()){
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }else{
                        PlayLocalFile(rtspUrl.getEditableText().toString());
                    }

                }
            }
        });


    }

    private void PlayLocalFile(String filePath) {

        File file=new File(Environment.getExternalStorageDirectory() + "/" + filePath);
        if(!file.exists()){
            Toast.makeText(MainActivity.this,"file is not exist!",Toast.LENGTH_SHORT).show();
            return;
        }

        videoView.setVideoPath(file.getPath());
        videoView.requestFocus();
        videoView.start();
    }

    private void PlayRtspStream(String rtspUrl) {
        try{
            videoView.setVideoURI(Uri.parse(rtspUrl));
            videoView.requestFocus();
            videoView.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    PlayLocalFile(rtspUrl.getEditableText().toString());
                }else{
                    Toast.makeText(this,"you denied the request",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    PlayRtspStream(rtspUrl.getEditableText().toString());
                }else{
                    Toast.makeText(this,"you denied the request",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            default:break;

        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(videoView!=null){
            videoView.suspend();
        }
    }
}
