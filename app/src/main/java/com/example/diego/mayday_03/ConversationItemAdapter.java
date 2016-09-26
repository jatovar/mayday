package com.example.diego.mayday_03;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 26/09/16.
 * This class handles the Cursor adapter to our needs (it gets the position where an unread message
 * is and sets the color of the textView)
 */
public class ConversationItemAdapter extends BaseAdapter {

    private final List<ChatMessage> chatMessages;
    private Activity context;

    public ConversationItemAdapter(ConversationsFragment conversationsFragment, ArrayList<ChatMessage> chatMessages) {
        this.context      = conversationsFragment.getActivity();
        this.chatMessages = chatMessages;
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
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = vi.inflate(R.layout.item_conversation, null);
            holder      = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String contactPlaceholder = chatMessage.getAuthor() + "(" +
                chatMessage.getContactMayDayID() + ")";
        holder.tvContactName.setText(contactPlaceholder);
        holder.tvMessageBody.setText(chatMessage.getMessage());
        holder.tvTimestamp.setText(chatMessage.getDatetime());
        setNoReadMessage(holder.tvMessageBody, chatMessage.getStatus(), chatMessage.getDirection());

        return convertView;
    }

    private void setNoReadMessage(TextView tvMessageBody, ChatMessageStatus status, ChatMessageDirection direction) {

        if(direction == ChatMessageDirection.INCOMING && status == ChatMessageStatus.UNREAD) {
            tvMessageBody.setTextAppearance(context, android.R.style.TextAppearance_Material_Medium);
            tvMessageBody.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryMayday));
            tvMessageBody.setTypeface(null, Typeface.BOLD);
        }

    }

    /**We need to add the latest message in the first position*/
    public void addFirst(ChatMessage message){
        chatMessages.add(0, message);
    }

    public void add(List<ChatMessage> messages){
        chatMessages.addAll(messages);
    }

    /**This method looks in our list of latest messages and replaces it when a new message has arrived
     * the author and the mayday_id should not change because it will always be the same**/
    public boolean lookUpOrReplace(ChatMessage message){
        for (ChatMessage msg : chatMessages) {
            if(msg.getContactMayDayID().equals(message.getContactMayDayID())){
                msg.setMessage(message.getMessage());
                msg.setStatus(message.getStatus());
                msg.setType(message.getType());
                msg.setDirection(message.getDirection());
                msg.setId(message.getId());
                msg.setDatetime(message.getDatetime());
                return true;
            }
        }
        return false;
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder    = new ViewHolder();

        holder.tvContactName = (TextView) v.findViewById(R.id.tvContactName);
        holder.tvMessageBody = (TextView) v.findViewById(R.id.tvMessageBody);
        holder.tvTimestamp   = (TextView) v.findViewById(R.id.tvTimestamp);

        return holder;
    }

    private static class ViewHolder {
        public TextView tvContactName;
        public TextView tvMessageBody;
        public TextView tvTimestamp;
    }

}
