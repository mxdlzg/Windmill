package android.mxdlzg.com.windmill.main;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.mxdlzg.com.windmill.R;
import android.mxdlzg.com.windmill.config.ClassOBJ;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by 廷江 on 2017/3/18.
 */

public class ShowClassDetail extends AppCompatActivity {
    private Button button;
    private ClassOBJ tempObj = new ClassOBJ(2,"测试课程","老师",1,1,2,false);
    private TextView txt_name;
    private TextView txt_position;
    private TextView txt_time;
    private TextView txt_length;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_schedule_detail);

        //temp
        int[] colors = new int[12];
        initColors(colors);

        //intent
        Intent intent = getIntent();
        ClassOBJ this_obj = intent.getParcelableExtra("classOBJ");

        //TextView
        txt_name = (TextView) findViewById(R.id.schedule_detail_name);
        txt_position = (TextView) findViewById(R.id.schedule_detail_position);
        txt_time = (TextView) findViewById(R.id.schedule_detail_time);
        txt_length = (TextView) findViewById(R.id.schedule_detail_length);

        //ImageView
        imageView = (ImageView) findViewById(R.id.schedule_detail_main_imageView);
        System.out.println(intent.getIntExtra("color",0));
        imageView.setBackgroundColor(getColor(colors[intent.getIntExtra("colorIndex",0)]));

        //初始化
        int time = this_obj.getIndex()+this_obj.getNum()-1;
        txt_name.setText(this_obj.getName());
        txt_position.setText(this_obj.getWeek(intent.getIntExtra("thisWeek",1)));
        txt_time.setText("周"+this_obj.getDay()+"    "+this_obj.getIndex()+"-"+time+"节");
        txt_length.setText(this_obj.getStart()+"-"+this_obj.getEnd()+"周");

        button = (Button) findViewById(R.id.schedule_detail_btn);
        button.setText(getIntent().getStringExtra("name"));
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
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
}
