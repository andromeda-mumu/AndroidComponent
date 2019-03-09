package com.example.lib_net.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;

import com.example.lib_net.base.Request;

import java.lang.reflect.Type;

/**
 * Created by 16244 on 2019/3/9.
 */

public abstract class DialogCallback<T> extends JsonCallback<T>{

    private ProgressDialog dialog;

    public DialogCallback(Activity activity){
        super();
        initDialog(activity);
    }

    public DialogCallback(Activity activity,Type type){
        super(type);
        initDialog(activity);
    }
    protected  void initDialog(Activity activity){
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }
}
