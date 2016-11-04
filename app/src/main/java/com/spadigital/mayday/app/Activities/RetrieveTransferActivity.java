package com.spadigital.mayday.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.spadigital.mayday.app.R;


/**
 * Created by jorge on 4/11/16.
 * This is the view that retrieves transfer activity
 */
public class RetrieveTransferActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_transfer);
        Button btnStop = (Button)findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    /*
                    VCard vCard = VCardManager.getInstanceFor(
                            MayDayApplication.getInstance().getConnection()).loadVCard();
                    vCard.setField("redirectTo", null);
                    */
                    Intent principal = new Intent(getApplicationContext(), TaberActivity.class);
                    principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(principal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
