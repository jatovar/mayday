package com.example.diego.mayday_03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * Created by diego on 9/09/16.
 */
public class LoginActivity extends AppCompatActivity {

    private String log_v = "Android:";
    private View mProgressView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v(log_v, "onCreate");
        mProgressView = findViewById(R.id.login_progress);

    }

    public void click_login(View view){

        Log.v(log_v, "even_login");
        EditText etMayDayId = (EditText) findViewById(R.id.et_MayDayID);
        EditText etPassword = (EditText) findViewById(R.id.et_Password);
        String mayDayId     = etMayDayId.getText().toString();
        String password     = etPassword.getText().toString();
        if(TextUtils.isEmpty(mayDayId)) {
            etMayDayId.setError("escriba su MayDayId");
            etMayDayId.requestFocus();
        }else
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("escriba su contraseña");
                etPassword.requestFocus();
            }
            else{
                UserLoginTask userLoginTask =
                        new UserLoginTask(
                                mayDayId,
                                password,
                                (MyApplication)getApplication(),
                                this.getApplicationContext(),
                                mProgressView,
                                etPassword);
                userLoginTask.execute();
            }



    }

}
