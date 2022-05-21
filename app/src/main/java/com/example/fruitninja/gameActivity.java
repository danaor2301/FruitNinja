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

import java.util.Calendar;

public class gameActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Dialog dialogNotification, dialogSettings;
    Switch btnSwitch;
    private ImageButton btnClassic, btnSettings, btnLeaderboard;
    private ImageView ivApple, ivOrange, ivWatermelon;
    private Animation animRotateCw, animRotateCcw;
    MediaPlayer mp_click;

    private FirebaseUser user;
    private String userID;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mp_click = MediaPlayer.create(this, R.raw.click2_sound);
        btnClassic = findViewById(R.id.btnClassic);
        ivApple = findViewById(R.id.ivApple);
        btnSettings = findViewById(R.id.btnSettings);
        ivOrange = findViewById(R.id.ivOrange);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        ivWatermelon = findViewById(R.id.ivWatermelon);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);

        // Dialogs
        dialogNotification = new Dialog(gameActivity.this);
        createDialog(dialogNotification);
        dialogSettings = new Dialog(gameActivity.this);
        settingsDialog(dialogSettings);

        // Navigation drawer
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.menu_open);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
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

        // Animations
        animRotateCw = AnimationUtils.loadAnimation(this, R.anim.rotatecw);
        animRotateCcw = AnimationUtils.loadAnimation(this, R.anim.rotateccw);
        btnClassic.startAnimation(animRotateCcw);
        ivApple.startAnimation(animRotateCw);
        btnSettings.startAnimation(animRotateCcw);
        ivOrange.startAnimation(animRotateCw);
        btnLeaderboard.startAnimation(animRotateCcw);
        ivWatermelon.startAnimation(animRotateCw);

        // On click listeners
        btnClassic.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnLeaderboard.setOnClickListener(this);
        ivApple.setOnClickListener(this);
        ivOrange.setOnClickListener(this);
        ivWatermelon.setOnClickListener(this);
        toolbar.setOnClickListener(this);
        navigationView.setOnClickListener(this);
    }

    public void createDialog(Dialog dialogNotification)
    {
        dialogNotification.setContentView(R.layout.custom_dialog);
        dialogNotification.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbackground));
        dialogNotification.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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

    public void settingsDialog(Dialog dialogSettings)
    {
        dialogSettings.setContentView(R.layout.dialog_settings);
        dialogSettings.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_settings));
        dialogSettings.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogSettings.setCancelable(false);
        TextView tvUsernameSettings = dialogSettings.findViewById(R.id.username_settings);
        TextView tvDateSettings = dialogSettings.findViewById(R.id.date_settings);
        ImageButton musicBtn = dialogSettings.findViewById(R.id.musicBtn);
        ImageButton soundBtn = dialogSettings.findViewById(R.id.soundBtn);
        Button closeBtn = dialogSettings.findViewById(R.id.closeBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String username = snapshot.child(userID).child("name").getValue(String.class);
                String date = snapshot.child(userID).child("date").getValue(String.class);
                tvUsernameSettings.setText(username);
                tvDateSettings.setText(date);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        if (OpeningActivity.musicCheck == true){
            musicBtn.setImageResource(R.drawable.music_note_on);
        }
        else
            musicBtn.setImageResource(R.drawable.music_note_off);
        if (OpeningActivity.soundCheck == true)
            soundBtn.setImageResource(R.drawable.volume_up_icon);
        else
            soundBtn.setImageResource(R.drawable.volume_off_icon);

        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                if(OpeningActivity.musicCheck == true){
                    musicBtn.setImageResource(R.drawable.music_note_off);
                    OpeningActivity.themeSound.pause();
                    OpeningActivity.musicCheck = false;
                } else {
                    musicBtn.setImageResource(R.drawable.music_note_on);
                    OpeningActivity.themeSound.start();
                    OpeningActivity.musicCheck = true;
                }
            }
        });
        soundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                if(OpeningActivity.soundCheck == true){
                    soundBtn.setImageResource(R.drawable.volume_off_icon);
                    OpeningActivity.soundCheck = false;
                } else {
                    soundBtn.setImageResource(R.drawable.volume_up_icon);
                    OpeningActivity.soundCheck = true;
                }
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                dialogSettings.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_leaderboard:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(gameActivity.this, scoresChart.class));
                break;
            case R.id.nav_profile:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(gameActivity.this, profileActivity.class));
                break;
            case R.id.nav_logout:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                user = FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                DatabaseReference db =  databaseReference.child("users").child(userID).child("check");
                db.setValue(false);
                startActivity(new Intent(gameActivity.this, signInActivity.class));
                break;
            case R.id.nav_notification:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                dialogNotification.show();
                break;
            case R.id.nav_classic:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(gameActivity.this, MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == ivApple){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity(new Intent(gameActivity.this, MainActivity.class));
        }
        if (view == ivOrange){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            dialogSettings.show();
        }
        if (view == ivWatermelon){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity(new Intent(gameActivity.this, scoresChart.class));
        }
        if (view == btnClassic){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity(new Intent(gameActivity.this, MainActivity.class));
        }
        if (view == btnSettings){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            dialogSettings.show();
        }
        if (view == btnLeaderboard){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            startActivity(new Intent(gameActivity.this, scoresChart.class));
        }
        if (view == navigationView){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            drawerLayout.openDrawer(GravityCompat.START);
        }
        if (view == toolbar){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }
}