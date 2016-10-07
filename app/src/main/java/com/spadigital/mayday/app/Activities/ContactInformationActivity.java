package com.spadigital.mayday.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.R;

public class ContactInformationActivity extends AppCompatActivity {

    private String log_v = "ContactInfoActivity:";
    private String contactMayDayID;
    private String contactName;
    private ContactStatus contactStatus;
    private String contactId;
    private Button btnAction;
    private boolean modifiedName = false;
    private boolean modifiedId = false;
    private DataBaseHelper db;
    private EditText etContactId;
    private EditText etContactName;
    private TextView tvName;
    private TextView tvMaydayId;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_information);
        Log.v(log_v, "onCreate");

        contactId       = getIntent().getExtras().getString("editing_contact_id");
        contactMayDayID = getIntent().getExtras().getString("editing_contact_MayDayID");
        contactName     = getIntent().getExtras().getString("editing_contact_name");
        contactStatus   = (ContactStatus) getIntent().
                getExtras().getSerializable("editing_contact_status");

        Log.v(log_v, "Starting editing with MayDayID: " + contactMayDayID);

        tvName       = (TextView) findViewById(R.id.tvName);
        tvMaydayId   = (TextView) findViewById(R.id.tvMid);
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

        if (modifiedName || modifiedId){

            try{
                this.contactName = modifiedName && etContactName.getText().toString() != "" ?
                        etContactName.getText().toString() : this.contactName;

                this.contactMayDayID = modifiedId && etContactId.getText().toString() != ""  ?
                        etContactId.getText().toString() : this.contactMayDayID;

                Contact modifiedContact = new Contact(
                        contactId,
                        contactName,
                        contactMayDayID,
                        contactStatus
                );

                this.db = new DataBaseHelper(this);
                db.getWritableDatabase();
                db.contactEdit(modifiedContact);
                db.close();
                Log.v(log_v,  "Editing contact: " + modifiedContact.getName()
                        + ", " + modifiedContact.getMayDayId());

                Intent returnIntent = new Intent();
                returnIntent.putExtra("modified_contact_name", modifiedContact.getName());
                returnIntent.putExtra("modified_contact_MayDay_ID", modifiedContact.getMayDayId());
                returnIntent.putExtra("modified_contact_id", modifiedContact.getIdAsString());
                returnIntent.putExtra("modified_contact_status", "NORMAL"); //TODO: BLOCK CONTACTS
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
        modifiedId = true;
        btnAction.setText("Guardar");
    }

    public void clickEditContactName(View view){

        Log.v(log_v, "clickEditContactName");

        ViewSwitcher switcherName = (ViewSwitcher) findViewById(R.id.my_switcher_name);
        switcherName.showNext();

        etContactName.setText(contactName);
        modifiedName = true;
        btnAction.setText("Guardar");

    }

    public void clickRemoveContactButton(View view){
            /*TODO : We need a confirmation message to warn the user if he really wants to remove the contact with xxxxx name before this code is executed */
        Log.v(log_v, "clickRemoveContactButton");
        try{

            Contact deletedContact = new Contact(
                    contactId,
                    contactName,
                    contactMayDayID,
                    contactStatus
            );

            db = new DataBaseHelper(this);
            db.getWritableDatabase();
            db.contactTruncate(deletedContact.getIdAsString());
            db.close();
            Log.v(log_v,  "Removing contact: " + deletedContact.getName()
                    + ", " + deletedContact.getMayDayId());

            Intent returnIntent = new Intent();
            returnIntent.putExtra("removed_contact_id", deletedContact.getIdAsString());
            returnIntent.putExtra("removed_contact_maydayid", deletedContact.getMayDayId());
            returnIntent.putExtra("is_deleting", true);
            setResult(RESULT_OK, returnIntent);
            finish();


        }catch (Exception e){
            e.printStackTrace();
        }

    }

}