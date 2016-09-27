package com.example.diego.mayday_03;

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
    private MyApplication app;
    private Activity parentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view            = inflater.inflate(R.layout.activity_all_chats, container, false);
        parentActivity  = getActivity();
        app             = (MyApplication) getActivity().getApplication();

        app.setConversationsFragment(this);

        loadConversations();
        initControls();

        return view;
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
                ChatMessage chatMessage = conversationItemAdapter.getItem(position);
                Intent intent           = new Intent(parentActivity, ChatActivity.class);
                intent.putExtra("contact_MayDayID", chatMessage.getContactMayDayID());
                startActivity(intent);
            }
        });

        swpMessages = (SwipeRefreshLayout)view.findViewById(R.id.swpMessages);
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
    public void invalidateChatList(ChatMessage message){
        conversationItemAdapter.replaceMessageAndSetFirst(message);
        conversationItemAdapter.notifyDataSetChanged();
    }
}
