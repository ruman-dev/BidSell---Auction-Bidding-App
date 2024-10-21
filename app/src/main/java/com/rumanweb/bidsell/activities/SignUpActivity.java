package com.rumanweb.bidsell.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rumanweb.bidsell.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText signUpFullName, signUpEmail, signUpPass, signUpMobile, signUpUserName, signUpConfirmPass;
    private ProgressBar progressBarSignUp;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signUpFullName = findViewById(R.id.signUpName);
        signUpUserName = findViewById(R.id.signUpUserName);
        signUpEmail = findViewById(R.id.signUpEmail);
        signUpPass = findViewById(R.id.signUpPass);
        signUpConfirmPass = findViewById(R.id.signUpConfirmPass);
        signUpMobile = findViewById(R.id.signUpMobile);
        signUpBtn = findViewById(R.id.signUpBtn);
        progressBarSignUp = findViewById(R.id.progressBarSignUp);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProgress();

                String signUpName_str = signUpFullName.getText().toString().trim();
                String signUpUserName_str = signUpUserName.getText().toString().trim();
                String signUpEmail_str = signUpEmail.getText().toString().trim();
                String signUpPass_str = signUpPass.getText().toString().trim();
                String signUpMobile_str = signUpMobile.getText().toString().trim();
                String signUpConfirmPass_str = signUpConfirmPass.getText().toString().trim();

                if (signUpName_str.isEmpty() || signUpEmail_str.isEmpty() || signUpPass_str.isEmpty() || signUpMobile_str.isEmpty() || signUpUserName_str.isEmpty()) {

                    closeProgress();
                    // Show error
                    Toast.makeText(SignUpActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else if (signUpUserName_str.length() > 10) {
                    closeProgress();
                    Toast.makeText(SignUpActivity.this, "User name must be below 10 characters", Toast.LENGTH_SHORT).show();
                } else if (signUpMobile_str.length() != 11) {
                    closeProgress();
                    Toast.makeText(SignUpActivity.this, "Mobile number must be 11 characters", Toast.LENGTH_SHORT).show();
                } else if (signUpPass_str.length() < 8) {
                    closeProgress();
                    Toast.makeText(SignUpActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                } else if (!signUpPass_str.equals(signUpConfirmPass_str)) {
                    closeProgress();
                    Toast.makeText(SignUpActivity.this, "Password does not match yet", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with sign up
                    auth.createUserWithEmailAndPassword(signUpEmail_str, signUpPass_str)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User successfully signed up, now save additional data to Firestore
                                        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("fullName", signUpName_str);
                                        userData.put("email", signUpEmail_str);
                                        userData.put("userName", signUpUserName_str);
                                        userData.put("mobile", signUpMobile_str);

                                        db.collection("users").document(userId).set(userData)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            closeProgress();
                                                            Toast.makeText(SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
                                                        } else {
                                                            closeProgress();
                                                            Toast.makeText(SignUpActivity.this, "Failed to save data to Firestore", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    } else {
                                        closeProgress();
                                        Toast.makeText(SignUpActivity.this, "Sign Up Failed! Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void openProgress() {
        signUpBtn.setVisibility(View.GONE);
        progressBarSignUp.setVisibility(View.VISIBLE);
    }

    private void closeProgress() {
        progressBarSignUp.setVisibility(View.GONE);
        signUpBtn.setVisibility(View.VISIBLE);
    }
}