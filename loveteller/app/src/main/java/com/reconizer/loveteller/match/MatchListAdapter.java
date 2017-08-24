package com.reconizer.loveteller.match;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reconizer.loveteller.R;
import com.reconizer.loveteller.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dawid on 2017-08-23.
 */

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MyViewHolder> {
    private ArrayList<User> matches;
    private Context context;

    @Override
    public MatchListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.match_list_row, parent, false);
        return new MatchListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MatchListAdapter.MyViewHolder holder, int position) {
        User u = matches.get(position);
        Picasso.with(context).load(u.photo).into(holder.photo);
        holder.name.setText(u.first_name);
        holder.description.setText("linia1\nlinia2");
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView photo;
        private TextView name;
        private TextView description;
        private RelativeLayout matchListRow;
        MyViewHolder(View view) {
            super(view);
            matchListRow = (RelativeLayout) view.findViewById(R.id.matchListRow);
            photo = (ImageView) view.findViewById(R.id.photo);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
            ImageButton yesButton = (ImageButton) view.findViewById(R.id.yesButton);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchListRow.setBackgroundColor(Color.GREEN);
                    //TODO Sprawdzanie czy druga osoba jest zainteresowana i ewentualne tworzenie im chatu
                }
            });
            ImageButton noButton = (ImageButton) view.findViewById(R.id.noButton);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    matchListRow.setBackgroundColor(Color.RED);
                }
            });
        }
    }

    public MatchListAdapter(ArrayList<User> matches) {
        this.matches = matches;
    }
}
