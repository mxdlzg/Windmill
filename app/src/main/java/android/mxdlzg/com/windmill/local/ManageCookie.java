package android.mxdlzg.com.windmill.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.mxdlzg.com.windmill.config.Config;

import org.apache.http.client.CookieStore;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Created by 廷江 on 2017/3/23.
 */

public class ManageCookie {
    private CookieStore cookieStore;
    private java.net.CookieStore netCookieStore;
    private Context context;

    /**
     * 构造
     * @param context context
     */
    public ManageCookie(Context context){
        this.context = context;
    }

    /**
     * 存储apache的Cookie
     */
    public void cacheCookie(){

    }

    /**
     * 清空cookieList文件
     * @param name 要清空的cookie文件名
     */
    public void chearAll(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    /**
     * @return 存储的java net cookieStore的存储时间
     */
    public long getNetCacheTime(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.NET_COOKIE_CACHE,Context.MODE_PRIVATE);
        return sharedPreferences.getLong("time",0);
    }

    /**
     * 存储java net cookieStore到文件
     */
    public void cacheNetCookie(){
        SharedPreferences.Editor editor = context.getSharedPreferences(Config.NET_COOKIE_CACHE,Context.MODE_PRIVATE).edit();
        List<HttpCookie> cookies = netCookieStore.getCookies();
        editor.putLong("time",System.currentTimeMillis());
        editor.apply();
        editor.putInt("count",cookies.size());
        editor.apply();
        for (int i = 0;i<cookies.size();i++){
            editor.putString(String.valueOf(i),cookies.get(i).getDomain()+";"+cookies.get(i).getName()+"="+cookies.get(i).getValue());
            editor.apply();
        }
    }

    /**
     * 从文件读取java net CookieStore
     */
    public void getNetCookieFromCache(){
        if (netCookieStore == null){
            initCookieStore();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.NET_COOKIE_CACHE,Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt("count",0);
        long time = sharedPreferences.getLong("time",0);
        for (int i = 0;i<count;i++){
            String cookieData = sharedPreferences.getString(String.valueOf(i),"");
            String domain = cookieData.split(";")[0];
            String value = cookieData.split(";")[1];
            try {
                netCookieStore.add(new URI(domain),new HttpCookie(value.split("=")[0],value.split("=")[1]));
                System.out.println("add"+domain+":"+value);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化java net CookieStore
     * @return 是否完成
     */
    public boolean initCookieStore(){
        netCookieStore = new CookieManager().getCookieStore();
        return true;
    }

    /**
     * @return 返回java net CookieStore
     */
    public java.net.CookieStore getNetCookieStore() {
        return netCookieStore;
    }

    /**
     * @param netCookieStore 设置java net cookieStore
     */
    public void setNetCookieStore(java.net.CookieStore netCookieStore) {
        this.netCookieStore = netCookieStore;
    }

    /**
     * @return 返回apache cookieStore
     */
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * @param cookieStore 设置apache cookieStore
     */
    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
