package com.example.diego.mayday_03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

/**
 * Created by diego on 9/09/16.
 */
public class LoginActivity extends AppCompatActivity {

    private String log_v="Android:";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v(log_v,"onCreate");

    }

    public void click_login(View view){

        Log.v(log_v,"even_login");
        EditText etMayDayId = (EditText) findViewById(R.id.et_MayDayID);
        EditText etPassword = (EditText) findViewById(R.id.et_Password);
        String mayDayId     = etMayDayId.getText().toString();
        String password     = etPassword.getText().toString();

        if(attemptLogin(mayDayId, password)){

            Intent intent = new Intent(getApplicationContext(), TabberActivity.class);
            intent.putExtra("mayDayID", mayDayId);
            intent.putExtra("password", password);

            startActivity(intent);

        }

        else{

            /* TODO:
                -Code unsuccessful login
            */
        }

    }

    private boolean attemptLogin(String mayDayID, String password) {
        /*TODO:
            -Asynck activity
            -authenticate locally
            -show progress
          */

        return true;
    }
}
