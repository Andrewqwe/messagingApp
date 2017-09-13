package com.reconizer.loveteller.match;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.reconizer.loveteller.Database;
import com.reconizer.loveteller.R;
import com.reconizer.loveteller.User;
import com.reconizer.loveteller.chat.Conversation;
import com.reconizer.loveteller.chat.Messages;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dawid on 2017-08-23.
 */

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MyViewHolder> {
    private ArrayList<User> matches;
    private Context context;
    private ChildEventListener mChildEventListener;
    private ArrayList<MatchesList> myMatchesList = new ArrayList<>();

    @Override
    public MatchListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.match_list_row, parent, false);

        return new MatchListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        myMatchesList.clear();
        //Listener do pobierania matchy userów
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                MatchesList ml = dataSnapshot.getValue(MatchesList.class);
                ml.mid = dataSnapshot.getKey();
                myMatchesList.add(ml);
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                MatchesList ml = dataSnapshot.getValue(MatchesList.class);
                for(int i = 0; i < myMatchesList.size(); i++)
                {
                    if(myMatchesList.get(i).mid.equals(ml.mid))
                    {
                        myMatchesList.remove(i);
                        myMatchesList.add(ml);
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.setLocation(Database.getMatchDir()).addChildEventListener(mChildEventListener);
    }

    @Override
    public void onBindViewHolder(MatchListAdapter.MyViewHolder holder, int position) {

        User u = matches.get(position);
        Picasso.with(context).load(u.photo).into(holder.photo);
        holder.name.setText(u.first_name);
        holder.description.setText(u.description);
        holder.mid = u.uid;
        for(MatchesList mcolor : myMatchesList){
            if(mcolor.mid.equals(Database.getUserUID())){
                if(mcolor.listmatch != null) {
                    if (mcolor.listmatch.contains(holder.mid)) {
                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.matchListRow.getLayoutParams();
                        holder.matchListRow.setVisibility(View.GONE);
                        param.height = 0;
                        param.width = 0;
                    }
                }
                if(mcolor.listyes != null) {
                    if (mcolor.listyes.contains(holder.mid))
                        holder.matchListRow.setBackgroundColor(context.getResources().getColor(R.color.yes_match));
                }
                if (mcolor.listno != null) {
                    if (mcolor.listno.contains(holder.mid))
                        holder.matchListRow.setBackgroundColor(context.getResources().getColor(R.color.no_match));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView name;
        private TextView description;
        private String mid;
        private RelativeLayout matchListRow;
        private boolean brak;
        MyViewHolder(View view) {
            super(view);
            matchListRow = (RelativeLayout) view.findViewById(R.id.matchListRow);
            photo = (ImageView) view.findViewById(R.id.photo);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
            ImageButton yesButton = (ImageButton) view.findViewById(R.id.yesButton); //Zaakceptowanie usera
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchListRow.setBackgroundColor(context.getResources().getColor(R.color.yes_match));
                    brak = true;
                    for(MatchesList mlist1 : myMatchesList) {
                        if(mlist1.mid.equals(Database.getUserUID())) {
                            brak = false;
                            if(mlist1.listno != null) {
                                mlist1.listno = mlist1.listno.replaceAll(mid + " ", "");
                            }
                            if(mlist1.listyes == null) mlist1.listyes = mid + " ";
                            else if(!mlist1.listyes.contains(mid)) mlist1.listyes += mid + " ";

                            for(MatchesList mlist2 : myMatchesList) {
                                if (mlist2.mid.equals(mid)) {
                                    if (mlist2.listyes != null)
                                    if (mlist2.listyes.contains(Database.getUserUID())) {
                                        ArrayList<String> userID = new ArrayList<>();
                                        userID.add(mlist1.mid);
                                        userID.add(mlist2.mid);
                                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                                        Date date = new Date();
                                        Messages messages = new Messages(date);
                                        Conversation conversation = new Conversation(userID);
                                        Database.sendConversationToDatabase(conversation, mlist2.mid, messages);
                                        if(mlist1.listmatch == null) {
                                            mlist1.listmatch = mid + " ";
                                            mlist1.listyes = mlist1.listyes.replaceAll(mid + " ", "");
                                        }
                                        else if(!mlist1.listmatch.contains(mid)) {
                                            mlist1.listmatch += mid + " ";
                                            mlist1.listyes = mlist1.listyes.replaceAll(mid + " ", "");
                                        }
                                        if(mlist2.listmatch == null) {
                                            mlist2.listmatch = mlist1.mid + " ";
                                            mlist2.listyes = mlist2.listyes.replaceAll(mlist1.mid + " ", "");
                                        }
                                        else if(!mlist2.listmatch.contains(mlist1.mid)) {
                                            mlist2.listmatch += mlist1.mid + " ";
                                            mlist2.listyes = mlist2.listyes.replaceAll(mlist1.mid + " ", "");
                                        }
                                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)matchListRow.getLayoutParams();
                                        matchListRow.setVisibility(View.GONE);
                                        param.height = 0;
                                        param.width = 0;
                                    }
                                    Database.sendMatchToDatabase(mlist2);
                                }
                            }
                            Database.sendMatchToDatabase(mlist1);
                            brak = true;
                        }
                    }
                    if(brak == true){
                        MatchesList mlist = new MatchesList(Database.getUserUID(), null, null, null);
                        mlist.listyes = mid + " ";
                        for(MatchesList mlist2 : myMatchesList) {
                            if (mlist2.mid.equals(mid)) {
                                if (mlist2.listyes != null)
                                    if (mlist2.listyes.contains(Database.getUserUID())) {
                                        ArrayList<String> userID = new ArrayList<>();
                                        userID.add(mlist.mid);
                                        userID.add(mlist2.mid);
                                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
                                        Date date = new Date();
                                        String time = dateFormat.format(date);
                                        Messages messages = new Messages(time);
                                        Conversation conversation = new Conversation(userID);
                                        Database.sendConversationToDatabase(conversation, mlist2.mid, messages);
                                        if(mlist.listmatch == null) {
                                            mlist.listmatch = mid + " ";
                                            mlist.listyes = mlist.listyes.replaceAll(mid + " ", "");
                                        }
                                        else if(!mlist.listmatch.contains(mid)) {
                                            mlist.listmatch += mid + " ";
                                            mlist.listyes = mlist.listyes.replaceAll(mid + " ", "");
                                        }
                                        if(mlist2.listmatch == null) {
                                            mlist2.listmatch = mlist.mid + " ";
                                            mlist2.listyes = mlist2.listyes.replaceAll(mlist.mid + " ", "");
                                        }
                                        else if(!mlist2.listmatch.contains(mlist.mid)) {
                                            mlist2.listmatch += mlist.mid + " ";
                                            mlist2.listyes = mlist2.listyes.replaceAll(mlist.mid + " ", "");
                                        }
                                        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)matchListRow.getLayoutParams();
                                        matchListRow.setVisibility(View.GONE);
                                        param.height = 0;
                                        param.width = 0;
                                    }
                                Database.sendMatchToDatabase(mlist2);
                            }
                        }
                        Database.sendMatchToDatabase(mlist);
                    }
                }
            });
            ImageButton noButton = (ImageButton) view.findViewById(R.id.noButton); //Odrzucenie usera
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchListRow.setBackgroundColor(context.getResources().getColor(R.color.no_match));
                    brak = true;
                    for(MatchesList mlist : myMatchesList) {
                        if(mlist.mid.equals(Database.getUserUID())) {
                            brak = false;
                            if(mlist.listyes != null) {
                                mlist.listyes = mlist.listyes.replaceAll(mid + " ", "");
                            }
                            if(mlist.listno == null) mlist.listno = mid + " ";
                            else if(!mlist.listno.contains(mid)) mlist.listno += mid + " ";
                            Database.sendMatchToDatabase(mlist);
                        }
                    }
                    if(brak == true){
                        MatchesList mlist = new MatchesList(Database.getUserUID(), null, null, null);
                        mlist.listno = mid + " ";
                        Database.sendMatchToDatabase(mlist);
                    }
                }
            });
        }
    }

    public MatchListAdapter(ArrayList<User> matches) {
        this.matches = matches;
    }
}
