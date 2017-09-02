package com.reconizer.loveteller.chat;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.reconizer.loveteller.Database;
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
    private int myNumber;
    private String messageID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //dane od Damiana
        messageID = getIntent().getStringExtra("messageID");
        myNumber = getIntent().getIntExtra("myIdPosition", -1);         //-1 oznacza błąd

        messageList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);

        //===
        Database.initialize(true);
        DatabaseReference ref = Database.setLocation(Database.getMessageDir()).child(messageID).child("messages");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<Message>> t = new GenericTypeIndicator<List<Message>>() {
                };
                List messages = dataSnapshot.getValue(t);
                if (messages == null) {
                    Log.println(Log.ERROR, "Andrzej", "Brak wiadomosci do pobrania z bazy");
                } else {
                    messageList.clear();
                    for (Object m : messages) {
                        messageList.add((Message) m);
                    }
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //===

        chatAdapter = new ChatAdapter(messageList);
        chatAdapter.setMyNumber(myNumber); //wiadomosci z moim numerkiem beda po prawo, reszta po lewo

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);

        //============== OBSZAR EKSPERYMENTOW =======================


        final TextView messageBox = (TextView) findViewById(R.id.chatTextWritingBox);   //Pole tekstowe z wiadomoscia
        ImageButton sendButton = (ImageButton) findViewById(R.id.chatMessageSendButton); //guzik do wysylania wiadomosci
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageBox.length() > 0) {
                    if (messageBox.getText().toString().equals("0")) {
                        myNumber = 0;
                    } else if (messageBox.getText().toString().equals("1")) {
                        myNumber = 1;
                    } else {
                        Message m = new Message(myNumber, messageBox.getText().toString()); //tutaj numer zostaje zapisany w wiadomosci
                        chatAdapter.addMessage(m);
                        Database.setLocation(Database.getMessageDir()).child(messageID).child("messages").setValue(messageList);
                    }
                    messageBox.setText("");
                }
            }
        });
        //===========================================================
        //showExampleMessages();
    }

    private void showExampleMessages() {
        Message message = new Message(1, "Czesc");
        messageList.add(message);

        message = new Message(0, "No hej :)");
        messageList.add(message);

        message = new Message(1, "Co slychac ?");
        messageList.add(message);

        message = new Message(0, "Wszystko w porzadku a u ciebie ?");
        messageList.add(message);

        message = new Message(1, "Nareszcie działa to dynamiczne skalowanie dymków. Przydał by się jakiś guzik do wysyłania tej wiadomosci" +
                " i powoli mozna zaczac uzywac tego czatu");
        messageList.add(message);

        message = new Message(0, "Widze ze dobrze ci idzie, ciekawe jak radzi sobie reszta");
        messageList.add(message);

        message = new Message(1, "To fajna ekipa, na pewno mnie nie zawiodą ! :)");
        messageList.add(message);

        chatAdapter.notifyDataSetChanged();
    }
}
