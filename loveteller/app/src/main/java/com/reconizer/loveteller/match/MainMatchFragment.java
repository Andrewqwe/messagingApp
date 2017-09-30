package com.reconizer.loveteller.match;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Priority;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.reconizer.loveteller.Coordinates;
import com.reconizer.loveteller.Database;
import com.reconizer.loveteller.R;
import com.reconizer.loveteller.User;

import java.util.ArrayList;

/**
 * Created by Andrzej on 2017-08-14.
 */


public class MainMatchFragment extends Fragment {
    private RecyclerView matchesListView;
    private TextView emptyView;
    private LocationManager lm;
    private LocationListener ls;
    private Location start, end;
    private MatchListAdapter adapter;
    private ArrayList<User> matchesList = new ArrayList<>();
    private ArrayList<Coordinates> coordinatesList = new ArrayList<>();
    private ArrayList<User> usersList = new ArrayList<>();
    private ChildEventListener lChildEventListener, uChildEventListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int radius;
    private int TIME_DURATION = 20000;
    private int DISTANCE = 0;
    private int WANTED_ACCURACY = 200;
    private boolean gps_enabled,network_enabled;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.main_match_fragment, container, false);

        return myFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//Sprawdzanie permisji do lokalizacji
        matchesListView = (RecyclerView) getActivity().findViewById(R.id.matchListView);
        emptyView = (TextView) getActivity().findViewById(R.id.emptyView);

        coordinatesList.clear();
        usersList.clear();

        start = new Location("startLocation");
        end = new Location("endLocation");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location !=null){
                    if(location.getAccuracy()<WANTED_ACCURACY) {
                        ls.onLocationChanged(location);
                    }
                }
            }
        });
        Database.initialize(true);

        //Pobieranie lokalizacji usera
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        ls = new LocationUpdateHandler();

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15, 1, ls); //Aktualizowanie co jeden metr

        //Listener do pobrania listy lokalizacji z bazy
        lChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Coordinates c = dataSnapshot.getValue(Coordinates.class);
                coordinatesList.add(c);
                filter();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Coordinates c = dataSnapshot.getValue(Coordinates.class);
                for (int i = 0; i < coordinatesList.size(); i++) {
                    if (coordinatesList.get(i).cid.equals(c.cid)) {
                        coordinatesList.remove(i);
                        coordinatesList.add(c);
                        filter();
                        break;
                    }
                }
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };
        Database.setLocation(Database.getLocationDir()).addChildEventListener(lChildEventListener);

        //Listener do pobrania userów z bazy
        uChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                u.uid = dataSnapshot.getKey();
                if (u.uid.equals(Database.getUserUID())) {
                    if (u.radius != null) radius = Integer.parseInt(u.radius);
                    else radius = 0;
                }
                usersList.add(u);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User u = dataSnapshot.getValue(User.class);
                for (int i = 0; i < usersList.size(); i++) {
                    if (usersList.get(i).email.equals(u.email)) {
                        usersList.remove(i);
                        usersList.add(u);
                        break;
                    }
                }
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };
        Database.setLocation(Database.getUsersDirName()).addChildEventListener(uChildEventListener);

        //Wrzucamy do matchListView userów w zasięgu
        adapter = new MatchListAdapter(matchesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        matchesListView.setLayoutManager(mLayoutManager);
        matchesListView.setItemAnimator(new DefaultItemAnimator());
        matchesListView.setAdapter(adapter);
        if(gps_enabled) {
            lm.removeUpdates(ls);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_DURATION, DISTANCE, ls);
        } else if(network_enabled) { // Checking for GSM
            lm.removeUpdates(ls);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_DURATION, DISTANCE, ls);
        }
    }


    public static MainMatchFragment newInstance() {
        MainMatchFragment f = new MainMatchFragment();
        return f;
    }

    //Funkcja szukająca userów w zasięgu
    public void filter() {
        //Czyścimy listę pomocniczą
        matchesList.clear();
        for (Coordinates c : coordinatesList) {
            end.setLatitude(c.latitude);
            end.setLongitude(c.longitude);
            if (radius >= start.distanceTo(end) && c.cid != null) {
                if (!c.cid.equals(Database.getUserUID())) {
                    for (User u : usersList) {
                        if (u.uid != null && c.cid != null)
                            if (u.uid.equals(c.cid))
                                matchesList.add(u);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();

        //Jeśli user ma wyłączoną lokalizację wyświetlamy TextView z informacją o oczekiwaniu
        if (start.getLatitude() == 0 && start.getLongitude() == 0) {
            matchesListView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            matchesListView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    public class LocationUpdateHandler implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location.getAccuracy() < WANTED_ACCURACY) {
                Coordinates c = new Coordinates(Database.getUserUID(), location.getLatitude(), location.getLongitude());
                Database.sendLocationToDatabase(c);

                start.setLatitude(location.getLatitude());
                start.setLongitude(location.getLongitude());
                matchesListView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                filter();
                Toast.makeText(getContext(),"Pobrano lokalizacje - " + location.getProvider(),Toast.LENGTH_SHORT).show();
            } else {
                String errorMessage = "Mala dokladnosc lokalizacji: " + location.getAccuracy() + "m\n\nZrodlo: " + location.getProvider();
                emptyView.setText(errorMessage);
                Log.println(Log.ERROR,"Low Location Accuracy: ",location.toString());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) throws SecurityException {
            lm.removeUpdates(ls);
            if (provider.toLowerCase().equals(LocationManager.GPS_PROVIDER.toLowerCase())) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_DURATION, DISTANCE, ls);
            } else {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_DURATION, DISTANCE, ls);
            }
        }

        @Override
        public void onProviderDisabled(String provider)throws SecurityException{
            lm.removeUpdates(ls);
            if (provider.toLowerCase().equals(LocationManager.GPS_PROVIDER)) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_DURATION, DISTANCE, ls);
            } else {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_DURATION, DISTANCE, ls);
            }
        }

    }

}

