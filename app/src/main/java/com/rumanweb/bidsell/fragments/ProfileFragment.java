package com.rumanweb.bidsell.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.activities.CreateAuctionActivity;
import com.rumanweb.bidsell.activities.LoginActivity;
import com.rumanweb.bidsell.activities.OnBoardingActivity;
import com.rumanweb.bidsell.activities.PaymentActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    TextView tvChangePass, tvMyBids, tvCreateAuction, tvAddBalance, tvHelp, tvInvite, tvSignOut, tvProfileName, tvUserName, btnAvailableBalance;
    LinearLayout profileOptions;
    RelativeLayout profileHeader;
    String edAmountStr, userFullName, userName, userEmail;
    TextInputEditText edAmount;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    private boolean isBalanceVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = myView.findViewById(R.id.tvProfileName);
        tvUserName = myView.findViewById(R.id.tvUserName);
        tvChangePass = myView.findViewById(R.id.tvChangePass);
        tvMyBids = myView.findViewById(R.id.tvMyBids);
        tvCreateAuction = myView.findViewById(R.id.tvCreateAuction);
        tvAddBalance = myView.findViewById(R.id.tvAddBalance);
        tvHelp = myView.findViewById(R.id.tvHelp);
        tvInvite = myView.findViewById(R.id.tvInvite);
        tvSignOut = myView.findViewById(R.id.tvSignOut);
        profileHeader = myView.findViewById(R.id.profileHeader);
        profileOptions = myView.findViewById(R.id.profileOptions);
        btnAvailableBalance = myView.findViewById(R.id.btnAvailableBalance);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnAvailableBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBalanceVisible) {
                    hideBalance();
                } else {
                    fetchAndDisplayBalance();
                }
            }
        });

        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });


        tvCreateAuction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CreateAuctionActivity.class));
            }
        });


        if (currentUser != null) {
            String userId = currentUser.getUid();  // Get current user ID (document ID in Firestore)

            // Fetch the user document from Firestore
            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                userFullName = document.getString("fullName");
                                userName = document.getString("userName");
                                userEmail = document.getString("email");

                                tvProfileName.setText(userFullName);
                                tvUserName.setText("@" + userName);
                            } else {
                                tvProfileName.setText("Guest User");
                                tvUserName.setText("@guest");
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        tvAddBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View amountView = getLayoutInflater().inflate(R.layout.amount_dialog_layout, null);
                edAmount = amountView.findViewById(R.id.edAmount);
                MaterialAlertDialogBuilder amountAlertDialog = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Amount")
                        .setView(amountView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                edAmountStr = edAmount.getText().toString();

                                if (edAmountStr.isEmpty()) {
                                    edAmount.setError("Blank data cannot be accepted");
                                } else {
                                    if (Double.parseDouble(edAmountStr) >= 10) {
                                        Intent paymentIntent = new Intent(getContext(), PaymentActivity.class);
                                        paymentIntent.putExtra("userFullName", userFullName);
                                        paymentIntent.putExtra("userEmail", userEmail);
                                        paymentIntent.putExtra("userName", userName);
                                        paymentIntent.putExtra("amount", edAmountStr);

                                        dialog.dismiss();
                                        startActivity(paymentIntent);
                                    } else {
                                        Toast.makeText(getContext(), "Amount must be greater than BDT 10", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                amountAlertDialog.show();
            }
        });

        tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appLink = "https://play.google.com/store/apps/details?id=com.bKash.customerapp"; // Replace with your actual app package name
                String shareMessage = "Hey! Check out this amazing app. Download it here: " + appLink;

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                // Start the sharing intent
                startActivity(Intent.createChooser(shareIntent, "Invite via"));
            }
        });

        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(getContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), OnBoardingActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return myView;
    }

    private void fetchAndDisplayBalance() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Fetch available balance from Firestore
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Double availableBalance = task.getResult().getDouble("availableBalance");

                // Display balance if it exists
                if (availableBalance != null) {
                    btnAvailableBalance.setText(String.format("৳ %.2f", availableBalance));
                    isBalanceVisible = true;  // Set flag to indicate balance is visible

                    // Automatically hide balance after 5 seconds
                    new Handler().postDelayed(this::hideBalance, 2000);

                } else {
                    Toast.makeText(getActivity(), "Balance not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Failed to fetch balance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideBalance() {
        btnAvailableBalance.setText("Balance");
        isBalanceVisible = false;
    }

    private void changePassword() {
        View changePassView = getLayoutInflater().inflate(R.layout.change_pass_layout, null);
        TextView tvCurrentPass = changePassView.findViewById(R.id.currentPass);
        TextView tvNewPass = changePassView.findViewById(R.id.newPass);
        TextView tvConfirmPass = changePassView.findViewById(R.id.confirmPass);

        MaterialAlertDialogBuilder changePassAlertDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Password")
                .setView(changePassView)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = changePassAlertDialog.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassStr = tvCurrentPass.getText().toString();
                String newPassStr = tvNewPass.getText().toString();
                String confirmPassStr = tvConfirmPass.getText().toString();
                if (!currentPassStr.isEmpty() && !newPassStr.isEmpty() && !confirmPassStr.isEmpty()) {
                    if (!currentPassStr.equals(newPassStr)) {
                        if (newPassStr.length() >= 8 && confirmPassStr.length() >= 8) {
                            if (newPassStr.equals(confirmPassStr)) {
                                if (currentUser != null) {
                                    String userCurrentEmail = currentUser.getEmail();
                                    assert userCurrentEmail != null;
                                    AuthCredential credential = EmailAuthProvider.getCredential(userCurrentEmail, currentPassStr);

                                    // Re-authenticate the user
                                    currentUser.reauthenticate(credential)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // User re-authenticated successfully, now update the password
                                                        currentUser.updatePassword(newPassStr)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Password updated successfully
                                                                            Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        } else {
                                                                            // An error occurred
                                                                            Toast.makeText(getContext(), "Error changing password: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        // Old password is incorrect
                                                        Toast.makeText(getContext(), "Authentication failed: Old password is incorrect", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // No user is signed in
                                    Toast.makeText(getContext(), "No user is signed in", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                tvNewPass.setError("Password does not match!");
                            }

                        } else {
                            Toast.makeText(getContext(), "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        tvNewPass.setError("Both passwords cannot be same");
                    }
                } else {
                    Toast.makeText(getContext(), "No field can be blank!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}