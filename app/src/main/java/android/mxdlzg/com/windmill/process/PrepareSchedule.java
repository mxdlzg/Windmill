package android.mxdlzg.com.windmill.process;

import android.mxdlzg.com.windmill.config.ClassOBJ;
import android.mxdlzg.com.windmill.main.MainActivity;
import android.mxdlzg.com.windmill.net.GetCookie;
import android.mxdlzg.com.windmill.net.GetSchedule;
import android.os.Message;
import android.widget.Toast;

import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 廷江 on 2017/3/15.
 */

public class PrepareSchedule {
    List<ClassOBJ> classOBJList = new ArrayList<>();

    public List<ClassOBJ> getList(String result) {
        Document document = Jsoup.parse(result, "utf-8");
        Elements elements = document.select("tr[bgcolor]");


        for (int indexs = 0; indexs < 12; indexs++) {   //行
            Elements classes = elements.get(indexs).children(); //一行的所有格子
            if (indexs < 2 || indexs > 12) {          //控制分析的行数
                continue;
            }
            for (int days = 0; days < classes.size(); days++) {  //每行的每个格子，day代表格子所在的周几
                Elements smash = classes.get(days).select("div[name='d1']");        //一个格子中课程的破碎状态
                if (smash.size() == 0) {
                    continue;
                }

                List<String> names = new ArrayList<>();
                List<String> teachers = new ArrayList<>();
                Map<String, Integer> list_obj_pair = new HashMap<>();

                String name = "", week = "", position = "", teacher = "";
                int start = 0, end = 0;

                for (Element sms : smash) {
                    name = sms.childNode(0).attr("text");
                    teacher = sms.childNode(4).attr("text").split(" ")[1];
                    week = sms.childNode(2).attr("text").split(" ")[0];
                    position = sms.childNode(2).attr("text").split(" ")[1];

                    //匹配周数
                    String regexs = "(?<=(第)).*?(?=(周))";
                    Pattern patterns = Pattern.compile(regexs);
                    Matcher matchers = patterns.matcher(week);
                    if (matchers.find()) {
                        start = Integer.valueOf(matchers.group().split("-")[0]);
                        if (matchers.group().length() > 1 && matchers.group().split("-").length>1){
                            end = Integer.valueOf(matchers.group().split("-")[1]);
                        } else {
                            end = start;
                        }
                    }

                    if (!names.contains(name) && !teachers.contains(teacher)) {
                        ClassOBJ classOBJ = new ClassOBJ();
                        classOBJ.setName(name);
                        classOBJ.setTecher(teacher);
                        classOBJ.setDay(days);
                        classOBJ.setIndex(indexs - 1);
                        classOBJ.setWeeks(new String[20]);
                        classOBJ.setNum(Integer.valueOf(classes.get(days).attr("rowspan")));

                        //设置第一次分析的数据
                        if (sms.html().contains("**")) {
                            //设置每周上课地点
                            for (int i = (start % 2 == 0 ? start : start + 1); i <= end; i += 2) {
                                classOBJ.setWeek(i - 1, position);
                            }
                        } else if (sms.html().contains("*")) {
                            for (int i = (start % 2 == 0 ? start + 1 : start); i <= end; i += 2) {
                                classOBJ.setWeek(i - 1, position);
                            }
                        } else {
                            for (int i = start; i <= end; i++) {
                                classOBJ.setWeek(i - 1, position);
                            }
                        }

                        classOBJList.add(classOBJ);
                        names.add(name);
                        teachers.add(teacher);
                        list_obj_pair.put(sms.childNode(0).attr("text"), classOBJList.size() - 1);
                    } else {
                        ClassOBJ currentOBJ = classOBJList.get(list_obj_pair.get(name));

                        //设置第一次分析的数据
                        if (sms.html().contains("**")) {
                            //设置每周上课地点
                            for (int i = (start % 2 == 0 ? start : start + 1); i <= end; i += 2) {
                                currentOBJ.setWeek(i - 1, position);
                            }
                        } else if (sms.html().contains("*")) {
                            for (int i = (start % 2 == 0 ? start + 1 : start); i <= end; i += 2) {
                                currentOBJ.setWeek(i - 1, position);
                            }
                        } else {
                            for (int i = start; i <= end; i++) {
                                currentOBJ.setWeek(i - 1, position);
                            }
                        }

                    }
                }
            }
        }

        return classOBJList;
    }
}
