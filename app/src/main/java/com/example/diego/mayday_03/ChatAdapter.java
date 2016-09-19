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
public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessageList){
        super(context, 0, chatMessageList);
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

        TextView name      = (TextView) convertView.findViewById(R.id.tv_name);
        TextView message   = (TextView) convertView.findViewById(R.id.tv_MyMessage);
        TextView datetime  = (TextView) convertView.findViewById(R.id.tv_datetime);
        TextView status    = (TextView) convertView.findViewById(R.id.tv_status);
        TextView direction = (TextView) convertView.findViewById(R.id.tv_direction);
        TextView type      = (TextView) convertView.findViewById(R.id.tv_type);

        ChatMessage msg = getItem(position);
        System.out.println("INSIDE GET VIEW: "+ msg.getDirection().toString() + " : " + msg.getMessage());

        name.setText(msg.getContactMayDayID());
        message.setText(msg.getMessage());
        datetime.setText(msg.getDatetime());
        status.setText(msg.getStatus().toString());
        direction.setText(msg.getDirection().toString());
        type.setText(msg.getType().toString());

        /*Is this a message I've received ?
        if(msg.getDirection() == ChatMessageDirection.INCOMING) {
            message.setTextColor(16);
        }
        */
        return convertView;
    }
}



