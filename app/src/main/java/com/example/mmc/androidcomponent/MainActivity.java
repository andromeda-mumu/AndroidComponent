package com.example.mmc.androidcomponent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.lib_net.OkClient;
import com.example.lib_net.callback.StringCallback;

import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String url = "http://apis.juhe.cn/cook/query.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkClient.getInstance().init(getApplication());
    }

    //异步请求
    public void ayncGetNet(View view){
        Log.d("=mmc=","--------"+Thread.currentThread().getName());
        OkClient.<String>get(url)
                .tag(this)
                .params("key","d517491cb99669e8286f2491d22e86cd")
                .params("menu","红烧肉")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.example.lib_net.module.Response<String> response) {
                            Log.d("=mmc=","--------"+response.getBody());
                    }
                });
    }

    /**--------------同步请求 ----------------*/
    public void syncGetNet(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Response response = OkClient.get(url)
                            .params("key","d517491cb99669e8286f2491d22e86cd")
                            .params("menu","红烧肉")
                            .execute();
                    String body = response.body().string();
                    Log.d("=mmc=","----body----"+body);
                }catch(Exception e){
                    Log.d("=mmc=","----异常----");
                }
            }
        }).start();

    }

    public void ayncPostNet(View view){
        OkClient.<String>post(url)
                .tag(this)
                .params("key","d517491cb99669e8286f2491d22e86cd")
                .params("menu","红烧肉")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(com.example.lib_net.module.Response<String> response) {
                        Log.d("=mmc=","----post-aync---"+response.getBody());

                    }
                });

    }
    public void syncPostNet(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = OkClient.post(url)
                            .params("key","d517491cb99669e8286f2491d22e86cd")
                            .params("menu","红烧肉")
                            .execute();
                    String body = response.body().string();
                    Log.d("=mmc=","----post sync----"+body);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }
}

