package com.example.fruitninja;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class signInActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    private Button btnSignIn;
    private TextInputEditText etEmail, etPassword;
    private TextView tvSignUp, forgotPassword;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        progressDialog = new ProgressDialog(this);
        progressBar = findViewById(R.id.progressBar);

        btnSignIn = findViewById(R.id.btnSignIn);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        forgotPassword = findViewById(R.id.forgotPassword);
        checkBox = findViewById(R.id.checkbox);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null) {

            progressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance("https://fruitninja-c44d1-default-rtdb.firebaseio.com/");
            DatabaseReference databaseReference1=firebaseDatabase.getReference("users").child(firebaseAuth.getCurrentUser().getUid());

            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        boolean check = user.isCheck();
                        if (check == true) {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(signInActivity.this.getApplicationContext(), gameActivity.class));
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(signInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        btnSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        checkBox.setOnClickListener(this);
    }

    private void userLoginDb() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("please enter an email");
            etEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("please enter valid email.  Example: aaa@aaa.aaa");
            etEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)){
            etPassword.setError("please enter a password");
            etPassword.requestFocus();
        } else if (password.length() < 6){
            etPassword.setError("password should be at least 6 characters long");
            etPassword.requestFocus();
        } else{
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (checkBox.isChecked()) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            userID = user.getUid();
                            DatabaseReference db = databaseReference.child("users").child(userID).child("check");
                            db.setValue(true);
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(signInActivity.this.getApplicationContext(), gameActivity.class));
                        } else {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            userID = user.getUid();
                            DatabaseReference db = databaseReference.child("users").child(userID).child("check");
                            db.setValue(false);
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(signInActivity.this.getApplicationContext(), gameActivity.class));
                        }
                    } else {
                        Toast.makeText(signInActivity.this, "wrong email or password", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view == btnSignIn)
        {
            OpeningActivity.mp_click.start();
            userLoginDb();
        }
        if (view == tvSignUp)
        {
            OpeningActivity.mp_click.start();
            finish();
            startActivity(new Intent(this, registerActivity.class));
        }
        if (view == forgotPassword)
        {
            OpeningActivity.mp_click.start();
            finish();
            startActivity(new Intent(this, forgotPassword.class));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_home:
                OpeningActivity.mp_click.start();
                startActivity(new Intent(signInActivity.this, gameActivity.class));
                break;
            case R.id.nav_leaderboard:
                OpeningActivity.mp_click.start();
                startActivity(new Intent(signInActivity.this, scoresChart.class));
                break;
            case R.id.nav_profile:
                OpeningActivity.mp_click.start();
                startActivity(new Intent(signInActivity.this, profileActivity.class));
                break;
        }
        return true;
    }
}