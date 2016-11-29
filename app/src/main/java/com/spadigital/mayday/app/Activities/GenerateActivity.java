package com.spadigital.mayday.app.Activities;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.spadigital.mayday.app.R;

/**
 * Created by jorge on 24/11/16.
 */
public class GenerateActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registernew_md_id);
        String username = getIntent().getExtras().getString("newUserName");
        TextView tvMdId = (TextView)findViewById(R.id.tv_new_mayday_id);
        tvMdId.setText(username);

    }
}
