package com.spadigital.mayday.app.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spadigital.mayday.app.R;
import com.spadigital.mayday.app.Tasks.WebServiceTask;

import java.util.concurrent.TimeUnit;

/**
 * Created by jolrgetovar on 19/12/16.
 */

public class PasswordForgottenActivity extends AppCompatActivity implements View.OnClickListener{

    private Button recoverBtn;
    private EditText tvEmailRecover;
    private EditText tvMaydayIdRecover;

    private String error;
    private final long REQUEST_TIMEOUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psw_forgotten);

        tvMaydayIdRecover = (EditText) findViewById(R.id.et_maydayId_recover);
        tvEmailRecover    = (EditText) findViewById(R.id.et_email_recover);
        recoverBtn        = (Button) findViewById(R.id.btn_recover_psw);

        recoverBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_recover_psw:
                    recoverPasswordService();
                break;
        }
    }

    private void recoverPasswordService() {

        String[] data = new String[2];

        //Position zero is the username (MaydayID)
        data[0] = tvMaydayIdRecover.getText().toString();
        //Position one is the email
        data[1] = tvEmailRecover.getText().toString();

        final WebServiceTask webServiceTask = new WebServiceTask(this, data);
        webServiceTask.execute();

        Thread threadWebservice = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    webServiceTask.get(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                    webServiceTask.cancel(true);
                }
            }
        });

        threadWebservice.start();
    }
}
