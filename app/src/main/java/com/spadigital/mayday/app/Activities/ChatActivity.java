package com.spadigital.mayday.app.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.spadigital.mayday.app.Adapters.ChatAdapter;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.Fragments.ContactsFragment;
import com.spadigital.mayday.app.Fragments.ConversationsFragment;
import com.spadigital.mayday.app.Helpers.DataBaseHelper;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ChatMessageDirection;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Enum.ChatMessageType;
import com.spadigital.mayday.app.MayDayApplication;
import com.spadigital.mayday.app.R;
import com.spadigital.mayday.app.Tasks.AlarmReceiver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jorge on 21/09/16.
 * This is the activity where the user establish a conversation with another user
 */
public class ChatActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private static final int EDIT_CONTACT_REQUEST = 2;
    private static final int ADD_CONTACT_REQUEST = 1;

    private String log_v = "ChatActivity: ";
    private String contactMaydayId;
    private String author;


    private EditText etMessage;
    private ListView messagesContainer;
    private ChatAdapter adapter;
    private static ChatActivity instance;
    private CheckBox isEmergencyMessage;
    private String subject;
    private Boolean redirected;
    Toolbar myToolbar;

    public static ChatActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        instance = this;
        setContentView(R.layout.activity_chat_conversation);

        contactMaydayId = getIntent().getExtras().getString("contact_MayDayID");
        author = getIntent().getExtras().getString("contact_author");

        //*Ignore this fields if the client didn't transfer his account**//
        subject = getIntent().getExtras().getString("contact_subject");
        redirected = getIntent().getExtras().getBoolean("contact_redirected");

        Log.v(log_v, "Start a chat with MayDayId: " + contactMaydayId);

        MayDayApplication.getInstance().createChat(contactMaydayId + "@" + MayDayApplication.DOMAIN);
        initControls();

        //The alarm receiver can be null if the user is only selecting a contact to chat with
        //so we have to ensure there is actually an alarm Receiver
        if(AlarmReceiver.mVibrator != null && AlarmReceiver.mPlayer != null){
            AlarmReceiver.mPlayer.stop();
            AlarmReceiver.mVibrator.cancel();
        }

    }
    @Override
    protected void onResume() {

        super.onResume();


        if(AlarmReceiver.mVibrator != null && AlarmReceiver.mPlayer != null){
            AlarmReceiver.mPlayer.stop();
            AlarmReceiver.mVibrator.cancel();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(subject != null)
        switch (item.getItemId()) {
            case R.id.action_add_contact:
                Log.i("ActionBar", "Add contact!");
                Log.v(log_v,"clickContactAdd");
                Intent intentContactAdd = new Intent(getApplicationContext(), ContactAddActivity.class);
                intentContactAdd.putExtra("active_chat_contact", contactMaydayId);
                startActivityForResult(intentContactAdd, ADD_CONTACT_REQUEST);
                return true;
            case R.id.action_detail_contact:
                Log.i("ActionBar", "Info!");

                Contact contact;
                if((contact = ContactsFragment.getInstance().findContactById(contactMaydayId)) != null) {
                    Intent intent = new Intent(getApplicationContext(),
                            ContactAddActivity.ContactInformationActivity.class);

                    intent.putExtra("editing_contact_id",       contact.getIdAsString());
                    intent.putExtra("editing_contact_MayDayID", contact.getMayDayId());
                    intent.putExtra("editing_contact_name",     contact.getName());
                    intent.putExtra("editing_contact_status",   contact.getStatus());
                    startActivityForResult(intent, EDIT_CONTACT_REQUEST);
                } else {
                    Toast toast = Toast.makeText(
                            this.getApplicationContext(),
                            "Contacto no existe, favor de agregarlo",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

                return true;
            case R.id.action_block_contact:
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Bloquear contacto")
                        .setMessage("¿Estás seguro que deseas bloquear este contacto?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DataBaseHelper db = new DataBaseHelper(ChatActivity.this);
                                ArrayList<Contact> contacts = db.getContacts();
                                boolean found = false;
                                for (Contact c: contacts) {
                                    if (c.getMayDayId().equals(contactMaydayId)){
                                        c.setStatus(ContactStatus.BLOCKED);
                                        db.contactEdit(c);
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found){
                                    Contact newContact = new Contact("Unknown" ,
                                            contactMaydayId,
                                            ContactStatus.BLOCKED);
                                    db.contactAdd(newContact);
                                }

                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
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
                    //We must refresh it all here...... the Contacts Fragment,
                    // the Conversations Fragment, and the ChatActivity menu title
                    Log.v(log_v, "EDIT_CONTACT_REQUEST");
                    //This refreshes the contacts fragments...
                    if(data.getBooleanExtra("is_deleting", false))
                    {
                        ContactsFragment.getInstance().deleteContactInDataSet(data);
                        ConversationsFragment.getInstance().setToUnknownContactIfExists(data);
                        getSupportActionBar().setTitle("Unknown");
                    }
                    else {
                        ContactsFragment.getInstance().modifyContactInDataSet(data);
                        ConversationsFragment.getInstance().setContactInfoIfExists(data);
                        getSupportActionBar().setTitle(data.getStringExtra("modified_contact_name"));
                    }
                    break;
                case ADD_CONTACT_REQUEST:
                    Log.v(log_v, "ADD_CONTACT_REQUEST");
                    ContactsFragment.getInstance().addContactToDataSet(data);
                    if(ConversationsFragment.getInstance() != null)
                        ConversationsFragment.getInstance().setAuthorIfExists(data);
                    if(data.getExtras()!= null) {
                        getSupportActionBar().setTitle(data.getStringExtra("new_contact_name"));
                        contactMaydayId = data.getStringExtra("new_contact_name");
                        this.invalidateOptionsMenu();
                    }
                    break;

            }

        }
    }

    private void initControls() {

        messagesContainer   = (ListView) findViewById(R.id.messagesContainer);
        etMessage           = (EditText) findViewById(R.id.messageEdit);
        ImageButton btnSend = (ImageButton) findViewById(R.id.chatSendButton);
        isEmergencyMessage  = (CheckBox) findViewById(R.id.checkBox_emergency);
        myToolbar   = (Toolbar) findViewById(R.id.my_toolbar);

        isEmergencyMessage.setOnCheckedChangeListener(this);
        setSupportActionBar(myToolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setPadding(100, 0,  0, 0);
        myToolbar.setSubtitleTextColor(Color.WHITE);
        myToolbar.setSubtitle(contactMaydayId);
        if(redirected){
            getSupportActionBar().setTitle(subject+ "(desviado)");
            myToolbar.setSubtitle(subject);
        }else{
            getSupportActionBar().setTitle(author);
            myToolbar.setSubtitle(contactMaydayId);
        }
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
                    if(redirected){
                        outGoingMessage.setSubject(subject);
                        outGoingMessage.setRedirected(true);
                    }
                    MayDayApplication.getInstance().sendMessage(outGoingMessage, false);
                    db.getWritableDatabase();
                    db.messageAdd(outGoingMessage);
                    db.close();
                    etMessage.setText("");
                    displayMessage(outGoingMessage);
                    ConversationsFragment.getInstance().addOutgoingMessage(outGoingMessage);
                    isEmergencyMessage.setChecked(false);
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
        chatMessage.setDirection(ChatMessageDirection.OUTGOING);
        chatMessage.setType(ChatMessageType.NORMAL);
        chatMessage.setStatus(ChatMessageStatus.SENDING);
        chatMessage.setIsEmergency(this.isEmergencyMessage.isChecked());
    }

    private void loadChatDbHistory() {

        Log.v(log_v, "Loading conversation history... \n");

        try {
            DataBaseHelper db = new DataBaseHelper(this);
            //db.updateRead(contactMaydayId);
            ArrayList<ChatMessage> chatHistory = db.getMessages(contactMaydayId, 0, 5, redirected, subject);
            adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
            messagesContainer.setAdapter(adapter);

            if(chatHistory.isEmpty()) {
                    Log.v(log_v, "Empty Conversation");
            }else{

                for(ChatMessage message : chatHistory) {
                    System.out.println(message.getDirection().toString() + " : " + message.getMessage());
                    displayMessage(message);

                    if(message.getType() == ChatMessageType.SELFDESTRUCTIVE
                            && message.getDirection() == ChatMessageDirection.INCOMING
                            && !message.getExpireTime().equals("")) {
                        message.setTimer();


                    }
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

    public void updateSentIcon(int id){
        if(adapter != null){
            adapter.setMessageToSent(id);
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Mensaje de emergencia")
                    .setMessage("¿Estás seguro que desea activar mensajes de emergencia?\n\n" +
                            "No se recomienda enviar mensajes de emergencia al menos que" +
                            " sea necesario")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //finish();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            buttonView.setChecked(false);
                        }
                    })
                    .show();

            //
        }

    }

    public boolean getIsEmergencyMessage(){
        return isEmergencyMessage.isChecked();
    }

    public String getCurrentSubjectId() {
        return this.subject;
    }
}
