package com.walton.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtility {
    public static FirebaseDatabase mFireBaseDatabase;
    public static DatabaseReference mDatabaseReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<TravelDeal> mTravelDeals;
    private static UserActivity callerActivity;
    private static FirebaseUtility mFirebaseUtility;
    public static boolean isAdmin;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;

    private static final int RC_SIGN_IN = 123;

    private FirebaseUtility() {
    }

    public static void openFirebaseReference(String reference, final UserActivity caller){
        if (mFirebaseUtility == null){
            mFirebaseUtility = new FirebaseUtility();
            mFireBaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            callerActivity = caller;
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseUtility.signIn();
                    }
                    else {
                        String userId = mFirebaseAuth.getUid();
                        checkAdmin(userId);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!",
                            Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();
        }
        mTravelDeals = new ArrayList<>();
        mDatabaseReference = mFireBaseDatabase.getReference().child(reference);
    }

    private static void checkAdmin(String userId) {
        FirebaseUtility.isAdmin = false;
        DatabaseReference adminDatabaseReference = mFireBaseDatabase.getReference()
                .child("administrators").child(userId);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtility.isAdmin = true;
                callerActivity.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        adminDatabaseReference.addChildEventListener(childEventListener);
    }

    private static void signIn(){
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        callerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    public static  void attachAuthStateListener(){
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static  void detachAuthStateListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public static void connectStorage() {
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }
}

