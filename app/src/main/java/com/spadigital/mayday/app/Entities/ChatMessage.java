package com.spadigital.mayday.app.Entities;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

import com.spadigital.mayday.app.Adapters.ChatAdapter;
import com.spadigital.mayday.app.Enum.ChatMessageDirection;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Enum.ChatMessageType;

import java.util.List;

/**
 * Created by mayday on 13/09/16.
 */
public class ChatMessage {

    private String contactMayDayID = "";
    private String message         = "";
    private String datetime        = "";
    private int id                 = -1;
    private String author          = "";
    private String expireTime      = "";
    private Boolean isEmergency    = false;
    private Boolean isRedirected   = false;
    private String subject         = "";

    private ChatMessageStatus status;
    private ChatMessageDirection direction;
    private ChatMessageType type;
    private ProgressBar progressBarr;
    private List<ChatMessage> adapterCollection;
    private ChatAdapter chatAdapter;


    public ChatMessage(){

    }

    public void setId(int id){
        this.id = id;
    }

    public void setContactMayDayId(String contact_MayDayID){
        this.contactMayDayID = contact_MayDayID;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void setDatetime(String datetime){
        this.datetime = datetime;
    }

    public void setStatus(String status){
        this.status = ChatMessageStatus.valueOf(status);
    }

    public void setStatus(ChatMessageStatus status){
        this.status = status;
    }

    public void setDirection(String direction){
        this.direction = ChatMessageDirection.valueOf(direction);
    }

    public void setDirection(ChatMessageDirection direction){
        this.direction = direction;
    }

    public void setType(String type){
        this.type = ChatMessageType.valueOf(type);
    }

    public void setType(ChatMessageType type){
        this.type = type;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId(){
        return this.id;
    }

    public String getContactMayDayID(){
        return this.contactMayDayID;
    }

    public String getMessage(){
        return this.message;
    }

    public String getDatetime(){
        return this.datetime;
    }

    public ChatMessageStatus getStatus(){
        return this.status;
    }

    public ChatMessageDirection getDirection(){
        return this.direction;
    }

    public ChatMessageType getType(){
        return this.type;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public void setIsEmergency(boolean isEmergency) {
        this.isEmergency = isEmergency;
    }

    public Boolean getIsEmergency() {
        return isEmergency;
    }

    public Boolean getRedirected() {
        return isRedirected;
    }

    public void setRedirected(Boolean redirected) {
        isRedirected = redirected;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTimer(){
        CountDownTimer timer = new CountDownTimer(Integer.parseInt(expireTime), 20) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (progressBarr != null)
                    progressBarr.setProgress((int) millisUntilFinished);
            }

            @Override
            public void onFinish() {
                removeFromCollection();
            }
        };

        timer.start();
    }

    public void setProgressBar(ProgressBar progressBar){
        this.progressBarr = progressBar;
    }

    private void removeFromCollection() {
        if(adapterCollection != null && adapterCollection.contains(this) && chatAdapter!= null) {
            adapterCollection.remove(this);
            chatAdapter.notifyDataSetChanged();
        }
    }

    public void setAdapterCollection(List<ChatMessage> adapterCollection, ChatAdapter chatAdapter) {
        this.adapterCollection = adapterCollection;
        this.chatAdapter = chatAdapter;
    }
}
