package com.reconizer.loveteller;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Andrzej on 2017-08-06.
 */

public class MainProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.main_profile_fragment, container, false);

        final TextView v_name = (TextView)myFragmentView.findViewById(R.id.textViewProfileName);
        final TextView v_desc = (TextView)myFragmentView.findViewById(R.id.textViewProfileDesc);
        final ImageView iv_gender = (ImageView)myFragmentView.findViewById(R.id.imageViewProfileGender);

        final String uid = Database.GetUserUID();

        if(uid != null)
            /*pobieramy z bazy dane profilu uzytkownika i wpisujemy je na ekran*/
            Database.SetLocation(Database.getUsersDirName()).child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);

                    if(u == null)
                        return;

                    //Przestawilem tutaj name na firstname i lastname poniewaz te parametry pobieraja sie z facebooka // Andrzej
                    if(u.first_name != null && u.last_name != null)
                    {
                        // koncentruje tutaj imie i nazwisko poniewaz android studio podbowiada zeby nie robic tego w setText()
                        String name = ( u.first_name + " " + u.last_name);
                        if(u.age != null)
                            v_name.setText(name + ", " + u.age);
                        else
                            v_name.setText(name);
                    }

                    if(u.description != null)
                        v_desc.setText(u.description);
                    else
                        v_desc.setText("Ustaw opis w panelu opcji aby inni mogli go zobaczyć");

                    if(u.gender!= null) {
                        if (u.gender.equals("male"))
                            iv_gender.setImageResource(R.drawable.ic_male);
                        else if (u.gender.equals("female"))
                            iv_gender.setImageResource(R.drawable.ic_female);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        else //if (uid == null)
            Toast.makeText(myFragmentView.getContext(), "Wystapil blad. Sprobuj ponownie.", Toast.LENGTH_LONG).show();

        return myFragmentView;
    }

    public static MainProfileFragment newInstance(){
        MainProfileFragment f = new MainProfileFragment();
// https://stackoverflow.com/questions/18413309/how-to-implement-a-viewpager-with-different-fragments-layouts
// tutorial z ktorego korzystalem
        return f;
    }
}
