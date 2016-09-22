package com.example.diego.mayday_03;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by diego on 12/09/16.
 */
public class MyApplication extends Application {


    private String log_v = "MyApplication: ";
    private String log_e = "MyApplicationERROR: ";

    private static final String DOMAIN   = "jorge-latitude-e5440";
    private static final String HOST     = "192.168.168.67";
    private static final int PORT        = 5222;
    private static final String RESOURCE = "Android";


    private String mayDayID = "";
    private String password = "";


    Boolean connected = false;
    Boolean logged    = false;

    ChatManager chatManager;
    AbstractXMPPConnection connection;
    Chat chat;


    @Override
    public void onCreate(){
        super.onCreate();
    }

    public void setCredentials(String mayDayId, String password){

        this.mayDayID = mayDayId;
        this.password = password;

    }

    public void connect() {

        createConnection();
        connectAndLogin();

    }

    public Boolean isConnected(){

        return connection.isConnected();

    }

    public void createChat(String contactString){

        chatManager = ChatManager.getInstanceFor(connection);
        chat        = chatManager.createChat(contactString);

    }
    public void sendMessage(String message){

        Log.v(log_v, "Send_message");

        try {

            chat.sendMessage(message);
        }
        catch (SmackException.NotConnectedException e) {

            Log.d(log_e, "Exception at sendMsg " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void startListening() {
        if (connection != null) {
            this.connection.addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    /*TODO:
                        -Parse timestamp
                        -Insert message in DB
                     */
                        try {

                            Message message = (Message) packet;
                            ChatMessage chatMessage = new ChatMessage();
                            String body = message.getBody();

                            if(body == null){
                                //This is a control message like "composing, pause, etc"
                            }
                            else {
                                //Split MayDayID@DOMAIN/RESOURCE into MayDayID(0) and DOMAIN/RESOURCE(1)
                                String[] parts = message.getFrom().toString().split("@");
                                String from = parts[0];

                                Log.d("DEBUGING: ", "FROM: " + from);
                                Log.d("DEBUGING: ", "BODY: " + body);
                                /*TODO:
                                    -Extract the datetime from incoming message
                                    -Extract TYPE from incoming message
                                */
                                chatMessage.setContactMayDayId(from);
                                chatMessage.setMessage(body);
                                //Change this to incoming info
                                chatMessage.setDatetime(DateFormat.getDateTimeInstance()
                                        .format(new Date()));
                                //**The status should change if the user is in ChatActivity**//
                                chatMessage.setStatus(ChatMessageStatus.UNREAD);
                                chatMessage.setDirection(ChatMessageDirection.INCOMING);
                                //Change this to incoming info
                                chatMessage.setType(ChatMessageType.NORMAL);

                                insertMessageInDb(chatMessage);
                            }

                        }
                        catch(ClassCastException e) {

                            Log.v(log_v, "Exception in startListening: "+ e.getMessage().toString());
                        }


                }
            }, null);


        }
    }

    private void insertMessageInDb(ChatMessage chatMessage){

        Log.v(log_v, "insertMessageInDb");

        DataBaseHelper db = new DataBaseHelper(this);
        db.getWritableDatabase();
        db.messageAdd(chatMessage);
        db.close();
    }

    public AbstractXMPPConnection getXMPPConnection(){

        return connection;

    }




    private void createConnection() {

        Log.v(log_v,"createConnection");

        XMPPTCPConnectionConfiguration.Builder configBuilder
                = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(mayDayID, password);
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        //TODO: CHANGE to enable
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource(RESOURCE);
        //TODO: CHANGE to false
        configBuilder.setDebuggerEnabled(true);
        connection = new XMPPTCPConnection(configBuilder.build());
    }

    private void connectAndLogin(){
        Log.v(log_v, "connectAndLogin");
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            //This is executed in the Background thread
            protected Boolean doInBackground(Void... arg0) {
                Boolean result = false;
                try {

                    connection.connect();
                    connected = true;
                    if(login())
                        result = true;

                }
                catch (XMPPException | SmackException | IOException e){

                    Log.v(log_e, "Exception at connectConnection " + e.getMessage());
                }

                return result;
            }

            //TODO: Add OnProgress()

            @Override
            //This is executed in the UI thread
            protected void onPostExecute(Boolean ret){
                if(ret){
                    Log.v(log_v, "LOGIN SUCCESS!");
                    logged = true;
                }
                else {
                    Log.v(log_v, "LOGIN FAILED!");
                }

            }
        };
        connectionThread.execute();
    }

    private Boolean login(){
        Boolean result = false;

        try {

            connection.login();
            result = true;
        }
        catch (XMPPException | SmackException | IOException e){

            Log.v(log_e, "Exception at connectConnection" + e.getMessage());
        }
        return result;
    }



}
