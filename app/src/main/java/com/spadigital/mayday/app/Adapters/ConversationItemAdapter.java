package com.spadigital.mayday.app.Adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spadigital.mayday.app.Enum.ChatMessageDirection;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.Fragments.ConversationsFragment;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 26/09/16.
 * This class handles the Cursor adapter to our needs (it gets the position of an unread message
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
        LayoutInflater vi       = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = vi.inflate(R.layout.item_conversation, null);
            holder      = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String contactPlaceholder = chatMessage.getAuthor() + " [" +
                chatMessage.getContactMayDayID() + "]";

        holder.tvContactName.setText(contactPlaceholder);
        holder.tvMessageBody.setText(chatMessage.getMessage());
        holder.tvTimestamp.setText(chatMessage.getDatetime());
        setNoReadMessage(holder, chatMessage.getStatus(), chatMessage.getDirection());

        DataBaseHelper db = new DataBaseHelper(context);
        ContactStatus status = db.findContactStatus(chatMessage.getContactMayDayID());

        if(status == ContactStatus.BLOCKED)
            convertView.setEnabled(false);
        else
            convertView.setEnabled(true);
        db.close();

        return convertView;
    }

    private void setNoReadMessage(ViewHolder holder, ChatMessageStatus status, ChatMessageDirection direction) {

        if(direction == ChatMessageDirection.INCOMING && status == ChatMessageStatus.UNREAD) {

            holder.imgRead.setColorFilter(Color.RED);
            holder.tvMessageBody.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Small);
            holder.tvMessageBody.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryMayday));
            holder.tvMessageBody.setTypeface(null, Typeface.BOLD);
            holder.tvContactName.setTypeface(null, Typeface.BOLD);
        }else{

            holder.imgRead.setColorFilter(Color.LTGRAY);
            holder.tvMessageBody.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Small);
            holder.tvMessageBody.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.tvMessageBody.setTypeface(null, Typeface.NORMAL);
            holder.tvContactName.setTypeface(null, Typeface.NORMAL);
        }

    }

    /**We need to add the latest message in the first position*/
    public void addFirst(ChatMessage message){
        chatMessages.add(0, message);
    }

    public void add(List<ChatMessage> messages){
        chatMessages.addAll(messages);
    }

    /**This method looks in our list of latest messages and replaces it when a new message has arrived**/
    public void replaceMessageAndSetFirst(ChatMessage message){

        for (ChatMessage msg : chatMessages) {
            if(msg.getContactMayDayID().equals(message.getContactMayDayID())){
                chatMessages.remove(msg);
                this.addFirst(message);
                return ;
            }
        }
        this.addFirst(message);
    }
    public void setAuthorToUnknown(String toRemoveMayDayId) {
        for (ChatMessage chatMessage : chatMessages)
            if (chatMessage.getContactMayDayID().equals(toRemoveMayDayId))
                chatMessage.setAuthor(" UNKNOWN ");
    }

    /**This ViewHolder is a helper function for accessing to child controls in the conversation View*/
    private ViewHolder createViewHolder(View v) {

        ViewHolder holder    = new ViewHolder();

        holder.tvContactName = (TextView) v.findViewById(R.id.tvContactName);
        holder.tvMessageBody = (TextView) v.findViewById(R.id.tvMessageBody);
        holder.tvTimestamp   = (TextView) v.findViewById(R.id.tvTimestamp);
        holder.imgRead       = (ImageView) v.findViewById(R.id.imgRead);

        return holder;
    }

    public void updateContactInfo(Contact contact) {

        for (ChatMessage chatMessage : chatMessages)
            if (chatMessage.getContactMayDayID().equals(contact.getMayDayId())){
                chatMessage.setAuthor(contact.getName());
                chatMessage.setContactMayDayId(contact.getMayDayId());
            }
    }



    private static class ViewHolder {
        public TextView tvContactName;
        public TextView tvMessageBody;
        public TextView tvTimestamp;
        public ImageView imgRead;
    }

}
