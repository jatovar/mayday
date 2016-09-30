package com.example.diego.mayday_03;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

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
                // status.setText("blocked");
                break;
            case UNKNOWN:
                holder.tvName.setText("");
                // status.setText("unknown");
                break;
            case NORMAL:
                holder.tvName.setText(contact.getName());
                // status.setText("normal");
                break;
        }
        return convertView;
    }

    /** For new contacts **/
    public void add(Contact contact){
        contactList.add(contact);
        sortContacts();
    }
    /** For initializing from db set  **/
    public void add(List<Contact> contactList){
        this.contactList.addAll(contactList);
        sortContacts();
    }

    /**For removing from collection**/
    public void remove(String idToDelete){
        for (Contact c : contactList)
            if(c.getIdAsString().equals(idToDelete)){
                contactList.remove(c);
                sortContacts();
                break;
            }
    }

    /**For modifying from collection**/
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

    /**We need to sort in evey CRUD operation**/
    public void sortContacts() {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvMayDayId = (TextView) v.findViewById(R.id.tv_mayDayID);
        holder.tvName     = (TextView) v.findViewById(R.id.tv_name);
        holder.tvStatus   = (TextView) v.findViewById(R.id.tv_status);

        return holder;
    }


    private static class ViewHolder {
        public TextView tvMayDayId;
        public TextView tvName;
        public TextView tvStatus;
    }

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

