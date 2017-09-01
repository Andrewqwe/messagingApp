package com.reconizer.loveteller.match;

import android.content.Context;
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
import com.reconizer.loveteller.chat.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
                    matchListRow.setBackgroundColor(v.getResources().getColor(R.color.yes_match));
                    for(MatchesList mlist1 : myMatchesList) {
                        if(mlist1.mid.equals(Database.getUserUID())) {
                            if(mlist1.list == null) mlist1.list = mid + " ";
                            else if(!mlist1.list.contains(mid)) mlist1.list += mid + " ";

                            for(MatchesList mlist2 : myMatchesList) {
                                if (mlist2.mid.equals(mid)) {
                                    if (mlist2.list != null)
                                    if (mlist2.list.contains(Database.getUserUID())) {
                                        ArrayList<String> userUID = new ArrayList<>();
                                        ArrayList<Message> messages = new ArrayList<>();
                                        userUID.add(mlist1.mid);
                                        userUID.add(mlist2.mid);
                                        Conversation conversation = new Conversation(userUID, messages);
                                        Database.sendConversationToDatabase(conversation, mlist2.mid);
                                    }
                                }
                            }
                            Database.sendMatchToDatabase(mlist1);
                        }
                    }
                }
            });
            ImageButton noButton = (ImageButton) view.findViewById(R.id.noButton); //Odrzucenie usera
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchListRow.setBackgroundColor(v.getResources().getColor(R.color.no_match));
                    for(MatchesList mlist : myMatchesList) {
                        if(mlist.mid.equals(Database.getUserUID())) {
                            if(mlist.list != null) {
                                mlist.list = mlist.list.replaceAll(mid + " ", "");
                                Database.sendMatchToDatabase(mlist);
                            }
                        }
                    }
                }
            });
        }
    }

    public MatchListAdapter(ArrayList<User> matches) {
        this.matches = matches;
    }
}
