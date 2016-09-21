package com.example.diego.mayday_03;



import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    static final int ADD_CONTACT_REQUEST    = 1;  // The request code
    static final int EDIT_CONTACT_REQUEST   = 2;
    static final int REMOVE_CONTACT_REQUEST = 3;
    private String log_v="MainActivity";

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

            for (Contact contact : dbContacts) {
                System.out.println("Contact: " + contact.getName() + ", " + contact.getMayDayId());
            }

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


    //Button action in "activity_main" to add new contact
    public void clickContactAdd(View view){
        Log.v(log_v,"clickContactAdd");
        Intent intent = new Intent(getApplicationContext(), ContactAddActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, ADD_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(log_v,"Result callback");
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            Log.v(log_v,"RESULT OK, REFRESH CONTACT LIST DEPENDING THE CASE");
            // Check which request we're responding to
            switch (requestCode){
                case ADD_CONTACT_REQUEST:

                        Log.v(log_v, "ADD_CONTACT_REQUEST");

                        dbContacts.add(
                                new Contact(data.getStringExtra("contact_name"),
                                        data.getStringExtra("contact_id"), ContactStatus.NORMAL)
                        );

                        sortContacts();
                        contactListAdapter.notifyDataSetChanged();
                    break;
                case EDIT_CONTACT_REQUEST:

                    Log.v(log_v, "EDIT_CONTACT_REQUEST");
                    // TODO: We need to know which element in the set is going to be updated so we need the old value of PK, find it, and update it if thats the case
                    // and
                    //dbContacts.contains()
                    break;
                case REMOVE_CONTACT_REQUEST:
                    break;
            }

        }
    }



}
