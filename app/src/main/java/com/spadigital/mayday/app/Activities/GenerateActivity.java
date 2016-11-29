package com.spadigital.mayday.app.Activities;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.spadigital.mayday.app.R;

/**
 * Created by jorge on 24/11/16.
 */
public class GenerateActivity extends AppCompatActivity implements View.OnClickListener{


    private String username;
    private String password;

    private Button buttonStart;
    private TextView tvMdId;
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registernew_md_id);

        username = getIntent().getExtras().getString("newUserName");
        password = getIntent().getExtras().getString("newPassword");

        tvMdId             = (TextView)findViewById(R.id.tv_new_mayday_id);
        buttonStart        = (Button)findViewById(R.id.btn_start);
        saveLoginCheckBox  = (CheckBox)findViewById(R.id.save_login_checkbox);


        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();


        tvMdId.setText(username);
        buttonStart.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view == buttonStart){
            if(saveLoginCheckBox.isChecked()){
                loginPrefsEditor.putBoolean("saveLogin", true);
                loginPrefsEditor.putString("username", username);
                loginPrefsEditor.putString("password", password);
                loginPrefsEditor.commit();
            } else {
                loginPrefsEditor.clear();
                loginPrefsEditor.commit();
            }
            resetToLoginScreen();
        }

    }

    private void resetToLoginScreen() {
        Intent registerNew = new Intent(getApplicationContext(), LoginActivity.class );
        registerNew.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //this clears the Register activity
        startActivity(registerNew);
    }
}
