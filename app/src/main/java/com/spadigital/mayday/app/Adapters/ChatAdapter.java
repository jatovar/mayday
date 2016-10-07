package com.spadigital.mayday.app.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spadigital.mayday.app.Enum.ChatMessageDirection;
import com.spadigital.mayday.app.Enum.ChatMessageType;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jorge on 23/09/16.
 * This is the Adapter of chat 1 to 1, it creates the chat bubbles dynamically according to database
 *
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
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtInfo.setText(chatMessage.getDatetime());

        return convertView;

    }

    private void setTimerView(ViewHolder holder, final ChatMessage chatMessage) {
        if(chatMessage.getType() == ChatMessageType.SELFDESTRUCTIVE
                && chatMessage.getDirection() == ChatMessageDirection.INCOMING
                && !chatMessage.getExpireTime().equals("")){

            int milliseconds = Integer.parseInt(chatMessage.getExpireTime());
            int seconds      = milliseconds / 1000;

            holder.layoutProgress.setVisibility(View.VISIBLE);
            holder.textProgress.setText(String.valueOf(seconds));
            holder.progressBar.setMax(seconds);

            ProgressBarAnimation anim = new ProgressBarAnimation(0, seconds + 1, holder);
            anim.setDuration(milliseconds);
            holder.progressBar.startAnimation(anim);

            Timer setDeletionTimer = new Timer();
            setDeletionTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    deleteMessage(chatMessage);
                }

            }, milliseconds);
        }
    }
    private void deleteMessage(final ChatMessage chatMessage){

        DataBaseHelper db = new DataBaseHelper(context);
        db.messageRemove(chatMessage.getId());

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatMessages.remove(chatMessage);
                notifyDataSetChanged();
            }
        });
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
        holder.txtInfo        = (TextView)v.findViewById(R.id.txtInfo);
        holder.contentWithBG  = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.layoutProgress = (RelativeLayout)v.findViewById(R.id.layoutProgressBar);
        holder.progressBar    = (ProgressBar)v.findViewById(R.id.progressBarDestroy);
        holder.textProgress   = (TextView)v.findViewById(R.id.textViewProgressBar);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout contentWithBG;
        public RelativeLayout layoutProgress;
        public ProgressBar progressBar;
        public TextView textProgress;
    }

    private class ProgressBarAnimation extends Animation{

        private  ViewHolder holder;
        private float from;
        private float to;

        public ProgressBarAnimation(float from, float to, ViewHolder holder) {
            super();
            this.from   = from;
            this.to     = to;
            this.holder = holder;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = to + (from - to) * interpolatedTime;
            holder.progressBar.setProgress((int) value);
            holder.textProgress.setText(String.valueOf((int)value));
        }


    }


}







