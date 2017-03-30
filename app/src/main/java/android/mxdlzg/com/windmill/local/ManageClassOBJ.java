package android.mxdlzg.com.windmill.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.mxdlzg.com.windmill.config.ClassOBJ;
import android.mxdlzg.com.windmill.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 廷江 on 2017/3/25.
 */

public class ManageClassOBJ {


    public ManageClassOBJ() {

    }

    public static void cacheClassList(Context context,long time,List<ClassOBJ> list){
        SharedPreferences.Editor editor = context.getSharedPreferences(Config.CLASSOBJ_CACHE+time,Context.MODE_PRIVATE).edit();
        editor.clear().apply();
        editor.putInt("count",list.size());
        editor.apply();
        for (int i = 0;i<list.size();i++){
            cacheOBJ(editor,list.get(i),i);
        }
    }

    public static void cacheOBJ(SharedPreferences.Editor editor,ClassOBJ classOBJ,int index){
        String config = classOBJ.getALL();
        editor.putString("config"+index,config);
        editor.apply();
        editor.putString("weeks"+index,classOBJ.getWeekString());
        editor.apply();
    }

    public static List<ClassOBJ> getClassList(Context context,long time){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.CLASSOBJ_CACHE+time,Context.MODE_PRIVATE);
        int size = sharedPreferences.getInt("count",0);
        List<ClassOBJ> list = new ArrayList<>();
        for (int i = 0;i<size;i++){
            list.add(getOBJ(sharedPreferences,i));
        }
        return list;
    }

    public static ClassOBJ getOBJ(SharedPreferences sharedPreferences,int index){
        String[] config = sharedPreferences.getString("config"+index,"").split(";");
        String[] weeks = sharedPreferences.getString("weeks"+index,"").split(",");
        ClassOBJ classOBJ = new ClassOBJ(Integer.valueOf(config[0]),config[1],config[2],Integer.valueOf(config[3]),Integer.valueOf(config[4]),Float.valueOf(config[5]),true);
        classOBJ.setWeeks(weeks);
        return classOBJ;
    }


}
