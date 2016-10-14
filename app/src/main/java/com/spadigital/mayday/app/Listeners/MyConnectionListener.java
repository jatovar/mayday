package com.spadigital.mayday.app.Listeners;

import android.content.Context;

import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Models.DataBaseHelper;
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

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

        DataBaseHelper db = new DataBaseHelper(context);
        ArrayList<ChatMessage> chatHistory = db.getSendingMessages();
        db.close();

        MayDayApplication app = (MayDayApplication) context.getApplicationContext();
        for (ChatMessage message: chatHistory) {
            app.createChat(message.getContactMayDayID() + "@jorge-latitude-e5440"); //hard coded
            app.sendMessage(message,true);
        }
    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }

    @Override
    public void reconnectionSuccessful() {

        DataBaseHelper db = new DataBaseHelper(context);
        ArrayList<ChatMessage> chatHistory = db.getSendingMessages();
        db.close();

        MayDayApplication app = (MayDayApplication) context.getApplicationContext();
        for (ChatMessage message: chatHistory) {
            app.createChat(message.getContactMayDayID() + "@jorge-latitude-e5440"); //hard coded
            app.sendMessage(message,true);
        }
    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {

    }
}
