package com.spadigital.mayday.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spadigital.mayday.app.Enum.ChatMessageDirection;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Enum.ChatMessageType;
import com.spadigital.mayday.app.Helpers.DataBaseHelper;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 23/09/16.
 * This is the Adapter of chat 1 to 1, it creates the chat bubbles dynamically according to database
 *
 */
public class ChatAdapter extends BaseAdapter {

    public List<ChatMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, ArrayList<ChatMessage> chatMessageList){
        this.context      = context;
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
        LayoutInflater vi = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        ChatMessage chatMessage = getItem(position);

        layoutResource = chatMessage.getDirection() == ChatMessageDirection.INCOMING
                ? R.layout.item_chat_left : R.layout.item_chat_right;

        if(convertView == null){
            convertView = vi.inflate(layoutResource, null);
            holder      = createViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


        setTimerView(holder, chatMessage);
        setSendingStatusIcon(holder, chatMessage);
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtInfo.setText(chatMessage.getDatetime());

        return convertView;

    }

    private void setSendingStatusIcon(ViewHolder holder, ChatMessage chatMessage) {
        if(chatMessage.getDirection() == ChatMessageDirection.OUTGOING){
            if(chatMessage.getStatus() == ChatMessageStatus.SENT){
                holder.imgStatus.setImageResource(R.drawable.ic_sent_icon);
            }else{
                holder.imgStatus.setImageResource(R.drawable.ic_sending_icon);
            }
        }
    }

    private void setTimerView(final ViewHolder holder, final ChatMessage chatMessage) {



        if(chatMessage.getType() == ChatMessageType.SELFDESTRUCTIVE
                && chatMessage.getDirection() == ChatMessageDirection.INCOMING
                && !chatMessage.getExpireTime().equals("")){

            int milliseconds = Integer.parseInt(chatMessage.getExpireTime());
            int seconds      = milliseconds / 1000;

            holder.layoutProgress.setVisibility(View.VISIBLE);
            holder.textProgress.setVisibility(View.VISIBLE);

            holder.textProgress.setText(String.valueOf(seconds));
            holder.progressBar.setMax(milliseconds);

            ChatMessage.TimerUpdater timerUpdater = chatMessage.getTimerUpdater();
            timerUpdater.setAttributes(holder.progressBar, this);


            DataBaseHelper dbc = new DataBaseHelper(context);
            dbc.messageRemove(chatMessage.getId());
            dbc.close();

            notifyDataSetChanged();

        }
    }



    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = getItem(position);
        return chatMessage.getDirection() == ChatMessageDirection.INCOMING ? 0 : 1;
    }
    public void add(ChatMessage message){
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages){
        chatMessages.addAll(messages);
    }


    private ViewHolder createViewHolder(View v) {
        ViewHolder holder     = new ViewHolder();
        holder.txtMessage     = (TextView) v.findViewById(R.id.txtMessage);
        holder.txtInfo        = (TextView) v.findViewById(R.id.txtInfo);
        holder.textProgress   = (TextView) v.findViewById(R.id.textViewProgressBar);

        holder.contentWithBG  = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.layoutProgress = (RelativeLayout) v.findViewById(R.id.layoutProgressBar);

        holder.progressBar    = (ProgressBar) v.findViewById(R.id.progressBarDestroy);
        holder.imgStatus      = (ImageView) v.findViewById(R.id.imgStatus);

        return holder;
    }

    /** This method updates the GUI to a message sent**/
    public void setMessageToSent(int id) {
        for (ChatMessage chatMessage : chatMessages) {
            if(chatMessage.getId() == id){
                chatMessage.setStatus(ChatMessageStatus.SENT);
                break;
            }
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private static class ViewHolder {
        TextView txtMessage;
        TextView txtInfo;
        LinearLayout contentWithBG;
        RelativeLayout layoutProgress;
        ProgressBar progressBar;
        TextView textProgress;
        ImageView imgStatus;
    }


}







