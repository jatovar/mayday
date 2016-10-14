package com.spadigital.mayday.app.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.spadigital.mayday.app.Activities.ChatActivity;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.Adapters.ConversationItemAdapter;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.R;

import java.util.ArrayList;

/**
 * Created by jorge on 23/09/16.
 */
public class ConversationsFragment extends Fragment {

    private SwipeRefreshLayout swpMessages;
    private ListView lvMessages;
    private ArrayList<ChatMessage> messageArrayList;
    private View view;
    private ConversationItemAdapter conversationItemAdapter;
    private Activity parentActivity;
    private static ConversationsFragment instance;

    public static ConversationsFragment getInstance(){
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        instance        = this;
        view            = inflater.inflate(R.layout.fragment_all_chats, container, false);
        parentActivity  = getActivity();

        loadConversations();
        initControls();

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        if(conversationItemAdapter != null)
            conversationItemAdapter.notifyDataSetChanged();
    }
    private void initControls() {

        lvMessages              = (ListView) view.findViewById(R.id.lvMessages);
        conversationItemAdapter = new ConversationItemAdapter(ConversationsFragment.this,
                new ArrayList<ChatMessage>());

        conversationItemAdapter.add(this.messageArrayList);
        lvMessages.setAdapter(conversationItemAdapter);
        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if the contact is not bloked
                if(view.isEnabled())
                {
                    ChatMessage chatMessage = conversationItemAdapter.getItem(position);
                    chatMessage.setStatus(ChatMessageStatus.READ);
                    Intent intent           = new Intent(parentActivity, ChatActivity.class);
                    intent.putExtra("contact_MayDayID", chatMessage.getContactMayDayID());
                    intent.putExtra("contact_author", chatMessage.getAuthor());
                    startActivity(intent);
                }else{


                    Toast toast = Toast.makeText(ConversationsFragment.this.getContext(),
                            "Contacto bloqueado, para desbloquear ve al menu configuración",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        swpMessages = (SwipeRefreshLayout) view.findViewById(R.id.swpMessages);
        swpMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                refreshListView();
            }
        });
    }

    private void loadConversations() {
        DataBaseHelper db;
        db               = new DataBaseHelper(getContext());
        messageArrayList = db.getConversationsLastestMessage();
        db.close();
    }

    private void refreshListView(){

        swpMessages.setRefreshing(false);
        loadConversations();

        lvMessages              = (ListView) view.findViewById(R.id.lvMessages);
        conversationItemAdapter = new ConversationItemAdapter(ConversationsFragment.this, new ArrayList<ChatMessage>());

        conversationItemAdapter.add(this.messageArrayList);
        lvMessages.setAdapter(conversationItemAdapter);
    }

    /**There is is no need to notify changes because onResume method will do that,
     *  and the user will never be in this fragment when composing a message**/
    public void addOutgoingMessage(ChatMessage message){
        conversationItemAdapter.replaceMessageAndSetFirst(message);

    }
    /**The incoming messages are displayed intermediately because the user
     * can be in conversations fragment **/
    public void addIncomingMessage(ChatMessage message){
        conversationItemAdapter.replaceMessageAndSetFirst(message);
        conversationItemAdapter.notifyDataSetChanged();
    }
    /**Set **/
    public void setContactInfoIfExists(Intent data) {

        Contact contact = new Contact(
                data.getStringExtra("modified_contact_id"),
                data.getStringExtra("modified_contact_name"),
                data.getStringExtra("modified_contact_MayDay_ID"),
                ContactStatus.valueOf(data.getStringExtra("modified_contact_status"))
        );
        conversationItemAdapter.updateContactInfo(contact);
    }

    public void setToUnknownContactIfExists(Intent data) {

        String toRemoveMayDayId = data.getStringExtra("removed_contact_maydayid");
        conversationItemAdapter.setAuthorToUnknown(toRemoveMayDayId);
    }

    public void setAuthorIfExists(Intent data){

        Contact contact = new Contact(
                data.getStringExtra("new_contact_id"),
                data.getStringExtra("new_contact_name"),
                data.getStringExtra("new_contact_MaydayId"),
                ContactStatus.NORMAL);
        conversationItemAdapter.updateContactInfo(contact);
    }
}
