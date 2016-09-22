package com.example.diego.mayday_03;



import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ContactActivity extends AppCompatActivity {

    static final int ADD_CONTACT_REQUEST    = 1;  // The request code
    static final int VIEW_CONTACT_REQUEST   = 2;
    static final int START_CONVERSATION_REQUEST   = 3;

    private String log_v="ContactActivity";

    //used for the contactList
    ListView contactListView;
    ContactAdapter contactListAdapter;
    String mayDayID = "";
    String password = "";
    private DataBaseHelper db;
    MyApplication app;
    ArrayList<Contact> dbContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(log_v,"onCreate");
        Bundle params = getIntent().getExtras();
        mayDayID= params.getString("mayDayID");
        password= params.getString("password");

        //Class in charge of the XMPP server connection
        app=(MyApplication) getApplication();
        app.setCredentials(mayDayID, password);
        app.connect();
        app.startListening();

        //FOR DEBUGGING
        /*Log.v(log_v, "contactTruncate");
        this.db    = new DataBaseHelper(this);
        db.contactTruncate(null);
        */
        //Get contacts from the database, put them in the listView and add a onClickListener to each
        loadContacts();

    }


    private void loadContacts(){

        Log.v(log_v, "loadContacts");
        DataBaseHelper db = new DataBaseHelper(this);
        dbContacts = db.getContacts();

        if(dbContacts.isEmpty()){
            Log.v(log_v, "Empty contacts");
        }
        else {

            sortContacts();
            for (Contact contact : dbContacts)
                System.out.println("Contact: " + contact.getName() + ", " + contact.getMayDayId());

            contactListView = (ListView) findViewById(R.id.lv_contactList);
            contactListAdapter = new ContactAdapter(getApplicationContext(), dbContacts);
            contactListView.setAdapter(contactListAdapter);

            //What to do when a contact is clicked
            contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Contact contact = contactListAdapter.getItem(position);
                    Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                    intent.putExtra("contact_MayDayID", contact.getMayDayId());
                    startActivity(intent);

                }
            });
        }
    }

    private void sortContacts() {
        Collections.sort(dbContacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }


    /* Button action in "activity_main" to add new contact */
    public void clickContactAdd(View view){
        Log.v(log_v,"clickContactAdd");
        Intent intent = new Intent(getApplicationContext(), ContactAddActivity.class);
        startActivityForResult(intent, ADD_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(log_v, "Result callback");
        /* Make sure the request was successful */
        if (resultCode == RESULT_OK) {
            Log.v(log_v, "RESULT OK, REFRESH CONTACT LIST DEPENDING THE REQUEST");
            // Check which request we're responding to
            switch (requestCode){
                case ADD_CONTACT_REQUEST:
                    Log.v(log_v, "ADD_CONTACT_REQUEST");
                    addContactToDataSet(data);
                    sortContacts();
                    contactListAdapter.notifyDataSetChanged();
                break;

                case VIEW_CONTACT_REQUEST:
                    Log.v(log_v, "VIEW_CONTACT_REQUEST");
                    if(data.getBooleanExtra("is_deleting", false) == true)
                        deleteContactInDataSet(data);
                    else
                        modifyContactInDataSet(data);
                    sortContacts();
                    contactListAdapter.notifyDataSetChanged();
                break;
                case START_CONVERSATION_REQUEST:

                break;

            }
        }
    }

    private void deleteContactInDataSet(Intent data) {
        Contact dContact;
        for(int i = 0; i < dbContacts.size(); i++){
            dContact = dbContacts.get(i);
            if(dContact.getIdAsString().equals(data.getStringExtra("removed_contact_id"))){
                dbContacts.remove(i);
                break;
            }
        }
    }

    /* We need to know which element in the set is going to be updated so we need its id to find it, and update it */
    private void modifyContactInDataSet(Intent data) {
        for (Contact contact : dbContacts){
            if(contact.getIdAsString().equals(data.getStringExtra("modified_contact_id")))
            {
                contact.setMayDayId(data.getStringExtra("modified_contact_MayDay_ID"));
                contact.setName(data.getStringExtra("modified_contact_name"));
                contact.setStatus(data.getStringExtra("modified_contact_status"));
                break;
            }
        }
    }

    private void addContactToDataSet(Intent data) {
        dbContacts.add(
            new Contact(
                    data.getStringExtra("new_contact_id"),
                    data.getStringExtra("new_contact_name"),
                    data.getStringExtra("new_contact_MaydayId"),
                    ContactStatus.NORMAL)
        );
    }


}
