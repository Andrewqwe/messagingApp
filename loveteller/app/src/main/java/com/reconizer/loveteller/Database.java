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
import com.reconizer.loveteller.match.MatchesList;

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
    static final private String message_dir = "message";
    static final private String match_dir = "match";

    /*funkcje do zwracania nazw katalogow w bazie danych*/
    public static String getUsersDirName() {
        return users_dir;
    }

    public static String getLocation_dir() {
        return location_dir;
    }

    public static String getMessage_dir() {
        return message_dir;
    }

    public static String getMatch_dir() {
        return match_dir;
    }

    /*funkcje do zwracania sciezek do katalogow uzytkownikow w bazie danych*/
    public static String getProfilePath() {
        return users_dir + "/" + getUserUID();
    }

    public static String getLocationPath() {
        return location_dir + "/" + getUserUID();
    }

    public static String getMessagePath() {
        return message_dir + "/" + getUserUID();
    }

    public static String getMatchPath() {
        return match_dir + "/" + getUserUID();
    }

    public static void initialize(boolean persistence) {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
    }

    static public DatabaseReference setLocation(String path) {
        initialize(true);
        mDatabaseReference = mDatabase.getReference().child(path);
        return mDatabaseReference;
    }

    /**
     * Metoda prywatna pobierająca z bazy danych dane o zalogowanym użytkowniku
     * NIE UZYWAJCIE JUZ PLZZ
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

    static public void sendLocationToDatabase(final Coordinates coordinates) {
        initialize(true);
        DatabaseReference users = setLocation(location_dir);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) { //może być problem przy 2 logowaniu.
                    // run some code
                    mDatabaseReference.child(getUserUID()).setValue(coordinates); //tymczasowo
                } else {
                    mDatabaseReference.child(getUserUID()).setValue(coordinates);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    public static void getFacebookData() {
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
                                    object.getString("first_name"),
                                    object.getString("last_name"),
                                    object.getString("email"),
                                    object.getString("gender"),
                                    //getUserImage().toString(),// Firebase Ui login photo - miniaturka
                                    (ImageRequest.getProfilePictureUri(object.getString("id"), 500, 500)).toString(), //Zdjęcie z profilu Facebook
                                    object.getString("id")
                            );
                            Log.e("scoia UID", " UID= "+getUserUID());
                            Log.e("scoia facebook", " Token=" + AccessToken.getCurrentAccessToken() + " FirstName=" + object.getString("first_name") + " Email=" + object.getString("email") + " photoUrl=" + (ImageRequest.getProfilePictureUri(object.optString("id"), 500, 500)).toString());
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

    static public void sendMatchToDatabase(final MatchesList matchesList) {
        initialize(true);
        DatabaseReference users = setLocation(match_dir);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) {
                    mDatabaseReference.child(getUserUID()).setValue(matchesList);
                } else {
                    mDatabaseReference.child(getUserUID()).setValue(matchesList);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }




}