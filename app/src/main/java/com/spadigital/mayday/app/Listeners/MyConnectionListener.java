package com.spadigital.mayday.app.Listeners;

import android.content.Context;

import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Helpers.DataBaseHelper;
import com.spadigital.mayday.app.MayDayApplication;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;

/**
 * Created by jorge on 10/10/16.
 * This class is listening between network and server connection, if there is an error
 * it tries to reconnect and sends pending messages (if successful)
 */
public class MyConnectionListener implements ConnectionListener {

    private final Context context;

    public MyConnectionListener(Context context){
        this.context = context;
    }

    @Override
    public void connected(XMPPConnection connection) {
        //TODO
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

        DataBaseHelper db = new DataBaseHelper(context);
        ArrayList<ChatMessage> chatHistory = db.getSendingMessages();
        db.close();

        MayDayApplication app = (MayDayApplication) context.getApplicationContext();
        for (ChatMessage message: chatHistory) {
            app.createChat(message.getContactMayDayID() + "@" + MayDayApplication.DOMAIN);
            app.sendMessage(message, true);
        }
    }

    @Override
    public void connectionClosed() {
        //TODO
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        //TODO
    }

    @Override
    public void reconnectionSuccessful() {

        DataBaseHelper db = new DataBaseHelper(context);
        ArrayList<ChatMessage> chatHistory = db.getSendingMessages();
        db.close();

        MayDayApplication app = (MayDayApplication) context.getApplicationContext();
        for (ChatMessage message: chatHistory) {
            app.createChat(message.getContactMayDayID() + "@" + MayDayApplication.DOMAIN);
            app.sendMessage(message,true);
        }
    }

    @Override
    public void reconnectingIn(int seconds) {
        //TODO

    }

    @Override
    public void reconnectionFailed(Exception e) {
        //TODO
    }
}
