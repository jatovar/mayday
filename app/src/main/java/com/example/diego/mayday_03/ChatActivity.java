package com.example.diego.mayday_03;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jorge on 21/09/16.
 */
public class ChatActivity extends AppCompatActivity{

    static final int ADD_CONTACT_REQUEST = 1;
    static final int EDIT_CONTACT_REQUEST = 2;

    private String log_v = "ChatActivity: ";
    private String contactMaydayId;
    private String author;
    private MyApplication app;
    //AbstractXMPPConnection connection;


    private EditText etMessage;
    private ListView messagesContainer;
    private ImageButton btnSend;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;

    private Toolbar myToolbar;
    private Contact contact;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation2);

        contactMaydayId = getIntent().getExtras().getString("contact_MayDayID");
        //contactMaydayId = "jorge_spa"; //hard coded
        author = getIntent().getExtras().getString("contact_author");

        Log.v(log_v, "Start a chat with MayDayId: " + contactMaydayId);

        app = (MyApplication) getApplication();
        app.createChat(contactMaydayId + "@jorge-latitude-e5440"); //hard coded
        app.setChatActivity(this);
        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_contact:
                Log.i("ActionBar", "Add contact!");
                //TODO: Toast if contact already exists, add contact activity? if it doesn't
                return true;
            case R.id.action_detail_contact:
                Log.i("ActionBar", "Info!");

                if((contact = app.getContactsFragment().findContactById(contactMaydayId)) != null)
                {
                    Intent intent = new Intent(getApplicationContext(), ContactInformationActivity.class);

                    intent.putExtra("editing_contact_id",       contact.getIdAsString());
                    intent.putExtra("editing_contact_MayDayID", contact.getMayDayId());
                    intent.putExtra("editing_contact_name",     contact.getName());
                    intent.putExtra("editing_contact_status",   contact.getStatus());
                    startActivityForResult(intent, EDIT_CONTACT_REQUEST);
                }
                else
                {
                    Toast toast = Toast.makeText(
                            this.getApplicationContext(),
                            "Contacto no existe, favor de agregarlo",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(log_v, "Result callback");
        // Make sure the request was successful
        if (resultCode == RESULT_OK) {
            Log.v(log_v, "RESULT OK, REFRESH CONTACT LIST DEPENDING THE CASE");
            // Check which request we're responding to
            switch (requestCode){
                case EDIT_CONTACT_REQUEST:
                    //We must refresh it all here...... the Contacts Fragment, the Conversations Fragment, and the ChatActivity menu title
                    Log.v(log_v, "EDIT_CONTACT_REQUEST");
                    //This refreshes the contacts fragments...
                    if(data.getBooleanExtra("is_deleting", false))
                    {
                        app.getContactsFragment().deleteContactInDataSet(data);
                        app.getConversationsFragment().setToUnknownContactIfExists(data);
                        getSupportActionBar().setTitle("Unknown");
                    }
                    else {
                        app.getContactsFragment().modifyContactInDataSet(data);
                        app.getConversationsFragment().setContactInfoIfExists(data);
                        getSupportActionBar().setTitle(data.getStringExtra("modified_contact_name"));

                    }
                    break;

            }

        }
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        etMessage         = (EditText) findViewById(R.id.messageEdit);
        btnSend           = (ImageButton) findViewById(R.id.chatSendButton);
        myToolbar         = (Toolbar) findViewById(R.id.my_toolbar);

        //TextView meLabel         = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel  = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(author);
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setPadding(100,0,0,0);
        myToolbar.setSubtitleTextColor(Color.WHITE);
        myToolbar.setSubtitle(contactMaydayId);
        //companionLabel.setText("Jorge SPA"); //Hard coded
        //meLabel.setText("Diego SPA");

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        loadChatDbHistory();

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
                    app.getConversationsFragment().addOutgoingMessage(outGoingMessage);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }



        });

    }

    private void setOutgoingMessageProp(String messageText, ChatMessage chatMessage) {
        chatMessage.setContactMayDayId(contactMaydayId);
        chatMessage.setAuthor(author);
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

    private void scroll() {
        messagesContainer.setSelection((messagesContainer.getCount() - 1));
    }


    public String getCurrentConversationId(){
        return this.contactMaydayId;
    }



}
