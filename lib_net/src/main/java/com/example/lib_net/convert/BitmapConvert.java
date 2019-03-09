package com.example.lib_net.convert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 16244 on 2019/3/9.
 */

public class BitmapConvert implements Convert<Bitmap> {
    private int maxWidth;
    private int maxHeight;
    private Bitmap.Config decodeConfig;
    private ImageView.ScaleType scaleType;
    public BitmapConvert(){
        this(1000, 1000, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_INSIDE);
    }
    public BitmapConvert(int maxWidth, int maxHeight) {
        this(maxWidth, maxHeight, Bitmap.Config.ARGB_8888, ImageView.ScaleType.CENTER_INSIDE);
    }

    public BitmapConvert(int maxWidth, int maxHeight, Bitmap.Config decodeConfig, ImageView.ScaleType scaleType) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.decodeConfig = decodeConfig;
        this.scaleType = scaleType;
    }

    @Override
    public Bitmap convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if(body==null)return null;
        return parse(body.bytes());
    }

    private Bitmap parse(byte[] bytes) {
        BitmapFactory.Options options =  new BitmapFactory.Options();
        Bitmap bitmap;
        if(maxWidth==0 && maxHeight==0){
            options.inPreferredConfig = decodeConfig;
            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        }else{
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);

            int acturalWidth = options.outWidth;
            int acturalHeight = options.outHeight;

            int desireWidth = getResizedDimension(maxWidth,maxHeight,acturalWidth,acturalHeight,scaleType);
            int desireHeight =  getResizedDimension(maxHeight,maxWidth,acturalHeight,acturalWidth,scaleType);

            options.inJustDecodeBounds = false;
            options.inSampleSize = findBestSampleSize(acturalWidth,acturalHeight,desireWidth,desireHeight);
            Bitmap tempBitmap  = BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);

            if(tempBitmap!=null &&(tempBitmap.getWidth()>desireWidth || tempBitmap.getHeight()>desireHeight)){
               bitmap = Bitmap.createScaledBitmap(tempBitmap,desireWidth,desireHeight,true);
               tempBitmap.recycle();
            }else{
                bitmap = tempBitmap;
            }
        }
        return bitmap;
    }

    private int findBestSampleSize(int acturalWidth, int acturalHeight, int desireWidth, int desireHeight) {
        double wr = acturalWidth/maxWidth;
        double hr = acturalHeight/desireHeight;
        double ratio = Math.min(wr,hr);
        float n = 1.0f;
        while ((n*2)<ratio){
            n*=2;
        }
        return (int) n;
    }

    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary, ImageView.ScaleType scaleType) {

        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            } else {
                return maxPrimary;
            }
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

}
