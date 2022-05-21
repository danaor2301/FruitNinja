package com.example.fruitninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class registerActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{

    private Button btnRegister;
    private EditText etUsername, etPassword, etEmail;
    private TextView tvSignIn;
    private CheckBox checkBox;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvSignIn = findViewById(R.id.tvSignIn);
        checkBox = findViewById(R.id.checkbox);
        btnRegister.setOnClickListener(this);
        tvSignIn.setOnClickListener(this);
        checkBox.setOnClickListener(this);
    }

    private void createUserDb()
    {
        final String email = etEmail.getText().toString();
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            etEmail.setError("please enter an email");
            etEmail.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("please enter valid email. Example: aaa@aaa.aaa");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(username)){
            etUsername.setError("please enter a username");
            etUsername.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            etPassword.setError("please enter a password");
            etPassword.requestFocus();
        } else if (password.length() < 6){
            etPassword.setError("password should be at least 6 characters long");
            etPassword.requestFocus();
        }
        else {
            progressDialog.setMessage("Registering user...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String date = new SimpleDateFormat
                                        ("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                User user =  new User(email, username, 0, false, date, 0);
                                if (checkBox.isChecked()){
                                    user.setCheck(true);
                                }
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    finish();
                                                    Intent intent = new Intent(getApplicationContext(), gameActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(registerActivity.this, "registered failed, please try again", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(registerActivity.this, "registered failed", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnRegister)
        {
            OpeningActivity.mp_click.start();
            createUserDb();
        }
        if (view == tvSignIn)
        {
            OpeningActivity.mp_click.start();
            startActivity(new Intent(this, signInActivity.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                OpeningActivity.mp_click.start();
                startActivity(new Intent(registerActivity.this, gameActivity.class));
                break;
            case R.id.nav_leaderboard:
                OpeningActivity.mp_click.start();
                startActivity(new Intent(registerActivity.this, scoresChart.class));
                break;
            case R.id.nav_profile:
                OpeningActivity.mp_click.start();
                startActivity(new Intent(registerActivity.this, profileActivity.class));
                break;
        }
        return true;
    }
}