package com.example.lib_net.utils;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    public static boolean createFloder(String folder) {
        if(!TextUtils.isEmpty(folder)){
            File file = new File(folder);
            return createFolder(file);
        }
        return false;
    }

    private static boolean createFolder(File file) {
        if(file.exists()){
            if(file.isDirectory()) return true;
            file.delete();
        }
       return file.mkdirs();
    }

    public static Object toObject(byte[] input) {
        if(input==null) return null;
        ByteArrayInputStream bis =null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(input);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuickly(bis);
            IOUtils.closeQuickly(ois);
        }
        return null;
    }

    public static void delFileOrFolder(String filePath) {
        if(TextUtils.isEmpty(filePath)) return;
        delFileOrFolder(new File(filePath));
    }

    public static boolean delFileOrFolder(File file) {
        if(file==null || !file.exists()){

        }else if(file.isFile()){
            file.delete();
        }else if(file.isDirectory()){
            File[] files = file.listFiles();
            if(files!=null){
                for(File f:files){
                    delFileOrFolder(f);
                }
            }
            file.delete();
        }
        return true;
    }
}
