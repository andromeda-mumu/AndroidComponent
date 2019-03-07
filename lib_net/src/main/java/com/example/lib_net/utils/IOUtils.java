package com.example.lib_net.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by wangjiao on 2019/3/7.
 */

public class IOUtils {
    public static byte[] toByteArray(Object input){
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(input);
            os.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            OkLogger.printStackTrace(e);
        }finally {
            IOUtils.closeQuickly(bos);
            IOUtils.closeQuickly(os);
        }
        return null;
    }

    private static void  closeQuickly(Closeable closeable){
        if(closeable==null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
