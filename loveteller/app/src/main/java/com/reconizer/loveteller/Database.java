package com.reconizer.loveteller;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.ImageRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Radosław on 2017-08-16.
 */

public class Database {

    //Firebase instance variables
    static private FirebaseDatabase mDatabase;
    static private DatabaseReference mDatabaseReference;

    /*nazwy folderow z danymi w bazie*/
    static final private String users_dir = "users";
    static final private String location_dir = "location";

    /*funkcje do zwracania nazw katalogow w bazie danych*/
    public static String getUsersDirName() {
        return users_dir;
    }

    public static String getLocation_dir() {
        return location_dir;
    }

    /*funkcje do zwracania sciezek do katalogow uzytkownikow w bazie danych*/
    public static String getProfilePath() {
        return users_dir + "/" + getUserUID();
    }

    public static String getLocationPath() {
        return location_dir + "/" + getUserUID();
    }

    public static void initialize(boolean persistence) {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
    }

    static public DatabaseReference setLocation(String path) {
        mDatabaseReference = mDatabase.getReference().child(path);
        return mDatabaseReference;
    }

    /**
     * Metoda prywatna pobierająca z bazy danych dane o zalogowanym użytkowniku
     *
     * @return Zwraca tabele stringów gdzie kolejno jest nazwa użytkownika,e-mail,UID lub pustą tabelę gdy użytkownik nie jest zalogowany
     */
    static public String[] getUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            String[] details = {name, email, uid};
            return details;
        }
        return null;
    }

    /**
     * Metoda pobierająca avatar(zdjęcie użytkownika) z ustawionego z pola logowania (np avatar google)
     *
     * @return Zwraca Url do zdjęcia jeżeli użytkownik jest zalogowany
     */
    static public Uri getUserImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) return user.getPhotoUrl();
        return null;
    }

    /**
     * Metoda publiczna pobierająca z bazy UID użytkownika
     *
     * @return String UID lub null gdy użytkownik nie jest zalogowany
     */
    static public String getUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) return user.getUid();
        return null;
    }

    /**
     * Metoda publiczna tworząca profil użytkownika bazujący na UID. Funkcja pobiera imie(nazwe uzytkownika) i adres e-mail z danych podanych przy rejestracji(logowaniu)
     * Funkcja nadpisuje profil użytkownika tz nie wypełnienie wszystkich danych będzie skutkować wyczyszczeniem niewypełnionych pól.
     * Funkcja tworzy obiekt klasy User w trakcie wykonania (dobrze żeby tak pozostało)
     */
    static public void sendUserInfoToDatabase() {
        initialize(true);
        DatabaseReference users = setLocation(users_dir);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) {
                    // run some code
                } else {
                    String[] details = getUserInfo();
                    com.reconizer.loveteller.User user = new com.reconizer.loveteller.User(details[0], details[1], getUserImage().toString());
                    mDatabaseReference.child(getUserUID()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    static public void sendProfileToDatabase(final User profile) {
        initialize(true);
        DatabaseReference users = setLocation(users_dir);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) { //może być problem przy 2 logowaniu.
                    // run some code
                    mDatabaseReference.child(getUserUID()).setValue(profile); //tymczasowo
                } else {
                    mDatabaseReference.child(getUserUID()).setValue(profile);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public static void facebook() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            com.reconizer.loveteller.User user = new com.reconizer.loveteller.User(
                                    object.getString("first_name").toString(),
                                    object.getString("last_name").toString(),
                                    object.getString("description").toString(),
                                    object.getString("email").toString(),
                                    object.getString("gender").toString(),
                                    object.getString("age_range").toString(),
                                    //getUserImage().toString(),// Firebase Ui login photo - miniaturka
                                    (ImageRequest.getProfilePictureUri(object.getString("id"), 500, 500)).toString(), //Zdjęcie z profilu Facebook
                                    object.getString("id").toString()
                            );
                                    //object.getDouble("latitude"), //DAWID/ szerogość geograficzna
                                    //object.getDouble("longitude")); //DAWID/ długość geograficzna
                            Log.e("scoia UID", " UID= "+getUserUID());

                            Log.e("scoia facebook", " Token=" + AccessToken.getCurrentAccessToken() + " FirstName=" + object.getString("first_name").toString() + " Email=" + object.getString("email").toString() + " photoUrl=" + (ImageRequest.getProfilePictureUri(object.optString("id"), 500, 500)).toString());
                            //Database.initialize(false); //tymczasowo
                           // mDatabaseReference.child(getUserUID()).setValue(user); //niebezpieczna metoda
                            sendProfileToDatabase(user); //zabezpieczona przed brakiem połączenia z baza danych
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,gender,age_range,last_name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
}