package com.spadigital.mayday.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.spadigital.mayday.app.R;

/**
 * Created by jorge on 24/11/16.
 */
public class RegisterActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registernew);

    }

    public void click_new_account(View v){
        Intent registerNew = new Intent(getApplicationContext(), GenerateActivity.class);
        startActivity(registerNew);
        finish();
    }


}
