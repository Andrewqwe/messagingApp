package com.reconizer.loveteller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by Andrzej on 2017-08-14.
 */


public class MainMatchFragment extends Fragment {
    private TextView tv;
    private ListView list;
    private LocationManager lm;
    private LocationListener ls;
    private double userLatitude;
    private double userLongitude;
    private ArrayAdapter<User> adapter;
    private List<User> matchesList = new ArrayList<>();
    private List<User> usersList = new ArrayList<>();
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
        tv = (TextView) getActivity().findViewById(R.id.location2);
        Database.Initialize(true);
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        ls = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                userLatitude = location.getLatitude(); // szerokosc geograficzna
                userLongitude = location.getLongitude(); // dlugosc geograficzna

                //UpdataUserLocationInDatabase(latitude, longitude);

                String locationText = userLatitude + ", " + userLongitude;
                tv.setText(locationText);
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
        adapter = new ArrayAdapter<User>(getActivity(), android.R.layout.simple_list_item_1, matchesList);
        list = (ListView)getActivity().findViewById(R.id.listMatches);
        list.setAdapter(adapter);

        //Ustawiamy listenera wykrywającego naciśnięcie jednego z elementów listy i wywołującego odpowiednią metodę
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                userOnClick(matchesList.get(pos));
            }
        });
    }


    public static MainMatchFragment newInstance(){
        MainMatchFragment f = new MainMatchFragment();
// https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// tutorial z ktorego korzystalem
        return f;
    }

    private void userOnClick(User user)
    {
        //To co się stanie po kliknięciu na innego usera
    }

    public void filter() {
        //Czyścimy listę pomocniczą
        matchesList.clear();
        //Wyświetlamy userów odległych o max 200 jednostek
        for (User u : usersList) {
            if(200 >= sqrt(pow(u.latitude - userLatitude, 2) + pow(u.longitude - userLongitude, 2))){
                matchesList.add(u);
            }
        }
        adapter.notifyDataSetChanged();
    }
}

