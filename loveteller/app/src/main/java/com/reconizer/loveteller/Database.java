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
import com.reconizer.loveteller.chat.Conversation;
import com.reconizer.loveteller.match.MatchesList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Radosław on 2017-08-16.
 */

public class Database {

    //Firebase instance variables
    static private FirebaseDatabase mDatabase;
    //static private DatabaseReference mDatabaseReference;

    /*nazwy folderow z danymi w bazie*/
    static final private String USERS_DIR = "users";
    static final private String LOCATION_DIR = "location";
    static final private String MESSAGE_DIR = "message";
    static final private String CONVERSATION_DIR = "conversation";
    static final private String MATCH_DIR = "match";

    /*funkcje do zwracania nazw katalogow w bazie danych*/
    public static String getUsersDirName() {
        return USERS_DIR;
    }

    public static String getLocationDir() {
        return LOCATION_DIR;
    }

    public static String getMessageDir() {
        return MESSAGE_DIR;
    }

    public static String getConversationDir() {
        return CONVERSATION_DIR;
    }

    public static String getMatchDir() {
        return MATCH_DIR;
    }

    /*funkcje do zwracania sciezek do katalogow uzytkownikow w bazie danych*/
    public static String getProfilePath() {
        return USERS_DIR + "/" + getUserUID();
    }

    public static String getLocationPath() {
        return LOCATION_DIR + "/" + getUserUID();
    }

    public static String getMessagePath() {
        return MESSAGE_DIR + "/" + getUserUID();
    }

    public static String getConversationPath() {
        return CONVERSATION_DIR + "/" + getUserUID();
    }

    public static String getMatchPath() {
        return MATCH_DIR + "/" + getUserUID();
    }

    public static void initialize(boolean persistence) {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
    }

    static public DatabaseReference setLocation(String path) {
        initialize(true);
        DatabaseReference mDatabaseReference = mDatabase.getReference().child(path);
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
        final DatabaseReference users = setLocation(USERS_DIR);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) { //może być problem przy 2 logowaniu.
                    // run some code
                    users.child(getUserUID()).setValue(profile); //tymczasowo
                } else {
                    users.child(getUserUID()).setValue(profile);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    static public void sendLocationToDatabase(final Coordinates coordinates) {
        initialize(true);
        final DatabaseReference location = setLocation(LOCATION_DIR);
        location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) { //może być problem przy 2 logowaniu.
                    // run some code
                    location.child(getUserUID()).setValue(coordinates); //tymczasowo
                } else {
                    location.child(getUserUID()).setValue(coordinates);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    static public void sendConversationToDatabase(final Conversation conversation, final String UID) {
        initialize(true);
        final DatabaseReference conversations = setLocation(CONVERSATION_DIR);
        conversations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) { //może być problem przy 2 logowaniu.
                    // run some code
                    conversations.child(getUserUID()).push().setValue(conversation); //tymczasowo
                } else {
                    conversations.child(getUserUID()).push().setValue(conversation);
                }
                if (snapshot.child(UID).exists()) { //może być problem przy 2 logowaniu.
                    // run some code
                    conversations.child(UID).push().setValue(conversation); //tymczasowo
                } else {
                    conversations.child(UID).push().setValue(conversation);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    static public void sendMatchToDatabase(final MatchesList matchesList) {
        initialize(true);
        final DatabaseReference match = setLocation(MATCH_DIR);
        match.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(getUserUID()).exists()) {
                    match.child(getUserUID()).setValue(matchesList);
                } else {
                    match.child(getUserUID()).setValue(matchesList);
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


}