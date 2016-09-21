package com.example.diego.mayday_03;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

    private String log_v="ContactAdapter";
    //private Context mContext;
    static final int VIEW_CONTACT_REQUEST = 2;

    public  ContactAdapter(Context context, ArrayList<Contact> contactList){
        super(context, 0, contactList);
      //  mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
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

        ImageButton showContact = (ImageButton) convertView.findViewById(R.id.btn_showcontacts);
        ImageButton startConv   = (ImageButton) convertView.findViewById(R.id.btn_startconversation);

        showContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log_v, "onCLick SHOW CONTACT");
                Contact c = getItem(position);
                Log.v(log_v, "Contact name = "      +  c.getName() + "\n" +
                             "Contact Mayday ID = " + c.getMayDayId() + "\n"
                 );

                Intent intent = new Intent( parent.getContext(), ContactInformationActivity.class);
                intent.putExtra("editing_contact_MayDayID", c.getMayDayId());
                intent.putExtra("editing_contact_name", c.getName());
                intent.putExtra("editing_contact_status", c.getStatus());
                intent.putExtra("editing_contact_id", c.getIdAsString());
                ((Activity) parent.getContext()).startActivityForResult(intent, VIEW_CONTACT_REQUEST);

            }
        });



        startConv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(log_v,"onCLick SHOW CONVERSATION");
                Intent intent = new Intent( parent.getContext(), ChatActivity.class);
                parent.getContext().startActivity(intent);
            }
        });

        TextView mayDayID = (TextView) convertView.findViewById(R.id.tv_mayDayID);
        TextView name     = (TextView) convertView.findViewById(R.id.tv_name);
        TextView status   = (TextView) convertView.findViewById(R.id.tv_status);

        Contact contact   = getItem(position);

        mayDayID.setText(contact.getMayDayId());

        switch (contact.getStatus()){

            case BLOCKED:
                name.setText(contact.getName());
               // status.setText("blocked");
                break;
            case UNKNOWN:
                name.setText("");
               // status.setText("unknown");
                break;
            case NORMAL:
                name.setText(contact.getName());
               // status.setText("normal");
                break;
        }



        return convertView;
    }




}

