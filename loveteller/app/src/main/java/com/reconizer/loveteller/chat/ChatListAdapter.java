package com.reconizer.loveteller.chat;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reconizer.loveteller.Database;
import com.reconizer.loveteller.R;

import java.util.ArrayList;

/**
 * Created by Damian on 15.08.2017.
 */

class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private ArrayList<Conversation> conversations;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Pos", String.valueOf(holder.getAdapterPosition()));
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("messageID", conversations.get(holder.getAdapterPosition()).getMessagesID());
                Log.e("messageID", conversations.get(holder.getAdapterPosition()).getMessagesID());
                intent.putExtra("myIdPosition", conversations.get(holder.getAdapterPosition()).getUsersID().indexOf(Database.getUserUID()));
                Log.e("myIdPosition", String.valueOf(conversations.get(holder.getAdapterPosition()).getUsersID().indexOf(Database.getUserUID())));
                v.getContext().startActivity(intent);
            }
        });
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
