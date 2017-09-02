package com.reconizer.loveteller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        final TextView tv_name = (TextView) findViewById(R.id.textViewEditProfileName);
        final EditText et_desc = (EditText) findViewById(R.id.editTextEditProfileDescription);
        final ImageView iv_gender = (ImageView) findViewById(R.id.imageViewEditProfileGender);
        final SeekBar sb_radius = (SeekBar) findViewById(R.id.seekBarEditProfileRadius);
        final TextView tv_radius = (TextView) findViewById(R.id.textViewEditProfileRadius);

        final String uid = Database.getUserUID();

        if (uid != null) {
            /*pobieramy z bazy dane profilu uzytkownika i wpisujemy je na ekran*/
//            Database.SetLocation(Database.getUsersDirName()).child(uid).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    User u = dataSnapshot.getValue(User.class);
//
//                    if (u == null)
//                        return;
//
//                    //Przestawilem tutaj name na firstname i lastname poniewaz te parametry pobieraja sie z facebooka // Andrzej
//                    if (u.first_name != null && u.last_name != null) {
//                        // koncentruje tutaj imie i nazwisko poniewaz android studio podbowiada zeby nie robic tego w setText()
//                        String name = (u.first_name + " " + u.last_name);
//                        tv_name.setText(name);
//                    }
//
//                    if (u.description != null)
//                        et_desc.setText(u.description);
//                    else
//                        et_desc.setText("Ustaw opis w panelu opcji aby inni mogli go zobaczyć");
//
//                    if (u.gender != null) {
//                        if (u.gender.equals("male"))
//                            iv_gender.setImageResource(R.drawable.ic_male);
//                        else if (u.gender.equals("female"))
//                            iv_gender.setImageResource(R.drawable.ic_female);
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

            /*przy uruchomieniu aktywnosci czytamy promien z bazy i ustawiamy seek bar na taka wartosc*/
            Database.setLocation(Database.getUsersDirName()).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);

                    if (u == null)
                        return;

                    //Przestawilem tutaj name na firstname i lastname poniewaz te parametry pobieraja sie z facebooka // Andrzej
                    if (u.first_name != null && u.last_name != null) {
                        // koncentruje tutaj imie i nazwisko poniewaz android studio podbowiada zeby nie robic tego w setText()
                        String name = (u.first_name + " " + u.last_name);
                        tv_name.setText(name);
                    }

                    if (u.description != null)
                        et_desc.setText(u.description);
                    else
                        et_desc.setText("Ustaw opis w panelu opcji aby inni mogli go zobaczyć");

                    if (u.gender != null) {
                        if (u.gender.equals("male"))
                            iv_gender.setImageResource(R.drawable.ic_male);
                        else if (u.gender.equals("female"))
                            iv_gender.setImageResource(R.drawable.ic_female);
                    }

                    int r;

                    if (u.radius != null)
                        r = Integer.valueOf(u.radius);
                    else
                        r = 0;

                    sb_radius.setProgress(r / 50);
                    tv_radius.setText(r + "m");

                    et_desc.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() != 0)
                                Database.setLocation(Database.getUsersDirName()).child(uid).child("description").setValue(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else //if (uid == null)
        {
            Toast.makeText(this, "Wystapil blad. Sprobuj ponownie.", Toast.LENGTH_LONG).show();
            return;
        }

        /*przy zmiane wartosci promienia przy uzyciu seek bara, zmieniamy tekst obok seekbara i zapisujemy
        * nowa wartosc promienia do bazy*/
        sb_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_radius.setText(progress * 50 + "m");

                Database.setLocation(Database.getUsersDirName()).child(uid).child("radius").setValue(String.valueOf(progress * 50));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
