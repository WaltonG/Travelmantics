package com.walton.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater userActivityMenuInflater = getMenuInflater();
        userActivityMenuInflater.inflate(R.menu.user_activity_menu, menu);

        if (FirebaseUtility.isAdmin == true){
           menu.findItem(R.id.admin_add_travel_deal_menu).setVisible(true);
        }
        else {
            menu.findItem(R.id.admin_add_travel_deal_menu).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.admin_add_travel_deal_menu:
                startActivity(new Intent(this, AdminActivity.class));
                return true;
            case R.id.logout_menu:
                // [START auth_fui_signout]
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User Logged Out");
                                FirebaseUtility.attachAuthStateListener();
                            }
                        });
                // [END auth_fui_signout]
                FirebaseUtility.detachAuthStateListener();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtility.detachAuthStateListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtility.openFirebaseReference("traveldeals", this);
        RecyclerView dealsRecyclerView = findViewById(R.id.deals_recycler_view);
        final DealAdapter dealAdapter = new DealAdapter() ;
        dealsRecyclerView.setAdapter(dealAdapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        dealsRecyclerView.setLayoutManager(dealsLayoutManager);
        FirebaseUtility.attachAuthStateListener();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (FirebaseUtility.isAdmin == true){
            menu.findItem(R.id.admin_add_travel_deal_menu).setVisible(true);
        }
        else {
            menu.findItem(R.id.admin_add_travel_deal_menu).setVisible(false);
        }
        Log.d("Admin","Prepare options menu");
        return super.onPrepareOptionsMenu(menu);
    }

    public void showMenu(){
        invalidateOptionsMenu();
        Log.d("Admin","Invalidate options menu");
    }
}