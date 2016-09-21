package com.example.diego.mayday_03;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Created by jorge on 20/09/16.
 */
public class ContactInformationActivity extends AppCompatActivity{

    private String log_v = "ContactInfoActivity:";
    private String contactMayDayID;
    private String contactName;
    private ContactStatus contactStatus;
    private Button btnAction;
    private boolean modifed = false;
    private DataBaseHelper db;
    EditText etContactId;
    EditText etContactName;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_information);
        Log.v(log_v, "onCreate");
        Log.v(log_v, "Starting editing with MayDayID: " + contactMayDayID);

        contactMayDayID = getIntent().getExtras().getString("contact_MayDayID");
        contactName     = getIntent().getExtras().getString("contact_name");
        contactStatus   = (ContactStatus) getIntent().getExtras().getSerializable("contact_status");

        TextView tvName       = (TextView) findViewById(R.id.tvName);
        TextView tvMaydayId   = (TextView) findViewById(R.id.tvMid);
        TextView tvNameHeader = (TextView) findViewById(R.id.tv_name);

        tvName.setText(contactName);
        tvMaydayId.setText(contactMayDayID);
        tvNameHeader.setText(contactName);

        etContactId   = (EditText) findViewById(R.id.hidden_tvMid);
        etContactName = (EditText) findViewById(R.id.hidden_edit_view_name);
        btnAction     = (Button) findViewById(R.id.btn_action);


    }

    public void clickActionButton(View view){

        Log.v(log_v, "clickActionButton");
        if (modifed){

            try{

                this.contactName       = etContactName.getText().toString();
                Contact currentContact = new Contact(contactName, contactMayDayID , contactStatus);
                this.db                = new DataBaseHelper(this);
                db.getWritableDatabase();
                db.contactEdit(currentContact);
                Log.v(log_v,  "Editing contact: " + currentContact.getName()
                        + ", " + currentContact.getMayDayId());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("contact_name", currentContact.getName());
                returnIntent.putExtra("contact_id", currentContact.getMayDayId());
                setResult(RESULT_OK, returnIntent);
                finish();


            }catch (Exception e){
                e.printStackTrace();
            }



        }else{
            setResult(RESULT_CANCELED);
            finish();
        }

    }

    public void clickEditContactId(View view){

        Log.v(log_v, "clickEditContactId");

        ViewSwitcher switcherId = (ViewSwitcher) findViewById(R.id.my_switcher_id);
        switcherId.showNext();
        etContactId.setText(contactMayDayID);
        modifed = true;
        btnAction.setText("Guardar");
    }

    public void clickEditContactName(View view){

        Log.v(log_v, "clickEditContactName");

        ViewSwitcher switcherName = (ViewSwitcher) findViewById(R.id.my_switcher_name);
        switcherName.showNext();

        etContactName.setText(contactName);
        modifed = true;
        btnAction.setText("Guardar");

    }

}
