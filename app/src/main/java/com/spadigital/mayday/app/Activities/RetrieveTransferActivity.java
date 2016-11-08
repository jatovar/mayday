package com.spadigital.mayday.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.spadigital.mayday.app.MayDayApplication;
import com.spadigital.mayday.app.R;

import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;


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
                    VCard vCard = VCardManager.getInstanceFor(
                            MayDayApplication.getInstance().getConnection()).loadVCard();
                    String userTo = vCard.getField("redirectTo");
                    /*

                    vCard.setField("redirectTo", null);
                    */
                    Intent principal = new Intent(getApplicationContext(), TaberActivity.class);
                    principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(principal);

                    if(!isFinishing()) {
                        MayDayApplication.getInstance().sendCustomMessage(userTo,
                                "El usuario " +
                                        MayDayApplication.getInstance().getConnection().getUser() +
                                        " ha dejado de compartir su cuenta");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
