package com.example.fruitninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class scoresChart extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Dialog dialogNotification;
    Switch btnSwitch;
    ImageButton btnReturn;

    private FirebaseUser user;
    private String userID;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores_chart);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        dialogNotification = new Dialog(scoresChart.this);
        createDialog(dialogNotification);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.menu_open_green);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_leaderboard);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");
        RecyclerView recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setLayoutManager(new LinearLayoutManager(this));
        final ArrayList<User> users = new ArrayList<>();
        ScoresAdapter adapter = new ScoresAdapter(users);
        recycle_view.setAdapter(adapter);

        DatabaseReference mRef = database.getReference("users");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    User currentUser = userSnapshot.getValue(User.class);
                    users.add(currentUser);
                }
                Collections.sort(users, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        return Integer.valueOf(o2.getBestScore()).compareTo(o1.getBestScore());
                    }
                });

                for (int i=0; i<users.size(); i++){
                    users.get(i).setPlace(i+1);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(scoresChart.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);
    }

    public void createDialog(Dialog dialogNotification)
    {
        dialogNotification.setContentView(R.layout.custom_dialog);
        dialogNotification.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbackground));
        dialogNotification.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogNotification.setCancelable(true);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        if (Calendar.getInstance().after(calendar)){
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        Intent intent = new Intent(getApplicationContext(), Notification_reciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        btnSwitch = dialogNotification.findViewById(R.id.btnSwitch);
        SharedPreferences  sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        btnSwitch.setChecked(sharedPreferences.getBoolean("value", true));
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                if (btnSwitch.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    btnSwitch.setChecked(true);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    btnSwitch.setChecked(false);
                    alarmManager.cancel(pendingIntent);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(scoresChart.this, gameActivity.class));
                break;
            case R.id.nav_leaderboard:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                break;
            case R.id.nav_profile:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(scoresChart.this, profileActivity.class));
                break;
            case R.id.nav_logout:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                user = FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                DatabaseReference db =  databaseReference.child("users").child(userID).child("check");
                db.setValue(false);
                startActivity(new Intent(scoresChart.this, signInActivity.class));
                break;
            case R.id.nav_notification:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                dialogNotification.show();
                break;
            case R.id.nav_classic:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(scoresChart.this, MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnReturn){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            super.onBackPressed();
        }
    }
}