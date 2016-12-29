package com.spadigital.mayday.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.spadigital.mayday.app.Helpers.DataBaseHelper;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.R;

/**
 * Created by mayday on 9/09/16.
 * This Activity appends a new contact to contact list
 */
public class ContactAddActivity extends AppCompatActivity {

    private String log_v = "ContactAddActivity:";
    private EditText contactName;
    private EditText mayDayID;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        Log.v(log_v, "onCreate");


        contactName = (EditText) findViewById(R.id.et_mayDayID);
        mayDayID = (EditText) findViewById(R.id.et_contactName);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                contactName.setText(extras.getString("active_chat_contact"));
                mayDayID.requestFocus();
            }

        }else {
            contactName.setText((String) savedInstanceState.getSerializable("active_chat_contact"));
            mayDayID.requestFocus();
        }
    }

    public void click_ok(View view){


        Contact contact      = new Contact(
                                            mayDayID.getText().toString(),
                                            contactName.getText().toString(),
                                            ContactStatus.NORMAL
                                            );

        DataBaseHelper db    = new DataBaseHelper(this);
        db.getWritableDatabase();

        Log.v(log_v, "Adding contact: " + contact.getName() + ", " + contact.getMayDayId());


        //If mayDayID is already in the DB.
        long contactId = db.contactAdd(contact);
        db.close();

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


    /**
     * Created by jorge on 20/09/16.
     */
    public static class ContactInformationActivity extends AppCompatActivity{

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
                    this.contactName = modifiedName && !etContactName.getText().toString().equals("")?
                            etContactName.getText().toString() : this.contactName;

                    this.contactMayDayID = modifiedId && !etContactId.getText().toString().equals("")  ?
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
}

