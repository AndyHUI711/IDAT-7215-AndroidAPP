package com.example.imagerecognitiondemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_launch);

        Thread myThread=new Thread(){//创建子线程
            @Override
            public void run() {
                try{
                    sleep(3000);//使程序休眠3秒
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);//启动MainActivity
                    startActivity(intent);
                    finish();//关闭当前活动
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        myThread.start();//启动线程
    }
}