package com.example.diego.mayday_03;



import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private String log_v="MainActivity";

    //used for the contactList
    ListView contactListView;
    ContactAdapter contactListAdapter;

    String mayDayID = "";
    String password = "";
    private DataBaseHelper db;
    MyApplication app;




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
        ArrayList<Contact> dbContacts = db.getContacts();

        if(dbContacts.isEmpty()){
            Log.v(log_v, "Empty contacts");
        }
        else {
            //Debug
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



    //Button action in "activity_main" to add new contact
    public void clickContactAdd(View view){
        Log.v(log_v,"clickContactAdd");
        Intent intent = new Intent(getApplicationContext(), ContactAddActivity.class);
        startActivity(intent);
    }


}
