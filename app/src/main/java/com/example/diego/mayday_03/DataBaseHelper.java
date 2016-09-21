package com.example.diego.mayday_03;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by diego on 8/09/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    private String log_v="DataBaseHelper";

    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "mayDay";

    // Table Names
    private static final String TABLE_CONTACT = "contact";
    private static final String TABLE_MESSAGE = "message";

    //status can be: NORMAL, UNKNOWN or BLOCKED for TABLE "contact"
    //and: (SENDING or SENT) for outgoing and (UNREAD/READ) for incoming for TABLE "message"
    private static final String COLUMN_STATUS="status";


    // Columns from table: "contact"
    private static final String COLUMN_CONTACTID = "contactID";
    private static final String COLUMN_MAYDAYID = "mayDayID";
    private static final String COLUMN_NAME = "name";




    // Columns from table: "message"
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CONTACT_MAYDAYID = "contact_MayDayID";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_DATETIME = "datetime";


    //direction can be: INCOMING or OUTGOING
    private static final String COLUMN_DIRECTION = "direction";
    //type can be: NORMAL or SELFDESTRUCTIVE
    private static final String COLUMN_TYPE = "type";


    //CREATE STATEMENTS

    //Table "contact"
    private static final String CREATE_TABLE_CONTACT=
            "CREATE TABLE " + TABLE_CONTACT + "("
                    + COLUMN_CONTACTID  + " INTEGER PRIMARY KEY,"
                    + COLUMN_MAYDAYID   + " TEXT,"
                    + COLUMN_STATUS     + " TEXT,"
                    + COLUMN_NAME       + " TEXT)";



    //Table "message"
    private static final String CREATE_TABLE_MESSAGE=
            "CREATE TABLE " + TABLE_MESSAGE + "("
                    + COLUMN_ID                 + " INTEGER PRIMARY KEY,"
                    + COLUMN_CONTACT_MAYDAYID   + " TEXT," //FK
                    + COLUMN_MESSAGE            + " TEXT,"
                    + COLUMN_DATETIME           + " DATETIME,"
                    + COLUMN_STATUS             + " TEXT,"
                    + COLUMN_DIRECTION          + " TEXT,"
                    + COLUMN_TYPE               + " TEXT)";
                    //+ "FOREIGN KEY("+ COLUMN_ID_CONTACT +") REFERENCES "+ TABLE_CONTACT+"(" + COLUMN_ID +")";


    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(log_v, "onCreate");
        db.execSQL(CREATE_TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.d(log_v, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        onCreate(db);
    }





    //CRUDO

    //Table: "contact"

    public long contactAdd(Contact contact){

        long newContactID;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MAYDAYID, contact.getMayDayId());
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_STATUS, contact.getStatus().toString());


        try {
            newContactID = db.insert(TABLE_CONTACT, null, values);
        }
        catch (SQLiteConstraintException e){
            newContactID = -1;
        }

        return newContactID;
    }


    public void contactEdit(Contact contact){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_STATUS, contact.getStatus().toString());
        values.put(COLUMN_MAYDAYID, contact.getMayDayId());

        db.update(TABLE_CONTACT, values, COLUMN_CONTACTID+"=? ", new String[]{contact.getIdAsString()});

    }

    public ArrayList<Contact> getContacts() {
        Log.v(log_v,"getContacts");
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_CONTACT;

        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst()){

            do{
                Contact newContact = new Contact();

                newContact.setIdAsString(c.getString(c.getColumnIndex(COLUMN_CONTACTID)));
                newContact.setMayDayId(c.getString(c.getColumnIndex(COLUMN_MAYDAYID)));
                newContact.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
                newContact.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));

                contactList.add(newContact);


            }while(c.moveToNext());
            c.close();
        }

        return contactList;
    }



    //CRUDO
    //Table: message

    public long messageAdd(ChatMessage chatMessage){
        Log.v(log_v, "messageAdd -> body: "+ chatMessage.getMessage());
        long newMessageID;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CONTACT_MAYDAYID, chatMessage.getContactMayDayID());
        values.put(COLUMN_MESSAGE, chatMessage.getMessage());
        values.put(COLUMN_DATETIME, chatMessage.getDatetime());
        values.put(COLUMN_STATUS, chatMessage.getStatus().toString());
        values.put(COLUMN_DIRECTION, chatMessage.getDirection().toString());
        values.put(COLUMN_TYPE, chatMessage.getType().toString());


        try {
            newMessageID = db.insert(TABLE_MESSAGE, null, values);
        }
        catch (SQLiteConstraintException e){
            newMessageID=-1;
            Log.v(log_v, "Couldn't insert message exception: "+ e.getMessage());

        }

        return newMessageID;
    }

    /*Returns the last @counter messages where contact_MayDayID=@contact_MayDayID
    Staring from message number @start_index.
    * */
    public ArrayList<ChatMessage> getMessages(String contact_MayDayID, int start_index, int counter) {
        ArrayList<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
        SQLiteDatabase db = getReadableDatabase();
        /*TODO:
            - add logic for start_index
         */
       //String query = "SELECT TOP "+ String.valueOf(counter)+ " * FROM " +TABLE_MESSAGE + "WHERE " + COLUMN_MAYDAYID + " = " +contact_MayDayID;
        String query = "SELECT * FROM "
                + TABLE_MESSAGE + " WHERE " + COLUMN_CONTACT_MAYDAYID + " =?";

        Cursor c = db.rawQuery(query, new String[]{contact_MayDayID});
        if(c != null && c.moveToFirst()){
            do{
                ChatMessage newChatMessage = new ChatMessage();
                newChatMessage.setContactMayDayId(contact_MayDayID);
                newChatMessage.setMessage(c.getString(c.getColumnIndex(COLUMN_MESSAGE)));
                newChatMessage.setDatetime(c.getString(c.getColumnIndex(COLUMN_DATETIME)));
                newChatMessage.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));
                newChatMessage.setDirection(c.getString(c.getColumnIndex(COLUMN_DIRECTION)));
                newChatMessage.setType(c.getString(c.getColumnIndex(COLUMN_TYPE)));

                chatMessageList.add(newChatMessage);

            }while(c.moveToNext());
            c.close();
        }

        return chatMessageList;
    }

    //Erase conversation(s)
    public void messageTruncate(String MayDayID){

        Log.v(log_v, "messageTruncate");
        SQLiteDatabase db = this.getWritableDatabase();

        if (MayDayID == null) {
            db.execSQL("DELETE FROM " + TABLE_MESSAGE);
        } else {
            db.execSQL("DELETE FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CONTACT_MAYDAYID + " =?", new String[]{MayDayID});
        }

    }

    //Erase contact(s)
    public void contactTruncate(String contactId){

        Log.v(log_v, "contact");
        SQLiteDatabase db = this.getWritableDatabase();

        if (contactId == null) {
            db.execSQL("DELETE FROM " + TABLE_CONTACT);
        } else {
            db.execSQL("DELETE FROM " + TABLE_CONTACT + " WHERE " + COLUMN_CONTACTID + " =?", new String[]{contactId});
        }

    }


}
