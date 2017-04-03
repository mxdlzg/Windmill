package android.mxdlzg.com.windmill.main;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.mxdlzg.com.windmill.R;
import android.mxdlzg.com.windmill.config.Config;
import android.mxdlzg.com.windmill.local.ManageCookie;
import android.mxdlzg.com.windmill.net.GetCookie;
import android.mxdlzg.com.windmill.net.GetCookieTest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import org.apache.http.client.CookieStore;

/**
 * Created by 廷江 on 2017/3/22.
 */

public class LoginActivity extends AppCompatActivity{
    private AppCompatButton buttonLogin;
    private TextInputLayout userLayout;
    private TextInputLayout passwordLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        //user/password
        userLayout = (TextInputLayout) findViewById(R.id.login_userLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.login_passwordLayout);

        userLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    userLayout.setErrorEnabled(false);
                }
            }
        });
        passwordLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    passwordLayout.setErrorEnabled(false);
                }
            }
        });

        //login Button
        buttonLogin = (AppCompatButton) findViewById(R.id.login_btn_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = userLayout.getEditText().getText().toString();
                String password = passwordLayout.getEditText().getText().toString();
                if(user.equals("")){
                    userLayout.setErrorEnabled(true);
                    userLayout.setError("学号不能为空");
                }
                if (password.equals("")){
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("密码不能为空");
                }
                if (!user.equals("")&&!password.equals("")){
                    Login(user,password);
                }
            }
        });

    }

    private int Login(String user,String password){
        final ProgressDialog dialog = ProgressDialog.show(this,"","",true,true);

        new GetCookieTest("http://ems.sit.edu.cn:85/login.jsp", user, password, new GetCookieTest.SuccessCallback() {
            @Override
            public void onSuccess(java.net.CookieStore cookieStore) {
                dialog.dismiss();
                ManageCookie manageCookie = new ManageCookie(LoginActivity.this);
                manageCookie.setNetCookieStore(cookieStore);
                manageCookie.cacheNetCookie();
            }
        }, new GetCookieTest.FailCallback() {
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        },null,null);
        return 0;
    }
}
