package com.example.diego.mayday_03;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jorge on 21/09/16.
 */
public class ChatActivity extends AppCompatActivity{
    private EditText etMessage;
    private ListView messagesContainer;
    private Button btnSend;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);
        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        etMessage         = (EditText) findViewById(R.id.messageEdit);
        btnSend           = (Button)   findViewById(R.id.chatSendButton);

        TextView meLabel         = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel  = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("Nombre del amigo"); //Hard coded
        loadDummyHistory();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                String messageText = etMessage.getText().toString();
                if(TextUtils.isEmpty(messageText)){
                    return;
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(122);
                chatMessage.setMessage(messageText);
                chatMessage.setDatetime(DateFormat.getDateTimeInstance()
                        .format(new Date()));
                chatMessage.setDirection(ChatMessageDirection.OUTGOING);//setme(true)
                etMessage.setText("");
                displayMessage(chatMessage);
            }



        });

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection((messagesContainer.getCount() - 1));
    }

    private void loadDummyHistory() {
        chatHistory = new ArrayList<>();

        ChatMessage msg  = new ChatMessage();
        msg.setId(1);
        msg.setDirection(ChatMessageDirection.INCOMING);//setMe(false)
        msg.setMessage("¡¡Hola!!");
        msg.setDatetime(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);

        ChatMessage msg1 = new ChatMessage();
        msg1.setId(2);
        msg1.setDirection(ChatMessageDirection.INCOMING);
        msg1.setMessage("¿Como estas?");
        msg1.setDatetime(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        ChatMessage msg2 = new ChatMessage();
        msg2.setId(2);
        msg2.setDirection(ChatMessageDirection.OUTGOING);
        msg2.setMessage("Muy bien. ¿Y tú como estas?");
        msg2.setDatetime(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg2);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i = 0; i < chatHistory.size(); i++){
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }



}
