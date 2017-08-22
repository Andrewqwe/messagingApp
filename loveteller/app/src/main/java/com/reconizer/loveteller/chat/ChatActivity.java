package com.reconizer.loveteller.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.reconizer.loveteller.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrzej on 2017-08-15.
 */

public class ChatActivity extends AppCompatActivity {
    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        messageList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        chatAdapter = new ChatAdapter(messageList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);

        showExampleMessages();
        }

        private void showExampleMessages()
        {
            Message message = new Message(0,"Czesc");
            messageList.add(message);

            message = new Message(0,"No hej :)");
            messageList.add(message);

            message = new Message(0,"Co slychac ?");
            messageList.add(message);

            message = new Message(0,"Wszystko w porzadku a u ciebie ?");
            messageList.add(message);

            message = new Message(0,"Nareszcie działa to dynamiczne skalowanie dymków. Przydał by się jakiś guzik do wysyłania tej wiadomosci" +
                    " i powoli mozna zaczac uzywac tego czatu");
            messageList.add(message);

            message = new Message(0,"Widze ze dobrze ci idzie, ciekawe jak radzi sobie reszta");
            messageList.add(message);

            message = new Message(0,"To fajna ekipa, na pewno mnie nie zawiodą ! :)");
            messageList.add(message);

            chatAdapter.notifyDataSetChanged();
        }
}
