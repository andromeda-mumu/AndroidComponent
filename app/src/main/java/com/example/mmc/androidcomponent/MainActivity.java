package com.example.mmc.androidcomponent;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.lib_net.OkClient;
import com.example.lib_net.callback.DialogCallback;
import com.example.lib_net.callback.StringCallback;
import com.example.lib_net.module.CommonReqpons;
import com.example.lib_net.module.SimpleResponse;
import com.example.mmc.androidcomponent.json.ConvertJson;
import com.example.mmc.androidcomponent.json.ParamterizedTypeImpl;
import com.example.mmc.androidcomponent.model.MenuBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

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
//                            Log.d("=mmc=","--------"+response.getBody());
                        try {
                            //原始方法，不要用泛型
//                            Gson  gson = new Gson();
//                            CommonReqpons<MenuBean> menuBean = gson.fromJson(response.getBody(),new TypeToken<CommonReqpons<MenuBean>>(){}.getType());
//                            Log.d("=mmc=","--------"+menuBean.result.data.get(0).id);

                            //封装好的方法
//                            Type type = new ParamterizedTypeImpl(CommonReqpons.class,new Class[]{MenuBean.class});
//                            CommonReqpons<MenuBean> commonReqpons =new Gson().fromJson(response.getBody(),type);
                            CommonReqpons<MenuBean> commonReqpons = ConvertJson.fromJsonObject(response.getBody(),MenuBean.class);
                            Log.d("=mmc=","--------"+commonReqpons.result.data.get(0).steps.get(0).step);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        String reason = jsonObject.getString("reason");
                        Log.d("=mmc=","----reason---"+reason);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void syncUpString(View view){
//        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/360/mmx.jpg";
//        File file = new File(fileName);
//        if(file.exists()){
//            Log.d("=mmc=","--exist--");
//        }
//        OkClient.<String>post(url)
//                .tag(this)
//                .params("key","d517491cb99669e8286f2491d22e86cd")
//                .params("menu","红烧肉")
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(com.example.lib_net.module.Response<String> response) {
//                        Log.d("=mmc=","----upString---"+response.getBody());
//
//                    }
//                });

    }

    public void toJavaBean(View view){
        Type type = new ParamterizedTypeImpl(CommonReqpons.class,new Class[]{MenuBean.class});
        OkClient.<CommonReqpons<MenuBean>>post(url)
                .params("key","d517491cb99669e8286f2491d22e86cd")
                .params("menu","红烧肉")
                .execute(new DialogCallback<CommonReqpons<MenuBean>>(this,type){
                    @Override
                    public void onSuccess(com.example.lib_net.module.Response<CommonReqpons<MenuBean>> response) {
                        CommonReqpons<MenuBean> commonReqpons = response.getBody();
                        MenuBean bean =  commonReqpons.result;
                    }
                });
    }
}

