package com.example.lib_net.callback;

import com.example.lib_net.base.Request;
import com.example.lib_net.module.HttpParams;
import com.example.lib_net.utils.MD5Utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by 16244 on 2019/3/10.
 * 加密
 */

public abstract class EncryptCallback<T> extends JsonCallback<T> {
    private static final Random RANDOM = new Random();
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyz";

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        sign(request.getHttpParams());
    }

    private void sign(HttpParams httpParams) {
        httpParams.put("nonce",getRndStr(6+RANDOM.nextInt(8)));
        httpParams.put("timestamp",""+(System.currentTimeMillis()/1000L));
        //把所有的参数进行md5加密得到sign.
        StringBuilder sb = new StringBuilder();
        HashMap<String,String> hashMap = new HashMap<>();
        for (Map.Entry<String,List<String>> entry:httpParams.urlParamsMap.entrySet()){
            hashMap.put(entry.getKey(),entry.getValue().get(0));
        }
        for (Map.Entry<String,String> entry:getSortedMapByKey(hashMap).entrySet()){
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.delete(sb.length()-1,sb.length());
        String sign = MD5Utils.encode(sb.toString());
        httpParams.put("sign",sign);
    }

    //获取随机送
    private String getRndStr(int length) {
        StringBuffer sb = new StringBuffer();
        char ch;
        for (int i=0;i<length;++i){
            ch = CHARS.charAt(RANDOM.nextInt(CHARS.length()));
            sb.append(ch);
        }
        return sb.toString();
    }

    private Map<String, String> getSortedMapByKey(Map<String, String> map) {
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        Map<String,String> treeMap = new TreeMap<>(comparator);
        for (Map.Entry<String,String> entry:treeMap.entrySet()){
            treeMap.put(entry.getKey(),entry.getValue());
        }
        return treeMap;
    }
}
