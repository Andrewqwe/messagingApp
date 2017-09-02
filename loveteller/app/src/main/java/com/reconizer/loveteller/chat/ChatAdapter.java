package com.reconizer.loveteller.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reconizer.loveteller.R;

import java.util.List;

/**
 * Created by Andrzej on 2017-08-15.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList;
    private int myNumber;

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public ChatAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == myNumber) {
            itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_chatmessage_mine, parent, false);
        } else {
            itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_chatmessage_other, parent, false);
        }
        return new ChatViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return messageList.get(position).getOwner(); //tutaj wybieramy czyja to wiadomosc
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ChatViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size());
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.MessageTextView);
        }
    }
}
