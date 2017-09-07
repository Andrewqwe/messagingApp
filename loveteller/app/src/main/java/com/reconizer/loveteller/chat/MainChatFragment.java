package com.reconizer.loveteller.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.reconizer.loveteller.Database;
import com.reconizer.loveteller.R;

import java.util.ArrayList;

/**
 * Created by Andrzej on 2017-08-06.
 */

public class MainChatFragment extends Fragment {
    private ChatListAdapter chatListAdapter;
    private ArrayList<Conversation> conversationArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.main_chat_fragment, container, false);
        conversationArrayList.clear();
        chatListAdapter = new ChatListAdapter(conversationArrayList);

        RecyclerView chatView = (RecyclerView) myFragmentView.findViewById(R.id.chatView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        chatView.setLayoutManager(mLayoutManager);
        chatView.setItemAnimator(new DefaultItemAnimator());
        chatView.setAdapter(chatListAdapter);

        ChildEventListener conversationsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                conversationArrayList.add(conversation);
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                if (!conversationArrayList.contains(conversation)) {
                    conversationArrayList.remove(conversation);
                    conversationArrayList.add(conversation);
                    chatListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Conversation conversation = dataSnapshot.getValue(Conversation.class);
                if (!conversationArrayList.contains(conversation)) {
                    conversationArrayList.remove(conversation);
                    chatListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        Database.setLocation(Database.getConversationPath()).addChildEventListener(conversationsChildEventListener);

        return myFragmentView;
    }

    public static MainChatFragment newInstance() {
        // https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// tutorial z ktorego korzystalem
        return new MainChatFragment();
    }
}
