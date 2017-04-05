package android.mxdlzg.com.windmill.main;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.mxdlzg.com.windmill.R;
import android.mxdlzg.com.windmill.adapter.AddScheduleAdapter;
import android.mxdlzg.com.windmill.config.ClassOBJ;
import android.mxdlzg.com.windmill.config.Config;
import android.mxdlzg.com.windmill.config.TermOBJ;
import android.mxdlzg.com.windmill.local.ManageClassOBJ;
import android.mxdlzg.com.windmill.local.ManageSchedule;
import android.mxdlzg.com.windmill.net.GetScheduleTest;
import android.mxdlzg.com.windmill.process.PrepareSchedule;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.CharacterPickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 廷江 on 2017/3/26.
 */

public class AddScheduleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private AppCompatButton btn_newTerm;
    private NumberPicker yearPicker,termPicker;
    private View newTermView;
    private AddScheduleAdapter addScheduleAdapter;

    private String[] termTime = {"2016220171","2016220172"};
    private List<TermOBJ> list = new ArrayList<>();

    private Handler handler_notifiy_add;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_schedule_add);

        //handler
        handler_notifiy_add = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                addScheduleAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                return false;
            }
        });

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.schedule_add_toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));
        toolbar.setTitle("课程表");
        setSupportActionBar(toolbar);

        //获取数据
        ManageSchedule.getAll(this,list);


        //recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.schedule_add_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        addScheduleAdapter = new AddScheduleAdapter(this,list);
        recyclerView.setAdapter(addScheduleAdapter);

        //btn
        btn_newTerm = (AppCompatButton) findViewById(R.id.schedule_add_btn_newTerm);
        btn_newTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //newTermView
                newTermView = LayoutInflater.from(AddScheduleActivity.this).inflate(R.layout.layout_schedule_new_term,null);
                //picker
                yearPicker = (NumberPicker) newTermView.findViewById(R.id.schedule_add_year_picker);
                String[] years = {"2016秋","2017春"};
                yearPicker.setDisplayedValues(years);
                yearPicker.setMaxValue(years.length-1);
                yearPicker.setMinValue(0);
                yearPicker.setValue(0);
                yearPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                termPicker = (NumberPicker) newTermView.findViewById(R.id.schedule_add_term_picker);
                String[] terms = {"2016-2017第1学期","2016-2017第2学期"};
                termPicker.setDisplayedValues(terms);
                termPicker.setMaxValue(terms.length-1);
                termPicker.setMinValue(0);
                termPicker.setValue(0);
                termPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                //dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AddScheduleActivity.this, "取消", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println(yearPicker.getDisplayedValues()[yearPicker.getValue()]);
                        System.out.println(termPicker.getDisplayedValues()[termPicker.getValue()]);
                        // TODO: 2017/3/27 通知getSchedule去获取，cookie使用系统内存中的，如果获取完毕就启动分析函数，分析完毕
                        // TODO: 2017/3/27 写入schedulelist文件，和一个新的schedule+uuid的文件，以后启动就获取新文件
                        final ProgressDialog getScheduleDialog = ProgressDialog.show(AddScheduleActivity.this,"获取中","正在获取schedule",true,false);
                        new GetScheduleTest(new GetScheduleTest.SuccessCallback() {
                            @Override
                            public void onSuccess(String result) {
                                getScheduleDialog.setMessage("schedule获取完毕");
                                System.out.println(result);
                                Toast.makeText(AddScheduleActivity.this, result.substring(0,200), Toast.LENGTH_SHORT).show();

                                //准备分析schedule
                                getScheduleDialog.setTitle("分析中");
                                getScheduleDialog.setMessage("正在分析schedule");
                                PrepareSchedule prepareSchedule = new PrepareSchedule();
                                List<ClassOBJ> newScheduleList = prepareSchedule.getList(result);

                                //分析
                                getScheduleDialog.setMessage("正在存储");
                                ManageClassOBJ.cacheClassList(AddScheduleActivity.this,Long.valueOf(termTime[termPicker.getValue()]),newScheduleList);
                                getScheduleDialog.dismiss();
                                System.out.println("获取、分析、存储完毕");

                                //更新ui
                                String name = termPicker.getDisplayedValues()[termPicker.getValue()];
                                int start = Integer.valueOf(name.substring(0,4));
                                int end = Integer.valueOf(name.substring(5,9));
                                int num = Integer.valueOf(name.substring(10,11));
                                Long id = Long.valueOf((start+"2"+end+num).toString());
                                TermOBJ termOBJ = new TermOBJ(name,start,end,num,id);
                                list.add(termOBJ);

                                // TODO: 2017/3/28 cache Schedule信息到文件
                                ManageSchedule.addSchedule(AddScheduleActivity.this,termOBJ);

                                // TODO: 2017/3/28 handler更新ui
                                handler_notifiy_add.sendMessage(new Message());
                            }
                        }, new GetScheduleTest.FailCallback() {
                            @Override
                            public void onFail() {
                                System.out.println("addSchedule中获取课程表失败");
                            }
                        },"http://ems.sit.edu.cn:85/student/selCourse/syllabuslist.jsp",yearPicker.getDisplayedValues()[yearPicker.getValue()],"1",termPicker.getDisplayedValues()[termPicker.getValue()]);

                    }
                });
                builder.setView(newTermView);
                builder.setCancelable(true);
                builder.show();


            }
        });





    }
}
