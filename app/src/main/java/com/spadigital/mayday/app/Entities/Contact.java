package com.spadigital.mayday.app.Entities;

import com.spadigital.mayday.app.Enum.ContactStatus;

public class Contact {

    private String id;
    private ContactStatus status;

    private String name     = "";
    private String mayDayID = "";


    public Contact(){

    }

    public Contact(String id, String name, String mayDayID, ContactStatus status){

        this.id = id;
        this.mayDayID = mayDayID;
        this.name     = name;
        this.status   = status;
    }

    public Contact(String mayDayID){

        this.mayDayID = mayDayID;
        this.status   = ContactStatus.UNKNOWN;
    }

    public Contact(String name, String mayDayID, ContactStatus status){
        this.mayDayID = mayDayID;
        this.name     = name;
        this.status   = status;
    }



    public void setMayDayId(String mayDayID){

        this.mayDayID = mayDayID;
    }

    public void setName(String name){

        this.name = name;
    }

    public void setStatus(ContactStatus status){

        this.status = status;
    }

    public void setStatus(String status){

        this.status = ContactStatus.valueOf(status);
    }

    public ContactStatus getStatus(){

        return this.status;
    }

    public String getIdAsString(){

        return this.id;
    }

    public void setIdAsString(String id){

        this.id = id;
    }

    public String getName(){

        return this.name;
    }

    public String getMayDayId(){

        return this.mayDayID;
    }

    /*Used to load contacts in the contactList
    @Override
    public String toString() {
        return "Contact{"
                + "MayDayID='" + mayDayID + "', "
                + "Name='" + name + "', "
                + "Status='" + status + "'}";

    }
    */
}
