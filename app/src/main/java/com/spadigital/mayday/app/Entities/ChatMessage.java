package com.spadigital.mayday.app.Entities;

import android.os.CountDownTimer;
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

    private TimerUpdater timerUpdater;


    public ChatMessage(){
        timerUpdater = new TimerUpdater();
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

    /**
     * @see TimerUpdater
     * @return Timer helper class
     */
    public TimerUpdater getTimerUpdater() {
        return timerUpdater;
    }

    /**
     * This class handles the Timer graphics, we need to set the reference
     * in ChatAdapter.setTimerView, then the method startTimer() is called
     * in MyMessageListener.processPacket and ChatActivity.loadChatDbHistory
     * to render our progressBar properly
     *
     * @see ChatAdapter
     * @see com.spadigital.mayday.app.Listeners.MyMessageListener
     * @see com.spadigital.mayday.app.Activities.ChatActivity
     */
    public class TimerUpdater {

        private final int REFRESH_MILLISECONDS = 20;

        private ProgressBar progressBarr;
        private List<ChatMessage> adapterCollection;
        private ChatAdapter chatAdapter;


        /**
         * This method starts the TimerView ticking in our ChatAdapter by using a CountdownTimer
         * it Refreshes every REFRESH_MILLISECONDS and sets the progress until expireTime goes off.
         */
        public void startTimer(){

            CountDownTimer timer = new CountDownTimer(Integer.parseInt(ChatMessage.this.expireTime),
                    REFRESH_MILLISECONDS) {

                /**
                 * It ticks every REFRESH_MILLISECONDS and calculates how many milliseconds needs
                 * for the CountDownTimer to finish
                 * @param millisUntilFinished milliseconds until animation finishes.
                 */
                @Override
                public void onTick(long millisUntilFinished) {
                    if (progressBarr != null)
                        progressBarr.setProgress((int) millisUntilFinished);
                }


                /**
                 * When the countDownTimer ends, we need the adapter to know that the item is not there
                 * anymore so we remove the chatMessage and notify the adapter to refresh our graphics
                 */
                @Override
                public void onFinish() {
                    removeFromCollection();
                }
            };

            timer.start();
        }


        /**
         * Removes the item from our list and notifies the adapter to update
         */
        private void removeFromCollection() {
            if(adapterCollection != null && adapterCollection.contains(ChatMessage.this) && chatAdapter!= null) {
                adapterCollection.remove(ChatMessage.this);
                chatAdapter.notifyDataSetChanged();
            }
        }


        /**
         *
         * @param progressBar the progress bar asset to be shown
         * @param chatAdapter the adapter so we can call notifyDataSetChanged() and get the collection
         */
        public void setAttributes(ProgressBar progressBar,  ChatAdapter chatAdapter) {
            this.progressBarr = progressBar;
            this.adapterCollection = chatAdapter.chatMessages;
            this.chatAdapter = chatAdapter;
        }
    }
}
