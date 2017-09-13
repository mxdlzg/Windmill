package android.mxdlzg.com.windmill.test;

import android.mxdlzg.com.windmill.net.GetExam;
import android.mxdlzg.com.windmill.net.GetTemplate;
import android.mxdlzg.com.windmill.process.PrepareExam;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by 廷江 on 2017/5/1.
 */

@RunWith(AndroidJUnit4.class)
public class TestGetExam {
    @Test
    public void testExam(){
        new GetExam(new GetTemplate.SuccessCallback() {
            @Override
            public void onSuccess(String result) {
                PrepareExam prepareExam = new PrepareExam();
                prepareExam.getExam(result);
            }
        }, new GetTemplate.FailCallback() {
            @Override
            public void onFail() {

            }
        },"http://ems.sit.edu.cn:85/student/main.jsp");
    }
}
