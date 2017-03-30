package android.mxdlzg.com.windmill.main;

import android.mxdlzg.com.windmill.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

/**
 * Created by 廷江 on 2017/3/18.
 */

public class ShowClassDetail extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_schedule_detail);

        button = (Button) findViewById(R.id.schedule_detail_btn);
        button.setText(getIntent().getStringExtra("name"));
    }
}
