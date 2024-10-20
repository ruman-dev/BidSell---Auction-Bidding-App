package com.rumanweb.bidsell.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.activities.LoginActivity;
import com.rumanweb.bidsell.activities.OnBoardingActivity;
import com.rumanweb.bidsell.activities.PaymentActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    TextView tvEditProfile, tvMyBids, tvCreateAuction, tvAddBalance, tvHelp, tvSettings, tvInvite, tvSignOut, tvProfileName, tvUserName, btnAvailableBalance;
    LinearLayout profileOptions;
    RelativeLayout profileHeader;
    String edAmountStr, userFullName, userName, userEmail;
    TextInputEditText edAmount;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    private GestureDetectorCompat gestureDetector;
    private boolean isBalanceVisible = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = myView.findViewById(R.id.tvProfileName);
        tvUserName = myView.findViewById(R.id.tvUserName);
        tvEditProfile = myView.findViewById(R.id.tvEditProfile);
        tvMyBids = myView.findViewById(R.id.tvMyBids);
        tvCreateAuction = myView.findViewById(R.id.tvCreateAuction);
        tvAddBalance = myView.findViewById(R.id.tvAddBalance);
        tvHelp = myView.findViewById(R.id.tvHelp);
        tvSettings = myView.findViewById(R.id.tvSettings);
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
                                edAmountStr = Objects.requireNonNull(edAmount.getText()).toString();

                                Intent paymentIntent = new Intent(getContext(), PaymentActivity.class);
                                paymentIntent.putExtra("userFullName", userFullName);
                                paymentIntent.putExtra("userEmail",userEmail);
                                paymentIntent.putExtra("userName", userName);
                                paymentIntent.putExtra("amount", edAmountStr);

                                dialog.dismiss();
                                startActivity(paymentIntent);

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
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch available balance from Firestore
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Double availableBalance = task.getResult().getDouble("availableBalance");

                // Display balance if it exists
                if (availableBalance != null) {
                    btnAvailableBalance.setText(String.format("$%.2f", availableBalance));
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
}