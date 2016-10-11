package com.spadigital.mayday.app.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.spadigital.mayday.app.Activities.ChatActivity;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Adapters.ContactAdapter;
import com.spadigital.mayday.app.Activities.ContactAddActivity;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.MayDayApplication;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;

/**
 * Created by jorge on 23/09/16.
 * This class handles the view of Contacts tab by requesting data from DB
 * It also initializes add contact activity in order to push new data in DB
 */

public class ContactsFragment extends Fragment implements SearchView.OnQueryTextListener{

    /** There will be two requests from this view in order to refresh the current data**/

    static final int ADD_CONTACT_REQUEST = 1;
    private String log_v = "ContactFragment";
    private ContactAdapter contactAdapter;
    private Activity parentActivity;
    private ArrayList<Contact> dbContacts;
    private static ContactsFragment instance;
    private View currentView;

    public static ContactsFragment getInstance(){
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        View view = inflater.inflate(R.layout.activity_contact, container, false);
        currentView = view;
        SearchView searchViewContact = (SearchView) view.findViewById(R.id.search_contact);
        this.parentActivity  = getActivity();
        loadContacts(view);
        setClickAddContactListener(view);
        //Need to share dbContacts with ChatActivity
        searchViewContact.setOnQueryTextListener(this);
        searchViewContact.setIconified(false);
        searchViewContact.clearFocus();


        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(contactAdapter != null) {
            loadContacts(currentView);
            //contactAdapter.notifyDataSetChanged();
        }

    }

    private void setClickAddContactListener(View view) {
        FloatingActionButton buttonAddContact = (FloatingActionButton) view.findViewById(R.id.bt_add);
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
        //db.contactTruncate(null);// For debugging
        db.close();

        contactAdapter = new ContactAdapter(ContactsFragment.this, new ArrayList<Contact>());
        ListView lvContacts = (ListView) currentView.findViewById(R.id.lv_contactList);
        lvContacts.setAdapter(contactAdapter);
        lvContacts.setTextFilterEnabled(true);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactAdapter.getItem(position);
                Intent intent   = new Intent(parentActivity, ChatActivity.class);
                intent.putExtra("contact_MayDayID", contact.getMayDayId());
                intent.putExtra("contact_author", contact.getName());
                startActivity(intent);
            }
        });

        if(dbContacts == null || dbContacts.isEmpty()){
            Log.v(log_v, "There is no contacts in DB!");
            Context context   = parentActivity;
            CharSequence text = "Aún no tienes contactos!";
            Toast toast       = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
            contactAdapter.add(dbContacts);
            contactAdapter.sortContacts();
        }
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
                    //TODO:We must refresh the Contacts View, the Conversations View adapter
                    Log.v(log_v, "ADD_CONTACT_REQUEST");
                    addContactToDataSet(data);
                    ConversationsFragment.getInstance().setAuthorIfExists(data);
                break;


            }
        }
    }



    public Contact findContactById(String mayDayId){
        for (Contact c: dbContacts) {
            if(c.getMayDayId().equals(mayDayId))
                return c;
        }
        return null;
    }

    private void addContactToDataSet(Intent data) {

        Contact contact = new Contact(
                data.getStringExtra("new_contact_id"),
                data.getStringExtra("new_contact_name"),
                data.getStringExtra("new_contact_MaydayId"),
                ContactStatus.NORMAL);
        contactAdapter.add(contact);
        contactAdapter.notifyDataSetChanged();
    }

    public void deleteContactInDataSet(Intent data) {
        contactAdapter.remove(data.getStringExtra("removed_contact_id"));
    }

    /* We need to know which element in the set is going to be updated so we need its id to find it, and update it */
    public void modifyContactInDataSet(Intent data) {
        Contact contact = new Contact(
                data.getStringExtra("modified_contact_id"),
                data.getStringExtra("modified_contact_name"),
                data.getStringExtra("modified_contact_MayDay_ID"),
                ContactStatus.valueOf(data.getStringExtra("modified_contact_status"))
        );
        contactAdapter.modify(contact);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactAdapter.getFilter().filter(newText);
        return false;
    }
}
