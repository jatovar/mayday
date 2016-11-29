package com.spadigital.mayday.app.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.spadigital.mayday.app.MayDayApplication;
import com.spadigital.mayday.app.R;
import com.spadigital.mayday.app.Tasks.UserLoginTask;

import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.concurrent.TimeUnit;

/**
 * Created by mayday on 9/09/16.
 */
public class LoginActivity extends AppCompatActivity {

    private String log_v = "Android:";
    final long TIMEOUT = 5000;

    private EditText etMayDayId;
    private EditText etPassword;
    private View mProgressView;

    private SharedPreferences loginPreferences;

    private String mayDayId;
    private String password;
    private Boolean saveLogin;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v(log_v, "onCreate");

        etMayDayId    = (EditText) findViewById(R.id.et_MayDayID);
        etPassword    = (EditText) findViewById(R.id.et_Password);
        mProgressView = findViewById(R.id.login_progress);

        mayDayId = etMayDayId.getText().toString();
        password = etPassword.getText().toString();

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            etMayDayId.setText(loginPreferences.getString("username", ""));
            etPassword.setText(loginPreferences.getString("password", ""));
            //TODO: saveLoginCheckBox.setChecked(true);
        }else{
            etMayDayId.setText("");
            etPassword.setText("");
        }

    }

    public void click_register(View view){
        Intent registerNew = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(registerNew);
    }

    public void click_login(View view){

        Log.v(log_v, "even_login");

        if(TextUtils.isEmpty(mayDayId)) {
            etMayDayId.setError("escriba su MayDayId");
            etMayDayId.requestFocus();
        }else
            if (TextUtils.isEmpty(password)) {
                etPassword.setError("escriba su contraseña");
                etPassword.requestFocus();
            }
            else{
                final UserLoginTask userLoginTask =
                        new UserLoginTask(
                                mayDayId,
                                password,
                                this.getApplicationContext(),
                                mProgressView);

                userLoginTask.execute();

                Thread threadTimeout = new Thread(){
                    public void run(){
                        try {
                            userLoginTask.get(TIMEOUT, TimeUnit.MILLISECONDS);



                        } catch (Exception e) {
                            userLoginTask.cancel(true);
                            LoginActivity.this.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    mProgressView.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this,
                                            "El servidor no ha respondido correctamente, " +
                                                    "reintentar la conexión",
                                            Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    }
                };
                threadTimeout.start();

            }



    }

}
