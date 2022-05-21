package com.example.fruitninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class gameOverActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Dialog dialogNotification;
    Switch btnSwitch;
    TextView scoreTv, bestScoreTv, tvLeaderboard;
    ImageButton ibLeaderboard, ibHome, ibStartOver;
    ImageView ivNewBest;
    int finalScore = 0, bestScore = 0;
    private FirebaseUser user;
    private String userID;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        finalScore = intent.getIntExtra("finalScore", 0);
        scoreTv = findViewById(R.id.scoreTv);
        bestScoreTv = findViewById(R.id.bestScoreTv);
        scoreTv.setText("score: " + finalScore);
        ibStartOver = findViewById(R.id.ibStartOver);
        ibHome = findViewById(R.id.ibHome);
        ibLeaderboard = findViewById(R.id.ibLeaderboard);
        tvLeaderboard = findViewById(R.id.tvLeaderboard);
        ivNewBest = findViewById(R.id.ivNewBest);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);

        ivNewBest.setVisibility(View.GONE);
        if (OpeningActivity.musicCheck == true)
            OpeningActivity.themeSound.start();
        checkBestScore();

        // Notification dialog
        dialogNotification = new Dialog(gameOverActivity.this);
        createDialog(dialogNotification);

        // Navigation drawer
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.menu_open);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_classic);
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

        // On click listeners
        tvLeaderboard.setOnClickListener(this);
        ibLeaderboard.setOnClickListener(this);
        ibStartOver.setOnClickListener(this);
        ibHome.setOnClickListener(this);
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

    public void checkBestScore()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                final int getBestScore = snapshot.child(userID).child("bestScore").getValue(int.class);
                if (finalScore > getBestScore)
                {
                    if (OpeningActivity.soundCheck == true)
                        OpeningActivity.mp_newBestScore.start();
                    ivNewBest.setVisibility(View.VISIBLE);
                    databaseReference.child("users").child(userID).child("bestScore").setValue(finalScore);
                    bestScore = finalScore;
                    bestScoreTv.setText("Best score: " + bestScore);
                }
                else
                {
                    bestScore = snapshot.child(userID).child("bestScore").getValue(int.class);
                    bestScoreTv.setText("Best score: " + bestScore);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }

    @Override
    public void onClick(View view)
    {
        if (view == ibStartOver){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity( new Intent(gameOverActivity.this, MainActivity.class));
        }
        if (view == ibLeaderboard){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity( new Intent(gameOverActivity.this, scoresChart.class));
        }
        if (view == tvLeaderboard){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity( new Intent(gameOverActivity.this, scoresChart.class));
        }
        if (view == ibHome){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity( new Intent(gameOverActivity.this, gameActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(gameOverActivity.this, gameActivity.class));
                break;
            case R.id.nav_leaderboard:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(gameOverActivity.this, scoresChart.class));
                break;
            case R.id.nav_profile:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(gameOverActivity.this, profileActivity.class));
                break;
            case R.id.nav_logout:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                user = FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                DatabaseReference db =  databaseReference.child("users").child(userID).child("check");
                db.setValue(false);
                startActivity(new Intent(gameOverActivity.this, signInActivity.class));
                break;
            case R.id.nav_notification:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                dialogNotification.show();
                break;
            case R.id.nav_classic:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                break;
        }
        return true;
    }
}