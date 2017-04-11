package android.mxdlzg.com.windmill.main;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.mxdlzg.com.windmill.R;
import android.mxdlzg.com.windmill.config.ClassOBJ;
import android.mxdlzg.com.windmill.config.Config;
import android.mxdlzg.com.windmill.config.TermOBJ;
import android.mxdlzg.com.windmill.local.ManageClassOBJ;
import android.mxdlzg.com.windmill.local.ManageCookie;
import android.mxdlzg.com.windmill.local.ManageSetting;
import android.mxdlzg.com.windmill.net.GetCookie;
import android.mxdlzg.com.windmill.net.GetSchedule;
import android.mxdlzg.com.windmill.process.PrepareSchedule;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import noman.weekcalendar.WeekCalendar;

import static android.mxdlzg.com.windmill.util.wdUtil.dip2px;
import static android.mxdlzg.com.windmill.util.wdUtil.getIncrement;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppCompatButton schedule_btn_calendar;
    private WeekCalendar weekCalendar;
    private CalendarView calendarView;
    private FrameLayout schedule_calendar;
    private Spinner spinner;
    private NiceSpinner niceSpinner;
    private ImageView imageView;

    private String yearTerm = "2017春";
    private String cType = "1";
    private String yearTerm2 = "2016-2017第2学期";
    private String scheduleHTML = "";

    private Long currentId; //当前课程表的uuid
    private int currentWeek; //当前周,1开始

    private List<ClassOBJ> classList;

    private CookieStore cookieStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        final Handler scheduleHandler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                scheduleHTML = (String)msg.obj;
                Toast.makeText(MainActivity.this, "课程表获取完毕", Toast.LENGTH_SHORT).show();
            }
        };

        //设置toolbar
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        DateFormat format = new SimpleDateFormat("MM");
        toolbar.setTitle(format.format(System.currentTimeMillis())+"月");
        setSupportActionBar(toolbar);

        //drawerLayout
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        ActionBarDrawerToggle toggle =  new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //初始化设置
        initSetting();

        ManageCookie manageCookie = new ManageCookie(this);
        manageCookie.getNetCookieFromCache();
        CookieManager cookieManager;
        //如果时间大于15分钟，就设置一个新的，否则读取旧cookie并继续使用
        if ((System.currentTimeMillis()-manageCookie.getNetCacheTime())>15*60*60){
            Toast.makeText(this, "cookie过期,请重新登录（暂时使用手动模式）", Toast.LENGTH_SHORT).show();
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        }else {
            Toast.makeText(this, "读取已存储的cookie", Toast.LENGTH_SHORT).show();
            cookieManager = new CookieManager(manageCookie.getNetCookieStore(),CookiePolicy.ACCEPT_ALL);
        }
        //设置默认cookieManager
        CookieHandler.setDefault(cookieManager);

        //niceSpinner设置
        niceSpinner = (NiceSpinner) findViewById(R.id.schedule_niceSpinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("第1周", "第2周", "第3周", "第4周", "第5周", "第6周", "第7周", "第8周", "第9周", "第10周", "第11周", "第12周", "第13周", "第14周", "第15周", "第16周", "第17周", "第18周", "第19周", "第20周"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setTextColor(getColor(R.color.white));
        if (currentWeek >= 0){
            niceSpinner.setSelectedIndex(currentWeek);
        }
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prepareScheduleTable(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //
        if (currentId!=0){
            classList = ManageClassOBJ.getClassList(MainActivity.this,currentId);
            prepareScheduleTable(currentWeek);
        }else {
            classList = new ArrayList<>();
        }

        //获取calendarview
        weekCalendar = (WeekCalendar) findViewById(R.id.schedule_weekCalendar);
        calendarView = (CalendarView) findViewById(R.id.schedule_monthCalendar);
        calendarView.setVisibility(View.GONE);
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //calendar
        schedule_calendar = (FrameLayout) findViewById(R.id.schedule_calendar);
        schedule_btn_calendar = (AppCompatButton) findViewById(R.id.schedule_btn_drop);
        schedule_btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendarView.getVisibility() != View.VISIBLE){
                    animatorShow();
                }else {
                    animatorDismiss();
                }
            }
        });


        //navigationview
        navigationView = (NavigationView) findViewById(R.id.main_navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_slideshow:
                        drawerLayout.closeDrawers();
                        classList = ManageClassOBJ.getClassList(MainActivity.this,2016220172);
                        System.out.println(classList.get(0).getName());
                        Toast.makeText(MainActivity.this,"加载完毕"+ classList.get(0).getName(), Toast.LENGTH_SHORT).show();
                        prepareScheduleTable(currentWeek);
                        break;
                    case R.id.nav_manage:
                        drawerLayout.closeDrawers();
                        prepareScheduleTable(currentWeek);
                        break;
                    case R.id.main_menu_setting:
                        drawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this,SettingActivity.class));
                        break;
                    default:
                        drawerLayout.closeDrawers();
                        getSchedule();
                        PrepareSchedule p = new PrepareSchedule();
                        classList = p.getList(scheduleHTML,false);
                        ManageClassOBJ.cacheClassList(MainActivity.this,2016220172,classList);
                        Toast.makeText(MainActivity.this, "本地初始化完毕（今后无需调用）", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        //imageview
        View headerView = navigationView.inflateHeaderView(R.layout.layout_main_nav_header);
        imageView = (ImageView)(headerView.findViewById(R.id.drawer_imageView));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

    /**
     * 初始化设置
     */
    public void initSetting(){
        currentId = ManageSetting.getLongSetting(this,"id");
        currentWeek = ManageSetting.getIntSetting(this,"currentWeek")+getIncrement(ManageSetting.getLongSetting(this,"time"),System.currentTimeMillis());
    }

    /**
     * 显示calendar
     */
    public void animatorShow(){
        final ValueAnimator animator =  ValueAnimator.ofInt(dip2px(MainActivity.this, Config.CALENDAR_MINHEIGHT),dip2px(MainActivity.this,Config.CALENDAR_MAXHEIGHT));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                schedule_calendar.getLayoutParams().height = (int) animator.getAnimatedValue();
                schedule_calendar.requestLayout();
            }
        });
        animator.setDuration(300);

        final ValueAnimator alphaAnimator = ValueAnimator.ofFloat(1,0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                            System.out.println(alphaAnimator.getAnimatedValue());
                weekCalendar.setAlpha((Float) alphaAnimator.getAnimatedValue());
                calendarView.setAlpha(1-(float)alphaAnimator.getAnimatedValue());
            }
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                weekCalendar.setVisibility(View.GONE);
                calendarView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimator,animator);
        animatorSet.start();
    }

    /**
     * 隐藏calendar
     */
    public void animatorDismiss(){
        final ValueAnimator animator =  ValueAnimator.ofInt(dip2px(MainActivity.this,Config.CALENDAR_MAXHEIGHT),dip2px(MainActivity.this,Config.CALENDAR_MINHEIGHT));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                schedule_calendar.getLayoutParams().height = (int) animator.getAnimatedValue();
                schedule_calendar.requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                weekCalendar.setVisibility(View.VISIBLE);
                calendarView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(300);

        final ValueAnimator alphaAnimator = ValueAnimator.ofFloat(1,0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                            System.out.println(alphaAnimator.getAnimatedValue());
                calendarView.setAlpha((Float) alphaAnimator.getAnimatedValue());
                weekCalendar.setAlpha(1-(float)alphaAnimator.getAnimatedValue());
            }
        });
        alphaAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                weekCalendar.setVisibility(View.VISIBLE);
                calendarView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        alphaAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alphaAnimator,animator);
        animatorSet.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.schedule_add:
                startActivityForResult(new Intent(this,AddScheduleActivity.class),Config.START_ADD_SCHEDULE);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 为layout准备课程表并显示
     * @param thisWeek 0开始
     */
    public void prepareScheduleTable(int thisWeek){
        int cursor = 0;
        int[] colors = new int[12];
        LinearLayout[] linearLayouts = new LinearLayout[7];
        initLinearLayout(linearLayouts);
        initColors(colors);
        boolean[][] table = new boolean[7][12];

        List<ClassOBJ> tempList = new ArrayList<>();
        for (ClassOBJ obj : classList){
            if (thisWeek < obj.getWeeks().length){
                if (obj.getWeek(thisWeek) != null && !obj.getWeek(thisWeek).equals("")){
                    tempList.add(obj);
                }
            }
        }
        Random random = new Random();

        for (int index = 0;index<12;index++){
            for (int day = 0; day < 7;day++){
                if (!table[day][index]){  //如果还没有被赋值or清空
                    ClassOBJ tempOBJ = null;
                    if (tempList.size() != 0){
                        tempOBJ = tempList.get(cursor < tempList.size()?cursor:tempList.size()-1);
                    }
                    if (tempList.size()!=0&&tempOBJ.getDay() == (day+1) && tempOBJ.getIndex() == (index+1)) { //如果上课地点不为空（这周没课） 【todo并且 没有名字】
                        final AppCompatButton button = (AppCompatButton) getLayoutInflater().inflate(R.layout.button_blue,linearLayouts[day],false).findViewById(R.id.button);
                        button.setText(tempOBJ.getName()+"@"+tempOBJ.getWeek(thisWeek));
                        ViewGroup.LayoutParams params = button.getLayoutParams();
                        params.height =dip2px(this,tempOBJ.getNum()*60);
                        button.setLayoutParams(params);
                        ColorStateList lists = getResources().getColorStateList(colors[random.nextInt(12)]);
                        button.setSupportBackgroundTintList(lists);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this,ShowClassDetail.class);
                                intent.putExtra("name",button.getText());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,button,"schedule_detail").toBundle());
                                }else {
                                    startActivity(intent);
                                }
                            }
                        });
                        linearLayouts[day].addView(button);
                        for (int i = 0;i<tempOBJ.getNum();i++){
                            table[day][i+index] = true;
                        }
                        cursor++;
                    }else {
                        if (!table[day][index]){
                            View view = getLayoutInflater().inflate(R.layout.schedule_view,linearLayouts[day],true).findViewById(R.id.schedule_small_view);
                            table[day][index] = true;
                        }
                    }
                }
            }
        }

    }

    public void initLinearLayout(LinearLayout[] linearLayouts){
        for (int i = 0;i<7;i++){
            switch (i){
                case 0:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_mon);
                    break;
                case 1:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_tues);
                    break;
                case 2:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_wed);
                    break;
                case 3:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_thur);
                    break;
                case 4:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_fri);
                    break;
                case 5:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_sat);
                    break;
                case 6:
                    linearLayouts[i] = (LinearLayout) findViewById(R.id.schedule_day_sun);
                    break;
                default:break;
            }
            linearLayouts[i].removeAllViews();
        }
    }

    public void initColors(int[] colors){
        for (int i = 0;i<12;i++){
            switch (i){
                case 0:
                    colors[i] = R.color.amber200;
                    break;
                case 1:
                    colors[i] = R.color.blue200;
                    break;
                case 2:
                    colors[i] = R.color.blue_Grey200;
                    break;
                case 3:
                    colors[i] = R.color.brown200;
                    break;
                case 4:
                    colors[i] = R.color.cyan200;
                    break;
                case 5:
                    colors[i] = R.color.deep_Orange200;
                    break;
                case 6:
                    colors[i] = R.color.deep_Purple200;
                    break;
                case 7:
                    colors[i] = R.color.green200;
                    break;
                case 8:
                    colors[i] = R.color.pink200;
                    break;
                case 9:
                    colors[i] = R.color.indigo200;
                    break;
                case 10:
                    colors[i] = R.color.light_Blue200;
                    break;
                case 11:
                    colors[i] = R.color.teal200;
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Config.ADD_SCHEDULE_OK:
                TermOBJ obj = (TermOBJ) data.getSerializableExtra("termOBJ");
                System.out.println(obj.getName()+","+obj.getId());
                currentId = obj.getId();
                classList = ManageClassOBJ.getClassList(this,currentId);
                prepareScheduleTable(currentWeek);
                ManageSetting.addLongSetting(this,"id",currentId);
                niceSpinner.setSelectedIndex(currentWeek);
                break;
            default:
                break;
        }
    }

    public void getSchedule(){
        String schedule = "";
        AssetManager manager = getAssets();
        try {
            InputStreamReader reader = new InputStreamReader(manager.open("schedule.txt"));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String temp ="";
            while ((temp=bufferedReader.readLine())!=null){
                schedule+=temp;
                schedule+="\n";
            }
            scheduleHTML = schedule;
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            InputStreamReader reader = new InputStreamReader(manager.open("schedule.txt"),"utf-8");
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            String temp ="";
//            while ((temp=bufferedReader.readLine())!=null){
//                schedule+=temp;
//                schedule+="\n";
//            }
//            System.out.println(schedule);
//            scheduleHTML = schedule;
//            manager.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    }


//                    ViewGroup.LayoutParams params =  schedule_calendar.getLayoutParams();
//                    params.height = weekCalendar.getHeight();
//                    schedule_calendar.setLayoutParams(params);

