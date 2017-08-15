package com.reconizer.loveteller.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reconizer.loveteller.R;

import java.util.ArrayList;

/**
 * Created by Damian on 15.08.2017.
 */

class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private ArrayList<Conversation> conversations;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Conversation conversation = conversations.get(position);

        //TODO Pobieranie informacji z profilu
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView message;
        private ImageView photo;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            photo = (ImageView) view.findViewById(R.id.photo);
        }
    }

    public ChatListAdapter(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }


}
