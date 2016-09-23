package com.example.diego.mayday_03;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by diego on 9/09/16.
 */
public class ContactAddActivity extends AppCompatActivity {

    private String log_v = "ContactAddActivity:";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        Log.v(log_v, "onCreate");
    }

    public void click_ok(View view){

        EditText contactName = (EditText) findViewById(R.id.et_mayDayID);
        EditText mayDayID    = (EditText) findViewById(R.id.et_contactName);
        Contact contact      = new Contact(
                                            mayDayID.getText().toString(),
                                            contactName.getText().toString(),
                                            ContactStatus.NORMAL
                                            );

        DataBaseHelper db    = new DataBaseHelper(this);
        db.getWritableDatabase();

        Log.v(log_v, "Adding contact: " + contact.getName() + ", " +contact.getMayDayId());


        //If mayDayID is already in the DB.
        long contactId = db.contactAdd(contact);

        if(contactId == -1){
            Context context = getApplicationContext();
            CharSequence text = "MayDayID in use";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {
            contact.setIdAsString(String.valueOf(contactId));
            Intent returnIntent = new Intent();
            returnIntent.putExtra("new_contact_name", contact.getName());
            returnIntent.putExtra("new_contact_MaydayId", contact.getMayDayId());
            returnIntent.putExtra("new_contact_id", contact.getIdAsString());
            setResult(RESULT_OK, returnIntent);
            finish();
        }


    }

    public void click_cancel(View view){

        setResult(RESULT_CANCELED);
        finish();
    }


}

