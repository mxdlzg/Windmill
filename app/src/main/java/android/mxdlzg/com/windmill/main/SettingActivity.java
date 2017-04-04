package android.mxdlzg.com.windmill.main;

import android.mxdlzg.com.windmill.R;
import android.mxdlzg.com.windmill.local.ManageSetting;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by 廷江 on 2017/4/4.
 */

public class SettingActivity extends AppCompatActivity {
    private NiceSpinner niceSpinner;
    private Toolbar toolbar;

    private int currentWeek;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);

        //读取设置
        initData();

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);

        //spinner
        niceSpinner = (NiceSpinner) findViewById(R.id.setting_weeks_spinner);
        niceSpinner.attachDataSource(new LinkedList<>(Arrays.asList("第1周", "第2周", "第3周", "第4周", "第5周", "第6周", "第7周", "第8周", "第9周", "第10周", "第11周", "第12周", "第13周", "第14周", "第15周", "第16周", "第17周", "第18周", "第19周", "第20周")));
        niceSpinner.setSelectedIndex(currentWeek);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ManageSetting.addIntSetting(SettingActivity.this,"currentWeek",position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData(){
        currentWeek = ManageSetting.getIntSetting(this,"currentWeek");
    }
}
