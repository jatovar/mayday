package com.spadigital.mayday.app.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Enum.ContactStatus;

import java.util.ArrayList;

/**
 * Created by mayday on 8/09/16.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    private String log_v = "DataBaseHelper";

    private static final int DATABASE_VERSION = 10;
    //7 - added contact id primary key
    //8 - added message expiretime field
    //9 - added message emergency field
    //10 - added subject and redirected fields

    // Database Name
    private static final String DATABASE_NAME = "mayDay";

    // Table Names
    private static final String TABLE_CONTACT = "contact";
    private static final String TABLE_MESSAGE = "message";

    //status can be: NORMAL, UNKNOWN or BLOCKED for TABLE "contact"
    //and: (SENDING or SENT) for outgoing and (UNREAD/READ) for incoming for TABLE "message"
    private static final String COLUMN_STATUS ="status";


    // Columns from table: "contact"
    private static final String COLUMN_CONTACTID = "contactID";
    private static final String COLUMN_MAYDAYID  = "mayDayID";
    private static final String COLUMN_NAME      = "name";




    // Columns from table: "message"
    private static final String COLUMN_ID               = "id";
    private static final String COLUMN_CONTACT_MAYDAYID = "contact_MayDayID";
    private static final String COLUMN_MESSAGE          = "message";
    private static final String COLUMN_DATETIME         = "datetime";
    private static final String COLUMN_EXPIRETIME       = "expiretime";
    private static final String COLUMN_EMERGENCY        = "emergency";
    private static final String COLUMN_SUBJECT          = "subject";
    private static final String COLUMN_REDIRECTED       = "redirected";


    //direction can be: INCOMING or OUTGOING
    private static final String COLUMN_DIRECTION = "direction";
    //type can be: NORMAL or SELFDESTRUCTIVE
    private static final String COLUMN_TYPE      = "type";


    //CREATE STATEMENTS

    //Table "contact"
    private static final String CREATE_TABLE_CONTACT =
            "CREATE TABLE " + TABLE_CONTACT + "("
                    + COLUMN_CONTACTID  + " INTEGER PRIMARY KEY,"
                    + COLUMN_MAYDAYID   + " TEXT,"
                    + COLUMN_STATUS     + " TEXT,"
                    + COLUMN_NAME       + " TEXT)";



    //Table "message"
    private static final String CREATE_TABLE_MESSAGE =
            "CREATE TABLE " + TABLE_MESSAGE + "("
                    + COLUMN_ID                 + " INTEGER PRIMARY KEY,"
                    + COLUMN_CONTACT_MAYDAYID   + " TEXT," //FK
                    + COLUMN_MESSAGE            + " TEXT,"
                    + COLUMN_DATETIME           + " DATETIME,"
                    + COLUMN_STATUS             + " TEXT,"
                    + COLUMN_DIRECTION          + " TEXT,"
                    + COLUMN_EXPIRETIME         + " TEXT,"
                    + COLUMN_EMERGENCY          + " INTEGER DEFAULT 0,"
                    + COLUMN_SUBJECT            + " TEXT,"
                    + COLUMN_REDIRECTED         + " INTEGER DEFAULT 0,"
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
        Log.v(log_v, "getContacts");
        ArrayList<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACT;

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
        values.put(COLUMN_EMERGENCY,        chatMessage.getIsEmergency());
        values.put(COLUMN_CONTACT_MAYDAYID, chatMessage.getContactMayDayID());
        values.put(COLUMN_MESSAGE,          chatMessage.getMessage());
        values.put(COLUMN_DATETIME,         chatMessage.getDatetime());
        values.put(COLUMN_STATUS,           chatMessage.getStatus().toString());
        values.put(COLUMN_DIRECTION,        chatMessage.getDirection().toString());
        values.put(COLUMN_EXPIRETIME,       chatMessage.getExpireTime());
        values.put(COLUMN_TYPE,             chatMessage.getType().toString());
        values.put(COLUMN_EMERGENCY,        chatMessage.getIsEmergency() ? 1 : 0);
        values.put(COLUMN_SUBJECT,          chatMessage.getSubject());
        values.put(COLUMN_REDIRECTED,       chatMessage.getRedirected() ? 1 : 0);

        try {
            newMessageID = db.insert(TABLE_MESSAGE, null, values);
            chatMessage.setId((int)newMessageID);
        }
        catch (SQLiteConstraintException e){
            newMessageID = -1;
            Log.v(log_v, "Couldn't insert message exception: "+ e.getMessage());
        }

        return newMessageID;
    }
    public void updateRead(String mayDayId){
        // (UNREAD/READ) for incoming for TABLE "message"
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_STATUS, "READ");

        db.update(TABLE_CONTACT, values, COLUMN_MAYDAYID + "=? AND " + COLUMN_DIRECTION +
                "= OUTGOING", new String[]{mayDayId});
    }

    /**This method returns the last message of each conversation leaving author if it exists in
     * contacts DB otherwise is a unknown message received**/
    public ArrayList<ChatMessage> getConversationsLastestMessage(){

        ArrayList<Contact> contacts             = this.getContacts();
        ArrayList<ChatMessage> chatMessageList  = new ArrayList<>();
        SQLiteDatabase db                       = getReadableDatabase();

      //  String query = "SELECT tbl.* FROM ( SELECT * FROM " + TABLE_MESSAGE + " ORDER BY " + COLUMN_ID +
      //          " ASC ) AS tbl GROUP BY tbl." + COLUMN_CONTACT_MAYDAYID + " ORDER BY " + COLUMN_ID + " DESC";
        String query = "SELECT tbl.* FROM ( SELECT * FROM " + TABLE_MESSAGE + " ORDER BY " + COLUMN_ID +
                " ASC ) AS tbl GROUP BY tbl." + COLUMN_CONTACT_MAYDAYID + ", tbl." + COLUMN_REDIRECTED  +
                " ORDER BY " + COLUMN_ID + " DESC";
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()){
            do{
                ChatMessage msg = new ChatMessage();
                msg.setContactMayDayId(c.getString(c.getColumnIndex(COLUMN_CONTACT_MAYDAYID)));
                msg.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));
                msg.setType(c.getString(c.getColumnIndex(COLUMN_TYPE)));
                msg.setExpireTime(c.getString(c.getColumnIndex(COLUMN_EXPIRETIME)));
                msg.setMessage(c.getString(c.getColumnIndex(COLUMN_MESSAGE)));
                msg.setDatetime(c.getString(c.getColumnIndex(COLUMN_DATETIME)));
                msg.setDirection(c.getString(c.getColumnIndex(COLUMN_DIRECTION)));
                msg.setSubject(c.getString(c.getColumnIndex(COLUMN_SUBJECT)));
                msg.setRedirected(c.getInt(c.getColumnIndex(COLUMN_REDIRECTED)) == 1);
                chatMessageList.add(msg);
            }while (c.moveToNext());
            c.close();
        }
         //TODO: OPTIMIZATION - This should not be done, maybe a FK necessary
        // message-contact?? Perhaps not because not every message has a contact saved in DB
        for (ChatMessage message : chatMessageList){
            for(Contact contact : contacts){
                if(contact.getMayDayId().equals(message.getContactMayDayID()))
                    message.setAuthor(contact.getName());
            }
            if(message.getAuthor().equals(""))
                message.setAuthor(" UNKNOWN ");
        }
        return chatMessageList;
    }

    public ContactStatus findContactStatus(String mayDayId){
        Contact contact = new Contact();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM "
                + TABLE_CONTACT + " WHERE " + COLUMN_MAYDAYID + " = '"
                + mayDayId + "'";

        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst()){
            contact.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));
            c.close();
        }

        return contact.getStatus();
    }
    /*Returns the last @counter messages where contact_MayDayID=@contact_MayDayID
    Staring from message number @start_index.
    * */
    public ArrayList<ChatMessage> getMessages(String contact_MayDayID, int start_index, int counter) {
        ArrayList<ChatMessage> chatMessageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        //TODO: OPTIMIZATION  - add logic for start_index
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, String.valueOf(ChatMessageStatus.READ));
        db.update(TABLE_MESSAGE, cv , COLUMN_CONTACT_MAYDAYID + " =? AND "+ COLUMN_DIRECTION + "= 'INCOMING' "
                , new String[]{contact_MayDayID});

        String query = "SELECT * FROM "
                + TABLE_MESSAGE + " WHERE " + COLUMN_CONTACT_MAYDAYID + " =?";

        Cursor c = db.rawQuery(query, new String[]{contact_MayDayID});
        if(c != null && c.moveToFirst()){
            do{
                ChatMessage newChatMessage = new ChatMessage();
                newChatMessage.setContactMayDayId(contact_MayDayID);
                newChatMessage.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                newChatMessage.setMessage(c.getString(c.getColumnIndex(COLUMN_MESSAGE)));
                newChatMessage.setDatetime(c.getString(c.getColumnIndex(COLUMN_DATETIME)));
                newChatMessage.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));
                newChatMessage.setDirection(c.getString(c.getColumnIndex(COLUMN_DIRECTION)));
                newChatMessage.setType(c.getString(c.getColumnIndex(COLUMN_TYPE)));
                newChatMessage.setExpireTime(c.getString(c.getColumnIndex(COLUMN_EXPIRETIME)));
                newChatMessage.setIsEmergency(c.getInt(c.getColumnIndex(COLUMN_EMERGENCY)) == 1);
                newChatMessage.setSubject(c.getString(c.getColumnIndex(COLUMN_SUBJECT)));
                newChatMessage.setRedirected(c.getInt(c.getColumnIndex(COLUMN_REDIRECTED)) == 1);
                chatMessageList.add(newChatMessage);

            }while(c.moveToNext());
            c.close();
        }

        return chatMessageList;
    }

    public ArrayList<ChatMessage> getSendingMessages() {
        ArrayList<ChatMessage> chatMessageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM "
                + TABLE_MESSAGE + " WHERE " + COLUMN_STATUS + " = '"
                + String.valueOf(ChatMessageStatus.SENDING)+"'";
        //TODO:UPDATE
        Cursor c = db.rawQuery(query, null);
        if(c != null && c.moveToFirst()){
            do{
                ChatMessage newChatMessage = new ChatMessage();
                newChatMessage.setContactMayDayId(c.getString(c.getColumnIndex(COLUMN_CONTACT_MAYDAYID)));
                newChatMessage.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                newChatMessage.setMessage(c.getString(c.getColumnIndex(COLUMN_MESSAGE)));
                newChatMessage.setDatetime(c.getString(c.getColumnIndex(COLUMN_DATETIME)));
                newChatMessage.setStatus(c.getString(c.getColumnIndex(COLUMN_STATUS)));
                newChatMessage.setDirection(c.getString(c.getColumnIndex(COLUMN_DIRECTION)));
                newChatMessage.setType(c.getString(c.getColumnIndex(COLUMN_TYPE)));
                newChatMessage.setExpireTime(c.getString(c.getColumnIndex(COLUMN_EXPIRETIME)));
                newChatMessage.setIsEmergency(c.getInt(c.getColumnIndex(COLUMN_EMERGENCY)) == 1);
                newChatMessage.setSubject(c.getString(c.getColumnIndex(COLUMN_SUBJECT)));
                newChatMessage.setRedirected(c.getInt(c.getColumnIndex(COLUMN_REDIRECTED)) == 1);
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
            db.execSQL("DELETE FROM " + TABLE_MESSAGE + " WHERE " + COLUMN_CONTACT_MAYDAYID + " =?",
                    new String[]{MayDayID});
        }

    }

    //Erase contact(s)
    public void contactTruncate(String contactId){

        Log.v(log_v, "contact");
        SQLiteDatabase db = this.getWritableDatabase();

        if (contactId == null) {
            db.execSQL("DELETE FROM " + TABLE_CONTACT);
        } else {
            db.execSQL("DELETE FROM " + TABLE_CONTACT + " WHERE " + COLUMN_CONTACTID + " =?",
                    new String[]{contactId});
        }

    }


    public void messageRemove(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGE, COLUMN_ID + "=" + String.valueOf(id), null);
        db.close();
    }


    public ArrayList<Contact> getBlockedContacts() {
        Log.v(log_v, "getBlockedContacts");
        ArrayList<Contact> contactList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CONTACT + " WHERE " + COLUMN_STATUS+ " = 'BLOCKED' ";

        Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst()){

            do{
                Contact newContact = new Contact();
                newContact.setMayDayId(c.getString(c.getColumnIndex(COLUMN_MAYDAYID)));
                newContact.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));

                contactList.add(newContact);


            }while(c.moveToNext());
            c.close();
        }

        return contactList;
    }

    public void updateSendingMessage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "SENT");
        db.update(TABLE_MESSAGE, values, COLUMN_ID + "=" + String.valueOf(id), null);
        db.close();
    }

    public void unblockContact(String checkedField) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "NORMAL");
        db.update(TABLE_CONTACT, values, COLUMN_MAYDAYID + "=" + "'" + checkedField + "'", null);
        db.close();
    }

    public void updateReadingMessage(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "READ");
        db.update(TABLE_MESSAGE, values, COLUMN_ID + "=" + id, null);
        db.close();
    }
}
