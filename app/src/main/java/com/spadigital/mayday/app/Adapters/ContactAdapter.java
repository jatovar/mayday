package com.spadigital.mayday.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.Fragments.ContactsFragment;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by jorge on 27/09/16.
 * This class handles the contact adapter to our needs (it refreshes the data when its required)
 */
public class ContactAdapter extends BaseAdapter implements Filterable{

    private List<Contact> contactList;
    private Activity context;
    private ArrayList<Contact> filterOriginalContacts;

    public ContactAdapter(ContactsFragment contactsFragment, ArrayList<Contact> contactList) {
        this.context     = contactsFragment.getActivity();
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        if (contactList != null)
            return  contactList.size();
        return 0;
    }

    @Override
    public Contact getItem(int position) {
        if (contactList != null)
            return contactList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Contact contact = getItem(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null){
            convertView = vi.inflate(R.layout.item_contact, null);
            holder      = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvMayDayId.setText(contact.getMayDayId());

        switch (contact.getStatus()){
            case BLOCKED:
                holder.tvName.setText(contact.getName());
                break;
            case UNKNOWN:
                holder.tvName.setText("Unknown");
                break;
            case NORMAL:
                holder.tvName.setText(contact.getName());
                break;
        }
        return convertView;
    }

    /** For new contacts **/
    public void add(Contact contact){

        boolean found = false;
        if(contact.getStatus() != ContactStatus.BLOCKED) {
            for (Contact c : contactList)
                if(c.getMayDayId().equals(contact.getMayDayId())){
                    found = true;
                    break;
                }
            if(!found) {
                contactList.add(contact);
                sortContacts();
            }


        }
    }
    /** For initializing from db set  **/
    public void add(List<Contact> contactList){
        this.contactList.addAll(contactList);
        sortContacts();
    }

    /**
     * When a contact is deleted we need to find it and remove it
     * @param idToDelete The contact's mayDayId to delete
     */
    public void remove(String idToDelete){

        for (Contact c : contactList)
            if(c.getIdAsString().equals(idToDelete)){
                contactList.remove(c);
                sortContacts();
                break;
            }
    }

    /**
     * When a contact is modified we need to find it and set the information according to user's
     * request.
     * @param newContactInfo The contact's information to be modified
     */
    public void modify(Contact newContactInfo){

        for (Contact c : contactList)
            if (c.getIdAsString().equals(newContactInfo.getIdAsString())){
                c.setStatus(newContactInfo.getStatus());
                c.setName(newContactInfo.getName());
                c.setMayDayId(newContactInfo.getMayDayId());
                sortContacts();
                break;
            }
    }

    /**
     * This function is used to sort contacts every CRUD operation in order to keep them
     * sorted in our ListView
     */
    public void sortContacts() {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }


    /**
     * This function initializes our inflatedView so we can change it in execution time
     *
     * @param v The Inflated view with child controls
     * @return  The ViewHolder, a helper class to encapsulate our child controls
     */
    private ViewHolder createViewHolder(View v) {

        ViewHolder holder = new ViewHolder();

        holder.tvMayDayId = (TextView) v.findViewById(R.id.tv_mayDayID);
        holder.tvName     = (TextView) v.findViewById(R.id.tv_name);
        holder.tvStatus   = (TextView) v.findViewById(R.id.tv_status);

        return holder;
    }

    /**
     * This is a  helper class to optimize the findViewById, so we only initialize them once
     */
    private static class ViewHolder {
        public TextView tvMayDayId;
        public TextView tvName;
        public TextView tvStatus;
    }

    /**
     * This filter performs a contact name search to filter the contacts in the ListView
     * it also saves a copy so we never lose the original data in this process
     * @see Filterable
     * @return  Filter, a set of data that is passed to SearchView
     */
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Contact> FilteredArrayNames = new ArrayList<>();

                if (filterOriginalContacts == null) {
                    // saves the original data in filterOriginalContacts
                    filterOriginalContacts = new ArrayList<>(contactList);
                }

                if (constraint == null || constraint.length() == 0) {
                    // set the Original result to return
                    results.count = filterOriginalContacts.size();
                    results.values = filterOriginalContacts;
                }else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filterOriginalContacts.size(); i++) {
                        Contact data = filterOriginalContacts.get(i);
                        if (data.getName().toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrayNames.add(data);
                        }
                    }
                    results.count = FilteredArrayNames.size();
                    results.values = FilteredArrayNames;
                }

                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {

                contactList = (List<Contact>) results.values;
                notifyDataSetChanged();
            }

        };
        return filter;
    }

}

