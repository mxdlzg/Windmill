package android.mxdlzg.com.windmill.main;

import android.mxdlzg.com.windmill.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by 廷江 on 2017/4/29.
 */

public class ExamActivity extends AppCompatActivity{
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_exam);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.exam_toolbar);
        setSupportActionBar(toolbar);

    }
}
