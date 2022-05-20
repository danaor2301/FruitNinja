package com.example.fruitninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class forgotPassword extends AppCompatActivity implements View.OnClickListener
{
    private TextInputEditText etEmail;
    private Button btnResetPassword;
    private ImageButton btnReturn;
    private ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        etEmail = findViewById(R.id.etEmail);
        btnReturn = findViewById(R.id.btnReturn);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();

        btnReturn.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        if (view == btnResetPassword)
        {
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            String email = etEmail.getText().toString();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("please enter an email");
                etEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("please enter valid email.  Example: aaa@aaa.aaa");
                etEmail.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(forgotPassword.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(forgotPassword.this, signInActivity.class);
                            startActivity(intent);
                        } else{
                            Toast.makeText(forgotPassword.this, "Something wrong happened! Please try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }
        if (view == btnReturn){
            if (OpeningActivity.soundCheck == true)
                OpeningActivity.mp_click.start();
            Intent intent = new Intent(forgotPassword.this, signInActivity.class);
            startActivity(intent);
        }

    }
}