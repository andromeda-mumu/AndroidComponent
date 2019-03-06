package com.example.lib_net.cookie;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.lib_net.utils.OkLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;

import okhttp3.Cookie;

/**
 * Created by wangjiao on 2019/3/6.
 * yyy cookie是用来干嘛的
 */

public class SerializableCookie implements Serializable {
    private static final long serialVersionUID = 8934515377849958333L;

    public static final String HOST ="host";
    public static final String NAME ="name";
    public static final String DOMAIN ="domain";
    public static final String COOKIE ="cookie";

    public String host;
    public String name;
    public String domain;
    private transient Cookie mCookie;
    private transient Cookie mClientCookie;

    public SerializableCookie(String host,Cookie cookie){
        this.host = host;
        this.mCookie = cookie;
        this.domain = cookie.domain();
        this.name = cookie.name();
    }
    public Cookie getCookie(){
        Cookie bestCookie = mCookie;
        if(mClientCookie!=null){
            bestCookie = mClientCookie;
        }
        return bestCookie;
    }
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(mCookie.name());
        out.writeObject(mCookie.value());
        out.writeObject(mCookie.domain());
        out.writeObject(mCookie.expiresAt());
        out.writeObject(mCookie.path());
        out.writeObject(mCookie.secure());
        out.writeObject(mCookie.hostOnly());
        out.writeObject(mCookie.httpOnly());
        out.writeObject(mCookie.persistent());

    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        long expiresAt = in.readLong();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();
        boolean secure = in.readBoolean();
        boolean httpOnly = in.readBoolean();
        boolean hostOnly = in.readBoolean();
        boolean persistent = in.readBoolean();
        Cookie.Builder builder = new Cookie.Builder();
        builder = builder.name(name).value(value).expiresAt(expiresAt).path(path);
        builder = hostOnly?builder.hostOnlyDomain(domain):builder.domain(domain);
        builder = secure?builder.secure():builder;
        mClientCookie = builder.build();
    }
    public static SerializableCookie parseCursorToBean(Cursor cursor) {
        String host = cursor.getString(cursor.getColumnIndex(HOST));
        byte[] cookieBytes = cursor.getBlob(cursor.getColumnIndex(COOKIE));
        Cookie cookie = bytesToCookie(cookieBytes);
        return new SerializableCookie(host,cookie);
    }
    public static ContentValues getContentValues(SerializableCookie serializableCookie){
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOST,serializableCookie.host);
        contentValues.put(DOMAIN,serializableCookie.domain);
        contentValues.put(NAME,serializableCookie.name);
        contentValues.put(COOKIE,cookieToBytes(serializableCookie.host,serializableCookie.mCookie));
        return contentValues;
    }

    /**--------------cookie序列化为String----------------*/
    public static String encodeCookie(String host,Cookie cookie){
        if(cookie==null)return null;
        byte[] cookieBytes = cookieToBytes(host,cookie);
        return byteArrayToHexString(cookieBytes);
    }
    /**--------------字符串反序列化为cookie----------------*/
    public static Cookie decodeCoolie(String cookieString){
        byte[] bytes = hexStringToByteArray(cookieString);
        return bytesToCookie(bytes);
    }

    public static byte[] cookieToBytes(String host,Cookie cookie){
        SerializableCookie serializableCookie = new SerializableCookie(host,cookie);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(serializableCookie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();

    }


    private static Cookie bytesToCookie(byte[] cookieBytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cookieBytes);
        Cookie  cookie = null;
        try {
            ObjectInputStream in = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie)in.readObject()).getCookie();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        }
        return cookie;
    }

    /**--------------二进制数组转成十六进制字符串----------------*/
    public static String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder(bytes.length*2);
        for (byte element:bytes){
            int v = element & 0xff;
            if(v<16){
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**--------------十六进制字符串转换成二进制数组----------------*/
    private static byte[] hexStringToByteArray(String cookieString) {
        int len =cookieString.length();
        byte[] bytes = new byte[len/2];
        for(int i=0;i<len;i+=2){ //yyy 这个转换看不太懂
            bytes[i/2] = (byte)((Character.digit(cookieString.charAt(i),16)<<4)+Character.digit(cookieString.charAt(i+1),16));
        }
        return bytes;
    }

    /**--------------host name domain 表示cookie唯一----------------*/
    @Override
    public boolean equals(Object obj) {
        if(this==obj)return true;
        if(obj==null || getClass()!=obj.getClass()) return false;
        SerializableCookie that = (SerializableCookie) obj;
        if(host!=null? !host.equals(that.host) : that.host!=null) return false;
        if(name!=null?!name.equals(that.name):that.name!=null)return false;
        return domain!=null?domain.equals(that.domain):that.domain==null;
    }


    @Override
    public int hashCode() {
        int result = host!=null?host.hashCode():0;
        result = 31*result+(name!=null?name.hashCode():0);
        result = 31*result+(domain!=null?domain.hashCode():0);
        return result;
    }
}
