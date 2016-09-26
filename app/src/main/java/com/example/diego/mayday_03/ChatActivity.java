package com.example.diego.mayday_03;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jorge on 21/09/16.
 */
public class ChatActivity extends AppCompatActivity{

    private String log_v = "ChatActivity: ";
    private String contactMaydayId;
    private MyApplication app;
    //AbstractXMPPConnection connection;


    private EditText etMessage;
    private ListView messagesContainer;
    private Button btnSend;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

//        contactMaydayId = getIntent().getExtras().getString("contact_MayDayId");
        contactMaydayId = "jorge_spa"; //hard coded
        Log.v(log_v, "Start a chat with MayDayId: " + contactMaydayId);

        app = (MyApplication) getApplication();
        app.createChat("jorge_spa@jorge-latitude-e5440"); //hard coded
        app.setChatActivity(this);
        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        etMessage         = (EditText) findViewById(R.id.messageEdit);
        btnSend           = (Button)   findViewById(R.id.chatSendButton);

        TextView meLabel         = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel  = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("Jorge SPA"); //Hard coded
        meLabel.setText("Diego SPA");

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        //loadDummyHistory();
        loadChatDbHistory();
        //startAsyncListener();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                String messageText = etMessage.getText().toString();
                if(TextUtils.isEmpty(messageText)){
                    return;
                }
                try{
                    DataBaseHelper db = new DataBaseHelper(v.getContext());
                    ChatMessage outGoingMessage = new ChatMessage();
                    setOutgoingMessageProp(messageText, outGoingMessage);
                    app.sendMessage(messageText);
                    db.getWritableDatabase();
                    db.messageAdd(outGoingMessage);
                    db.close();
                    etMessage.setText("");
                    displayMessage(outGoingMessage);

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }



        });

    }

    private void setOutgoingMessageProp(String messageText, ChatMessage chatMessage) {
        chatMessage.setContactMayDayId(contactMaydayId);
        chatMessage.setMessage(messageText);
        chatMessage.setDatetime(DateFormat.getDateTimeInstance()
                .format(new Date()));
        chatMessage.setDirection(ChatMessageDirection.OUTGOING);//setme(true)
        chatMessage.setType(ChatMessageType.NORMAL);
        chatMessage.setStatus(ChatMessageStatus.SENDING);
    }

    private void loadChatDbHistory() {
        Log.v(log_v, "Loading conversation history... \n");
        try {
            DataBaseHelper db = new DataBaseHelper(this);
            this.chatHistory  = db.getMessages(contactMaydayId, 0, 5);
            adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
            messagesContainer.setAdapter(adapter);

            if(this.chatHistory.isEmpty()) {
                    Log.v(log_v, "Empty Conversation");
            }else{

                for(ChatMessage message : this.chatHistory) {
                    System.out.println(message.getDirection().toString() + " : " + message.getMessage());
                    displayMessage(message);

                }
            }
            db.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    public void scroll() {
        messagesContainer.setSelection((messagesContainer.getCount() - 1));
    }

    public ChatAdapter getChatAdapter(){
        return this.adapter;
    }

    public String getCurrentConversationId(){
        return this.contactMaydayId;
    }


}
