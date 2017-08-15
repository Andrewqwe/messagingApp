package com.reconizer.loveteller.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.reconizer.loveteller.R;

/**
 * Created by Andrzej on 2017-08-06.
 */

public class MainChatFragment extends Fragment {
    private ListView list_view;
    private ArrayAdapter<Conversation> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.main_chat_fragment, container, false);

        return myFragmentView;
    }
    public static MainChatFragment newInstance(){
        MainChatFragment f = new MainChatFragment();
// https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// tutorial z ktorego korzystalem
        return f;
    }
}
