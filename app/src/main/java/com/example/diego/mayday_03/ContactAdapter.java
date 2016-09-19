package com.example.diego.mayday_03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by diego on 9/09/16.
 * This "Adapter" is used to handle the communication between a listView and
 * a ArrayList<Contact>. It defines how an object "Contact" gets mapped into a
 * layout "item_contact".
 *
 * This adapter is used within the method "load_contacts" which is used to populate
 * The list of contacts.
 *
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    public  ContactAdapter(Context context, ArrayList<Contact> contactList){
        super(context, 0, contactList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact, parent, false);
        }

        /*TODO:
            -adding an avatar image
                ImageView avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
                http://www.hermosaprogramacion.com/2014/10/android-listas-adaptadores/
        */

        TextView mayDayID = (TextView) convertView.findViewById(R.id.tv_mayDayID);
        TextView name     = (TextView) convertView.findViewById(R.id.tv_name);
        TextView status   = (TextView) convertView.findViewById(R.id.tv_status);

        Contact contact   = getItem(position);

        mayDayID.setText(contact.getMayDayId());

        switch (contact.getStatus()){

            case BLOCKED:
                name.setText(contact.getName());
                status.setText("blocked");
                break;
            case UNKNOWN:
                name.setText("");
                status.setText("unknown");
                break;
            case NORMAL:
                name.setText(contact.getName());
                status.setText("normal");
                break;
        }


        return convertView;
    }
}

