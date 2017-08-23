package com.reconizer.loveteller.match;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.reconizer.loveteller.Database;
import com.reconizer.loveteller.R;
import com.reconizer.loveteller.User;

import java.util.ArrayList;

/**
 * Created by Andrzej on 2017-08-14.
 */


public class MainMatchFragment extends Fragment {
    private RecyclerView matchesListView;
    private LocationManager lm;
    private LocationListener ls;
    private Location start, end;
    private MatchListAdapter adapter;
    private ArrayList<User> matchesList = new ArrayList<>();
    private ArrayList<User> usersList = new ArrayList<>();
    private ChildEventListener lChildEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.main_match_fragment, container, false);

        return myFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        usersList.clear();
        start = new Location("startLocation");
        end = new Location("endLocation");
        Database.Initialize(true);
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        ls = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                //UpdataUserLocationInDatabase(latitude, longitude);

                start.setLatitude(location.getLatitude());
                start.setLongitude(location.getLongitude());
                filter();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ls);

        lChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                User user = dataSnapshot.getValue(User.class);
                //user.setUid(dataSnapshot.getKey());
                usersList.add(user);
                filter();
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                for(int i = 0; i < usersList.size(); i++)
                {
                    if(usersList.get(i).email.equals(user.email))
                    {
                        usersList.remove(i);
                        usersList.add(user);
                        filter();
                        break;
                    }
                }
        }
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        //Przechodzimy do userów w bazie i ustawiamy utworzonego wcześniej listenera
        Database.SetLocation(Database.getUsersDirName()).addChildEventListener(lChildEventListener);
        //Tworzymy adapter i przypisujemy go do listview żeby wyswietlac userów

        adapter = new MatchListAdapter(matchesList);
        matchesListView = (RecyclerView)getActivity().findViewById(R.id.matchListView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        matchesListView.setLayoutManager(mLayoutManager);
        matchesListView.setItemAnimator(new DefaultItemAnimator());
        matchesListView.setAdapter(adapter);
    }


    public static MainMatchFragment newInstance(){
        MainMatchFragment f = new MainMatchFragment();
// https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// tutorial z ktorego korzystalem
        return f;
    }

    public void filter() {
        //Czyścimy listę pomocniczą
        matchesList.clear();
        //Wyświetlamy userów odległych o max 200 metrów
        for (User u : usersList) {
                end.setLatitude(u.latitude);
                end.setLongitude(u.longitude);
                if (200 >= start.distanceTo(end)) {
                    matchesList.add(u);
                }
        }
        adapter.notifyDataSetChanged();
    }
}

