package com.example.diego.mayday_03;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import java.util.ArrayList;

/**
 * Created by diego on 13/09/16.
 */
public class ConversationActivity extends AppCompatActivity {


    private String log_v = "Conversation: ";
    private String contact_MayDayID = "";
    MyApplication app;

    //Only used to Listen for incoming messages
    AbstractXMPPConnection connection;

    //used for the contactList
    ListView messageListView;
    ChatAdapter messageListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(log_v, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Bundle params = getIntent().getExtras();
        contact_MayDayID = params.getString("contact_MayDayID");
        Log.v(log_v, "Start a chat with MayDayID: " + contact_MayDayID);

        app = (MyApplication) getApplication();

        //Testing
        //app.createChat(contact_MayDayID);
        app.createChat("jorge_spa@jorge-latitude-e5440");
        connection = app.getConnection();

        loadMessages();
        startListening();

/*
        final ScrollView scroll_view = (ScrollView) findViewById(R.id.sv_messageList);
        scroll_view.post(new Runnable() {
            @Override
            public void run() {
                // This method works but animates the scrolling
                // which looks weird on first load
                 scroll_view.fullScroll(View.FOCUS_DOWN);

                // This method works even better because there are no animations.
                //scroll_view.scrollTo(0, scroll_view.getBottom());
            }
        });
*/

    }


    private void loadMessages() {
        Log.v(log_v, "loadMessages");
        DataBaseHelper db = new DataBaseHelper(this);
        ArrayList<ChatMessage> array_list = db.getMessages(contact_MayDayID, 0, 5);

        if(array_list.isEmpty()){
            Log.v(log_v, "Empty conversation");

        }
        else {
            //Debug
            for (ChatMessage chatMessage : array_list) {
                System.out.println(chatMessage.getDirection().toString() + " : " + chatMessage.getMessage());
            }
            messageListView = (ListView) findViewById(R.id.lv_messageList);
            messageListAdapter = new ChatAdapter(getApplicationContext(), array_list);
            messageListView.setAdapter(messageListAdapter);
        }
        db.close();

    }

    /*Listen for incoming messages that belongs to the current conversation
        Reload conversation to update the conversation listView
     */
    private void startListening() {
        if (connection != null) {
            this.connection.addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Log.v(log_v, "RECEIVING packet");
                    try {
                        Message message = (Message) packet;
                        if (message.getBody() == null) {
                            //This is a control message like "composing, pause, etc"
                        }
                        //Code to execute when a message has arrived
                        else{
                            Log.v(log_v, "PACKET is a message");
                            String[] parts = message.getFrom().toString().split("@");
                            String from = parts[0];

                            //does the message belongs to this ocnversation?
                            if(from.equals(contact_MayDayID)){
                                Log.v(log_v, "LOADING due to INCOMING message");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        /*TODO:
                                            -Can we modify direct through the ChatAdapter
                                         */
                                        //update conversation
                                        loadMessages();
                                    }
                                });

                            }
                            else {
                                Log.v(log_v, "Message wasn't for me(" + contact_MayDayID + ") but for: " + from);
                            }

                        }
                    }
                    catch(ClassCastException e) {
                        Log.v(log_v, "Exception in startListening: "+ e.getMessage().toString());
                    }


                }
            }, null);

        }

    }

    //Called when the Send button is pressed in a ConversationActivity
    public void click_send_message(View view) {

        Log.v(log_v, "click_message_send()");
        DataBaseHelper db = new DataBaseHelper(this);

        EditText message = (EditText) findViewById(R.id.et_message);
        String str_message = message.getText().toString();
        message.setText("");

        //Use the connection defined previously within @app, to send the message
        app.sendMessage(str_message);

        //Create an object "ChatMessage" and use it to save it in the DB.
        ChatMessage msg = new ChatMessage();
        msg.setContactMayDayId(contact_MayDayID);
        msg.setMessage(str_message);
        /*TODO:
            -define whether this datetime is ok
            -should we add an extension for datetime?
                https://ramzandroidarchive.wordpress.com/2016/03/14/add-custum-tags-in-message-using-smack-4-1/
         */
        msg.setDatetime(String.valueOf(System.currentTimeMillis()));
        msg.setStatus(ChatMessageStatus.SENDING);
        msg.setDirection(ChatMessageDirection.OUTGOING);
        msg.setType(ChatMessageType.NORMAL);

        db.getWritableDatabase();
        db.messageAdd(msg);
        db.close();
        //loadMessages();
        messageListAdapter.add(msg);
    }


    //Called when the Send button is pressed in a ConversationActivity
    public void click_erase_conversation(View view) {
        Log.v(log_v, "click_erase_conversation");
        DataBaseHelper db = new DataBaseHelper(this);
        db.messageTruncate(null);
        db.close();
        messageListAdapter.clear();

    }


}