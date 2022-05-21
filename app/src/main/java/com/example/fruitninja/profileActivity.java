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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.service.autofill.Validator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Calendar;

public class profileActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Dialog dialogNotification, dialogChangeName;
    Switch btnSwitch;

    private Button btnLogOut;
    private ImageButton btnReturn;
    private TextView tvEmail, tvUsername, tvChangeName;
    String newName;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private String userID;
    private DatabaseReference databaseReference2 = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Dialogs
        dialogNotification = new Dialog(profileActivity.this);
        dialogChangeName = new Dialog(profileActivity.this);
        createDialog(dialogNotification);
        createDialogChangeName(dialogChangeName);

        // Navigation view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.menu_open_purple);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
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

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogOut = findViewById(R.id.btnLogout);
        btnReturn = findViewById(R.id.btnReturn);
        tvEmail = findViewById(R.id.tvEmail2);
        tvUsername = findViewById(R.id.tvUsername2);
        tvChangeName = findViewById(R.id.tvChangeName);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        if(firebaseAuth.getCurrentUser()!=null){
            user = firebaseAuth.getCurrentUser();
            userID = user.getUid();

            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null){
                        String username = userProfile.getName();
                        String email = userProfile.getEmail();
                        tvUsername.setText(username);
                        tvEmail.setText(email);
                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(profileActivity.this, "something wrong happened!", Toast.LENGTH_LONG).show();
                }
            });
        }

        btnLogOut.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
        tvChangeName.setOnClickListener(this);
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

    public void createDialogChangeName(Dialog dialogChangeName)
    {
        dialogChangeName.setContentView(R.layout.dialog_changename);
        dialogChangeName.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialogbackground));
        dialogChangeName.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogChangeName.setCancelable(true);
        Button save = dialogChangeName.findViewById(R.id.btnSave);
        Button cancel = dialogChangeName.findViewById(R.id.btnCancel);
        EditText editText = dialogChangeName.findViewById(R.id.etNewName);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                newName = editText.getText().toString();
                tvUsername.setText(newName);
                user = FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                databaseReference2.child("users").child(userID).child("name").setValue(newName);
                dialogChangeName.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                dialogChangeName.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnLogOut)
        {
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();
            DatabaseReference db =  databaseReference2.child("users").child(userID).child("check");
            db.setValue(false);
            startActivity(new Intent(this, signInActivity.class));
        }
        if (view ==  btnReturn)
        {
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            super.onBackPressed();
        }
        if (view == tvChangeName){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            dialogChangeName.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(profileActivity.this, gameActivity.class));
                break;
            case R.id.nav_leaderboard:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(profileActivity.this, scoresChart.class));
                break;
            case R.id.nav_profile:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                break;
            case R.id.nav_logout:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                user = FirebaseAuth.getInstance().getCurrentUser();
                userID = user.getUid();
                DatabaseReference db =  databaseReference2.child("users").child(userID).child("check");
                db.setValue(false);
                startActivity(new Intent(profileActivity.this, signInActivity.class));
                break;
            case R.id.nav_notification:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                dialogNotification.show();
                break;
            case R.id.nav_classic:
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(profileActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}