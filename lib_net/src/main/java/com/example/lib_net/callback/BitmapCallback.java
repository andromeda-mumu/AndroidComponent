package com.example.lib_net.callback;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.lib_net.convert.BitmapConvert;

import okhttp3.Response;

/**
 * Created by 16244 on 2019/3/9.
 */

public abstract class BitmapCallback extends AbstractCallback<Bitmap> {
    private BitmapConvert bitmapConvert;
    public BitmapCallback(){
        bitmapConvert = new BitmapConvert();
    }
    public BitmapCallback(int maxWidth,int maxHeight){
        bitmapConvert = new BitmapConvert(maxWidth,maxHeight);
    }
    public BitmapCallback(int maxWidth, int maxHeight, Bitmap.Config decodeConfig, ImageView.ScaleType scaleType){
        bitmapConvert = new BitmapConvert(maxWidth,maxHeight,decodeConfig,scaleType);
    }

    @Override
    public Bitmap convertResponse(Response response) throws Throwable {
        Bitmap bitmap = bitmapConvert.convertResponse(response);
        response.close();
        return bitmap;
    }
}
