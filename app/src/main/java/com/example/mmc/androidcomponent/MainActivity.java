package com.example.mmc.androidcomponent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.lib_net.OkClient;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String url = "http://apis.juhe.cn/cook/query.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //异步请求
    public void syncNet(View view){

    }

    /**--------------同步请求 ----------------*/
    public void ayncNet(View view){
//        OkClient.<String>get(url)
//                .tag("01")
//                .params("key","d517491cb99669e8286f2491d22e86cd")
//                .params("menu","红烧肉")
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        //yyy 这应该是在子线程吧
//                        Log.d("=mmc=","----thread----"+Thread.currentThread().getName());
//                    }
//                });

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
}
