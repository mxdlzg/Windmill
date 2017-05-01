package android.mxdlzg.com.windmill.main;

import android.mxdlzg.com.windmill.R;
import android.mxdlzg.com.windmill.adapter.ExamAdapter;
import android.mxdlzg.com.windmill.local.ManageExam;
import android.mxdlzg.com.windmill.net.GetExam;
import android.mxdlzg.com.windmill.net.GetTemplate;
import android.mxdlzg.com.windmill.process.PrepareExam;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 廷江 on 2017/4/29.
 */

public class ExamActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ExamAdapter adapter;
    private SwipeRefreshLayout swipeLayout;
    private List<String> exams = new ArrayList<>();

    private Handler rcy_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            PrepareExam prepareExam = new PrepareExam();
            exams = prepareExam.getExam(String.valueOf(msg.obj));
            adapter.setExams(exams);
            adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            ManageExam.cacheExam(ExamActivity.this,exams);
            swipeLayout.setRefreshing(false);
            Toast.makeText(ExamActivity.this, "获取完毕", Toast.LENGTH_SHORT).show();
            return true;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_exam);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.exam_toolbar);
        toolbar.setTitle("Exam");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        //swipe
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.exam_swipeLayout);
        swipeLayout.setColorSchemeResources(R.color.blue400,R.color.green400,R.color.orange400,R.color.red400);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取
                new GetExam(new GetTemplate.SuccessCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Message message = new Message();
                        message.obj = result;
                        rcy_handler.sendMessage(message);
                    }
                }, new GetTemplate.FailCallback() {
                    @Override
                    public void onFail() {

                    }
                },"http://ems.sit.edu.cn:85/student/main.jsp");
            }
        });

        List<String> cacheList = ManageExam.getExamList(this);
        if (cacheList.size()<=0){
            //获取
            Toast.makeText(this, "从网络获取", Toast.LENGTH_SHORT).show();
            new GetExam(new GetTemplate.SuccessCallback() {
                @Override
                public void onSuccess(String result) {
                    Message message = new Message();
                    message.obj = result;
                    rcy_handler.sendMessage(message);
                }
            }, new GetTemplate.FailCallback() {
                @Override
                public void onFail() {

                }
            },"http://ems.sit.edu.cn:85/student/main.jsp");
        }else {
            Toast.makeText(this, "读取存储", Toast.LENGTH_SHORT).show();
            exams = cacheList;
        }




        //recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.exam_recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new ExamAdapter(exams,this);
        recyclerView.setAdapter(adapter);

    }
}
