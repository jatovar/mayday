package com.example.diego.mayday_03;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 23/09/16.
 */
public class ChatAdapter extends BaseAdapter {

    private final List<ChatMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, ArrayList<ChatMessage> chatMessageList){
        this.context = context;
        this.chatMessages = chatMessageList;
    }


    @Override
    public int getCount() {
        if (chatMessages != null)
            return chatMessages.size();
        return 0;
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null)
            return chatMessages.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int layoutResource; // determined by view type
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        ChatMessageDirection msgDirection = chatMessage.getDirection();

        if(msgDirection == ChatMessageDirection.INCOMING)
            layoutResource = R.layout.item_chat_left;
        else
            layoutResource = R.layout.item_chat_right;

        if(convertView == null){
            convertView = vi.inflate(layoutResource, null);
            holder      = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        //setAlignment(holder, msgDirection);

        holder.txtMessage.setText(chatMessage.getMessage());
      //  holder.txtInfo.setText(chatMessage.getDatetime());

        return convertView;

    }
    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        ChatMessage chatMessage = getItem(position);
        ChatMessageDirection msgDirection = chatMessage.getDirection();
        if(msgDirection == ChatMessageDirection.INCOMING)
            return 0;
        else
            return 1;
    }
    public void add(ChatMessage message){
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages){
        chatMessages.addAll(messages);
    }


    private ViewHolder createViewHolder(View v) {
        ViewHolder holder    = new ViewHolder();
        holder.txtMessage    = (TextView) v.findViewById(R.id.txtMessage);
       // holder.content       = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        //holder.txtInfo       = (TextView) v.findViewById(R.id.txtInfo);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        //public TextView txtInfo;
       // public LinearLayout content;
        public LinearLayout contentWithBG;
    }


}



