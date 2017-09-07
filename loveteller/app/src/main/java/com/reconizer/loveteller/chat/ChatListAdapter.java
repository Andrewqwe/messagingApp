package com.reconizer.loveteller.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.reconizer.loveteller.Database;
import com.reconizer.loveteller.R;
import com.reconizer.loveteller.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Damian on 15.08.2017.
 */

class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private ArrayList<Conversation> conversationArrayList;
    private Context context;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.chat_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ValueEventListener userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Picasso.with(context).load(user.photo).into(holder.photo);
                holder.name.setText(user.first_name + " " + user.last_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "ChatListAdapter: get user info error");
            }
        };

        ArrayList<String> userIdArrayList = conversationArrayList.get(position).getUsersID();
        for (String userID : userIdArrayList) {
            if (!userID.equals(Database.getUserUID())) {
                Database.setLocation(Database.getUsersDirName()).child(userID).addListenerForSingleValueEvent(userValueEventListener);
            }
        }

        ChildEventListener messageChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                assert message != null;
                holder.message.setText(message.getMessage());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERROR", "ChatListAdapter: get last message error");
            }
        };

        Database.setLocation(Database.getMessageDir()).child(conversationArrayList.get(holder.getAdapterPosition()).getMessagesID()).child("messages").limitToLast(1).addChildEventListener(messageChildEventListener);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Pos", String.valueOf(holder.getAdapterPosition()));
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("messageID", conversationArrayList.get(holder.getAdapterPosition()).getMessagesID());
                Log.e("messageID", conversationArrayList.get(holder.getAdapterPosition()).getMessagesID());
                intent.putExtra("myIdPosition", conversationArrayList.get(holder.getAdapterPosition()).getUsersID().indexOf(Database.getUserUID()));
                Log.e("myIdPosition", String.valueOf(conversationArrayList.get(holder.getAdapterPosition()).getUsersID().indexOf(Database.getUserUID())));
                v.getContext().startActivity(intent);
            }
        });

        //TODO Pobieranie informacji z profilu
    }

    @Override
    public int getItemCount() {
        return conversationArrayList.size();
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

    ChatListAdapter(ArrayList<Conversation> conversationArrayList) {
        this.conversationArrayList = conversationArrayList;
    }
}
