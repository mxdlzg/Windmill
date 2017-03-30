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

    public ManageCookie(Context context){
        this.context = context;
    }

    public void cacheCookie(){

    }

    public void chearAll(String name){
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

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

    public void getCookieFromCache(String name){
        if (netCookieStore == null){
            initCookieStore();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
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

    public boolean initCookieStore(){
        netCookieStore = new CookieManager().getCookieStore();
        return true;
    }

    public java.net.CookieStore getNetCookieStore() {
        return netCookieStore;
    }

    public void setNetCookieStore(java.net.CookieStore netCookieStore) {
        this.netCookieStore = netCookieStore;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }
}
