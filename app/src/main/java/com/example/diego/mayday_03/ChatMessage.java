package com.example.diego.mayday_03;

/**
 * Created by diego on 13/09/16.
 */
public class ChatMessage {

    private String contactMayDayID = "";
    private String message         = "";
    private String datetime        = "";
    private int id                 = -1;
    private String author          = "";
    private String expireTime      = "";

    private ChatMessageStatus status;
    private ChatMessageDirection direction;
    private ChatMessageType type;

    public ChatMessage(String contactMayDayID, String message, String datetime, String status, String direction, String type){

        this.contactMayDayID = contactMayDayID;
        this.message         = message;
        this.datetime        = datetime;
        this.status          = ChatMessageStatus.valueOf(status);
        this.direction       = ChatMessageDirection.valueOf(direction);
        this.type            = ChatMessageType.valueOf(type);

    }

    public ChatMessage(String contactMayDayID, String message, String datetime, ChatMessageStatus status, ChatMessageDirection direction, ChatMessageType type){

        this.contactMayDayID = contactMayDayID;
        this.message         = message;
        this.datetime        = datetime;
        this.status          = status;
        this.direction       = direction;
        this.type            = type;

    }

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
}
