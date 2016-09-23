package com.example.diego.mayday_03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jorge on 23/09/16.
 * This class handles the view of Contacts tab by requesting data from DB
 * It also initializes add contact activity in order to push new data in DB
 */

public class ContactsFragment extends Fragment{

    /** There will be two requests from this view in order to refresh the current data**/

    static final int ADD_CONTACT_REQUEST        = 1;
    static final int START_CONVERSATION_REQUEST = 2;

    private String log_v = "ContactFragment";

    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleCursorAdapter adapter;
    private ListView lvContacts;

    private ContactAdapter contactAdapter;
    private Activity parentActivity;
    private ArrayList<Contact> dbContacts;
    private View view;
    private FloatingActionButton buttonAddContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contact, container, false);
        this.parentActivity  = getActivity();
        this.view = view;
        loadContacts(view);
        setClickAddListener(view);
        return view;
    }

    private void setClickAddListener(View view) {
        buttonAddContact = (FloatingActionButton)view.findViewById(R.id.bt_add);
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log_v,"clickContactAdd");
                Intent intent = new Intent(v.getContext(), ContactAddActivity.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);
            }
        });
    }

    private void loadContacts(View currentView) {
        Log.v(log_v, "Loading contacts...");

        DataBaseHelper db = new DataBaseHelper(parentActivity);
        dbContacts        = db.getContacts();

        if(dbContacts.isEmpty()){
            Log.v(log_v, "There is no contacts in DB!");
        }
        else{

            sortContacts();

            lvContacts     = (ListView)currentView.findViewById(R.id.lv_contactList);
            contactAdapter = new ContactAdapter(currentView.getContext(), dbContacts);
            lvContacts.setAdapter(contactAdapter);
            lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Contact contact = contactAdapter.getItem(position);
                    Intent intent = new Intent(parentActivity, ChatActivity.class);
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(log_v, "Result callback");
        /* Make sure the request was successful */
        if (resultCode == Activity.RESULT_OK) {
            Log.v(log_v, "RESULT OK, REFRESH CONTACT LIST DEPENDING THE REQUEST");
            // Check which request we're responding to
            switch (requestCode){
                case ADD_CONTACT_REQUEST:
                    Log.v(log_v, "ADD_CONTACT_REQUEST");
                    addContactToDataSet(data);
                    sortContacts();
                    contactAdapter.notifyDataSetChanged();
                    break;

                /*case VIEW_CONTACT_REQUEST:
                    Log.v(log_v, "VIEW_CONTACT_REQUEST");
                    if(data.getBooleanExtra("is_deleting", false) == true)
                        deleteContactInDataSet(data);
                    else
                        modifyContactInDataSet(data);
                    sortContacts();
                    contactListAdapter.notifyDataSetChanged();
                    break;*/
                case START_CONVERSATION_REQUEST:

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
