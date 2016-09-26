package com.example.diego.mayday_03;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private ConversationItemAdapter adapter;
    private MyApplication app;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_all_chats, container, false);

        loadConversations();
        initControls();
        swpMessages = (SwipeRefreshLayout)view.findViewById(R.id.swpMessages);

        //ListView lvMessages = (ListView)view.findViewById(R.id.lvMessages);

        /*listado.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public  void onItemClick(AdapterView<?> parent, View view_aux, int position, long id){
                Intent conver = new Intent(getContext(), com.mcruz.maydayentrega.ConversacionActivity.class);
                startActivity(conver);
            }
        });*/

        swpMessages.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){

                refreshListView();

            }
        });

        //lvMessages.setAdapter(adapter);

        app = (MyApplication) getActivity().getApplication();
        app.setConversationsFragment(this);

        return view;
    }

    private void initControls() {
        lvMessages = (ListView) view.findViewById(R.id.lvMessages);
        adapter    = new ConversationItemAdapter(ConversationsFragment.this, new ArrayList<ChatMessage>());
        adapter.add(this.messageArrayList);
        lvMessages.setAdapter(adapter);
    }

    private void loadConversations() {
        DataBaseHelper db;
        db = new DataBaseHelper(getContext());
        this.messageArrayList = db.getConversationsLastestMessage();
        db.close();
    }

    private void refreshListView(){
        swpMessages.setRefreshing(false);
        loadConversations();
        lvMessages = (ListView) view.findViewById(R.id.lvMessages);
        adapter    = new ConversationItemAdapter(ConversationsFragment.this, new ArrayList<ChatMessage>());
        adapter.add(this.messageArrayList);
        lvMessages.setAdapter(adapter);

    }
    public ConversationItemAdapter getConversationsAdapter(){
        return this.adapter;
    }
}
