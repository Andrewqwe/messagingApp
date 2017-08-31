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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.reconizer.loveteller.R;

import java.util.ArrayList;

/**
 * Created by Andrzej on 2017-08-06.
 */

public class MainChatFragment extends Fragment {
    private RecyclerView chatView;
    private ChatListAdapter chatListAdapter;
    private ArrayList<Conversation> conversations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.main_chat_fragment, container, false);
        conversations.clear();
        chatListAdapter = new ChatListAdapter(conversations);

        chatView = (RecyclerView)myFragmentView.findViewById(R.id.chatView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        chatView.setLayoutManager(mLayoutManager);
        chatView.setItemAnimator(new DefaultItemAnimator());
        chatView.setAdapter(chatListAdapter);

        conversations.add(new Conversation(null, null));
        conversations.add(new Conversation(null, null));

        return myFragmentView;
    }

    public static MainChatFragment newInstance(){
        MainChatFragment f = new MainChatFragment();
// https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// tutorial z ktorego korzystalem
        return f;
    }
}
