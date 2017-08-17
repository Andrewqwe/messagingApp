package com.reconizer.loveteller;

import android.net.Uri;

import com.firebase.ui.auth.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Radosław on 2017-08-16.
 */

public class Database {

    //Firebase instance variables
    static private FirebaseDatabase mDatabase;
    static private DatabaseReference mDatabaseReference;

    /*nazwy folderow z danymi w bazie*/
    static final private String users_dir = "users";

    /*funkcje do zwracania nazw katalogow w bazie danych*/
    public static String getUsersDirName() { return users_dir; }

    /*funkcje do zwracania sciezek do katalogow uzytkownikow w bazie danych*/
    //public static String getVisitsPath() { return users_dir + "/" + mCurrentUid + "/" + visits_dir; }


static public void  test(){
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("message");

    myRef.setValue("Hello, World!");
}

    public static void Initialize(boolean persistence) {
        if (mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
    }
    static public DatabaseReference SetLocation(String path) {
        mDatabaseReference = mDatabase.getReference().child(path);
        return mDatabaseReference;
    }
    /**
     * Metoda prywatna pobierająca z bazy danych dane o zalogowanym użytkowniku
     * @return Zwraca tabele stringów gdzie kolejno jest nazwa użytkownika,e-mail,UID lub pustą tabelę gdy użytkownik nie jest zalogowany
     */
    static public String[]  GetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            String[] details = {name,email,uid};
            return details;
        }
        return null;
    }

    /**
     * Metoda pobierająca avatar(zdjęcie użytkownika) z ustawionego z pola logowania (np avatar google)
     * @return Zwraca Url do zdjęcia jeżeli użytkownik jest zalogowany
     */
    static public Uri GetUserImage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) return user.getPhotoUrl();
        return null;
    }

    /**
     * Metoda publiczna pobierająca z bazy UID użytkownika
     * @return String UID lub null gdy użytkownik nie jest zalogowany
     */
    static public String GetUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) return user.getUid();
        return null;
    }

    /**
     * Metoda publiczna tworząca profil użytkownika bazujący na UID. Funkcja pobiera imie(nazwe uzytkownika) i adres e-mail z danych podanych przy rejestracji(logowaniu)
     * Funkcja nadpisuje profil użytkownika tz nie wypełnienie wszystkich danych będzie skutkować wyczyszczeniem niewypełnionych pól.
     * Funkcja tworzy obiekt klasy User w trakcie wykonania (dobrze żeby tak pozostało)

     */
    static public void SendUserInfoToDatabase() {
        Initialize(true);
        DatabaseReference users = SetLocation(users_dir);
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child(GetUserUID()).exists()) {
                    // run some code
                }else{
                    String[] details = GetUserInfo();
                    com.reconizer.loveteller.User user = new com.reconizer.loveteller.User(details[0],details[1],GetUserImage().toString());
                    mDatabaseReference.child(GetUserUID()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}


