package com.example.diego.mayday_03;

/**
 * Created by diego on 13/09/16.
 */
public class MyMessage {
    private int id=-1;
    private String contactMayDayID="";
    private String message="";
    private String datetime="";
    private MyMessageStatus status;
    private MyMessageDirection direction;
    private MyMessageType type;

    public MyMessage(String contactMayDayID, String message, String datetime, String status, String direction, String type){

        this.contactMayDayID=contactMayDayID;
        this.message=message;
        this.datetime=datetime;
        this.status= MyMessageStatus.valueOf(status);
        this.direction= MyMessageDirection.valueOf(direction);
        this.type= MyMessageType.valueOf(type);

    }

    public MyMessage(String contactMayDayID, String message, String datetime, MyMessageStatus status, MyMessageDirection direction, MyMessageType type){

        this.contactMayDayID=contactMayDayID;
        this.message=message;
        this.datetime=datetime;
        this.status=status;
        this.direction=direction;
        this.type=type;

    }

    public MyMessage(){

    }

    public void set_id(int id){
        this.id=id;
    }

    public void setContactMayDayId(String contact_MayDayID){
        this.contactMayDayID = contact_MayDayID;
    }

    public void setMessage(String message){
        this.message=message;
    }

    public void setDatetime(String datetime){
        this.datetime=datetime;
    }

    public void set_status(String status){
        this.status = MyMessageStatus.valueOf(status);
    }

    public void setStatus(MyMessageStatus status){
        this.status=status;
    }

    public void setDirection(String direction){
        this.direction = MyMessageDirection.valueOf(direction);
    }

    public void setDirection(MyMessageDirection direction){
        this.direction=direction;
    }

    public void setType(String type){
        this.type = MyMessageType.valueOf(type);
    }

    public void setType(MyMessageType type){
        this.type=type;
    }




    public int get_id(){
        return this.id;
    }

    public String get_contact_MayDayID(){
        return this.contactMayDayID;
    }

    public String get_message(){
        return this.message;
    }

    public String get_datetime(){
        return this.datetime;
    }

    public MyMessageStatus get_status(){
        return this.status;
    }

    public MyMessageDirection get_direction(){
        return this.direction;
    }

    public MyMessageType get_type(){
        return this.type;
    }







}
