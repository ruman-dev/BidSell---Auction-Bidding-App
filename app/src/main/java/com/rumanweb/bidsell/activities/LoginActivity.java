package com.rumanweb.bidsell.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rumanweb.bidsell.R;

public class LoginActivity extends AppCompatActivity {

    private Button signInBtn;
    private EditText signInEmail, signInPass;
    private RelativeLayout loginLayout;
    private ProgressBar progressBarSignIn;
    private TextView redirectToSignUp;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        signInEmail = findViewById(R.id.signInEmail);
        signInPass = findViewById(R.id.signInPass);
        signInBtn = findViewById(R.id.signInBtn);
        progressBarSignIn = findViewById(R.id.progressBarSignIn);
        redirectToSignUp = findViewById(R.id.redirectToSignUp);
        loginLayout = findViewById(R.id.loginLayout);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProgress();
                String signInEmail_str = signInEmail.getText().toString();
                String signInPass_str = signInPass.getText().toString();

                if (signInEmail_str.isEmpty() || signInPass_str.isEmpty()) {
                    closeProgress();
                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else if (signInPass_str.length() < 8) {
                    closeProgress();
                    Toast.makeText(LoginActivity.this, "Please fill password at least 8 characters", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkInternet()) {
                        auth.signInWithEmailAndPassword(signInEmail_str, signInPass_str)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            closeProgress();
                                            FirebaseUser user = auth.getCurrentUser();

                                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        } else {
                                            closeProgress();
                                            Toast.makeText(LoginActivity.this, "Login Failed! Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        closeProgress();
                        Snackbar snackbar = Snackbar.make(loginLayout, "No Internet Found!", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Done", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                    }
                                })
                                .setBackgroundTint(getResources().getColor(R.color.primaryColor))
                                .setActionTextColor(getResources().getColor(R.color.darkBlue))
                                .setTextColor(getResources().getColor(R.color.white))
                                .show();
                    }
                }
            }
        });
    }

    private void openProgress() {
        signInBtn.setVisibility(View.GONE);
        progressBarSignIn.setVisibility(View.VISIBLE);
    }

    private void closeProgress() {
        progressBarSignIn.setVisibility(View.GONE);
        signInBtn.setVisibility(View.VISIBLE);
    }

    private boolean checkInternet() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conManager.getActiveNetworkInfo() != null && conManager.getActiveNetworkInfo().isAvailable() && conManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}