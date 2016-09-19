package com.example.diego.mayday_03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by diego on 13/09/16.
 */
public class MyMessageAdapter extends ArrayAdapter<MyMessage> {

    public MyMessageAdapter(Context context, ArrayList<MyMessage> myMessageList){
        super(context, 0, myMessageList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_message, parent,false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView message = (TextView) convertView.findViewById(R.id.tv_MyMessage);
        TextView datetime = (TextView) convertView.findViewById(R.id.tv_datetime);
        TextView status = (TextView) convertView.findViewById(R.id.tv_status);
        TextView direction = (TextView) convertView.findViewById(R.id.tv_direction);
        TextView type = (TextView) convertView.findViewById(R.id.tv_type);

        MyMessage msg = getItem(position);
        System.out.println("INSIDE GET VIEW: "+ msg.get_direction().toString() + " : " + msg.get_message());

        name.setText(msg.get_contact_MayDayID());
        message.setText(msg.get_message());
        datetime.setText(msg.get_datetime());
        status.setText(msg.get_status().toString());
        direction.setText(msg.get_direction().toString());
        type.setText(msg.get_type().toString());

        /*Is this a message I've received ?
        if(msg.get_direction() == MyMessageDirection.INCOMING) {
            message.setTextColor(16);
        }
        */
        return convertView;
    }
}



