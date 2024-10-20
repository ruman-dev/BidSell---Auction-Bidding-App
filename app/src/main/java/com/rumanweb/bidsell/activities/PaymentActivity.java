package com.rumanweb.bidsell.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rbmjltd.uddoktapay.UddoktaPay;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.fragments.ProfileFragment;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {
    WebView paymentWebView;
    String FULL_NAME, EMAIL, ENTEREDAMOUNT, USERNAME;
    Double availableBalance;
    LinearLayout paymentSuccessLayout;
    TextView userEmail, paidAmount, paidTime, userName, selectedPaymentMethod, tvTransactionId, tvCountDown;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    // Constants for payment
    private static final String API_KEY = "982d381360a69d419689740d9f2e26ce36fb7a50";
    private static final String CHECKOUT_URL = "https://sandbox.uddoktapay.com/api/checkout-v2";
    private static final String VERIFY_PAYMENT_URL = "https://sandbox.uddoktapay.com/api/verify-payment";
    private static final String REDIRECT_URL = "https://your-site.com";
    private static final String CANCEL_URL = "https://your-site.com";

    // Instance variables to store payment information
    private String storedFullName;
    private String storedEmail;
    private String storedAmount;
    private String storedInvoiceId;
    private String storedPaymentMethod;
    private String storedSenderNumber;
    private String storedTransactionId;
    private String storedDate;
    private String storedFee;
    private String storedChargedAmount;

    private String storedMetaKey1;
    private String storedMetaValue1;

    private String storedMetaKey2;
    private String storedMetaValue2;

    private String storedMetaKey3;
    private String storedMetaValue3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentWebView = findViewById(R.id.paymentWebView);
        paymentSuccessLayout = findViewById(R.id.paymentSuccessLayout);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        paidAmount = findViewById(R.id.paidAmount);
        paidTime = findViewById(R.id.paidTime);
        selectedPaymentMethod = findViewById(R.id.paymentMethod);
        tvTransactionId = findViewById(R.id.transactionId);
        tvCountDown = findViewById(R.id.tvCountDownToRedirect);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FULL_NAME = Objects.requireNonNull(getIntent().getExtras()).getString("userFullName");
        EMAIL = Objects.requireNonNull(getIntent().getExtras()).getString("userEmail");
        USERNAME = Objects.requireNonNull(getIntent().getExtras()).getString("userName");
        ENTEREDAMOUNT = Objects.requireNonNull(getIntent().getExtras()).getString("amount");

        // Set your metadata values in the map
        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("CustomMetaData1", "Meta Value 1");
        metadataMap.put("CustomMetaData2", "Meta Value 2");
        metadataMap.put("CustomMetaData3", "Meta Value 3");

        UddoktaPay.PaymentCallback paymentCallback = new UddoktaPay.PaymentCallback() {
            @Override
            public void onPaymentStatus(String status, String fullName, String email, String amount, String invoiceId,
                                        String paymentMethod, String senderNumber, String transactionId,
                                        String date, Map<String, String> metadataValues, String fee,String chargeAmount) {
                // Callback method triggered when the payment status is received from the payment gateway.
                // It provides information about the payment transaction.
                storedFullName = FULL_NAME;
                storedEmail = EMAIL;
                storedAmount = ENTEREDAMOUNT;
                storedInvoiceId = invoiceId;
                storedPaymentMethod = paymentMethod;
                storedSenderNumber = senderNumber;
                storedTransactionId = transactionId;
                storedDate = date;
                storedFee = fee;
                storedChargedAmount = chargeAmount;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Clear previous metadata values to avoid duplication
                        storedMetaKey1 = null;
                        storedMetaValue1 = null;
                        storedMetaKey2 = null;
                        storedMetaValue2 = null;
                        storedMetaKey3 = null;
                        storedMetaValue3 = null;

                        // Iterate through the metadata map and store the key-value pairs
                        for (Map.Entry<String, String> entry : metadataValues.entrySet()) {
                            String metadataKey = entry.getKey();
                            String metadataValue = entry.getValue();

                            if ("CustomMetaData1".equals(metadataKey)) {
                                storedMetaKey1 = metadataKey;
                                storedMetaValue1 = metadataValue;
                            } else if ("CustomMetaData2".equals(metadataKey)) {
                                storedMetaKey2 = metadataKey;
                                storedMetaValue2 = metadataValue;
                            } else if ("CustomMetaData3".equals(metadataKey)) {
                                storedMetaKey3 = metadataKey;
                                storedMetaValue3 = metadataValue;
                            }
                        }

                        // Update UI based on payment status
                        if ("COMPLETED".equals(status)) {
                            // Handle payment completed case

                            userName.setText(storedFullName+" ("+USERNAME+")");
                            userEmail.setText(storedEmail);
                            paidAmount.setText(String.format("%.2f",Double.parseDouble(storedAmount)));
                            selectedPaymentMethod.setText(storedPaymentMethod);
                            tvTransactionId.setText(storedTransactionId+ " ("+ storedSenderNumber+")");

                            Date currentDate = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                            SimpleDateFormat dbDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()); // Don't change this format, Database issues

                            String currentFormattedDate = simpleDateFormat.format(currentDate);

                            Timestamp dbTimeStamp = new Timestamp(currentDate);

                            paidTime.setText(currentFormattedDate);
                            paymentWebView.setVisibility(View.GONE);
                            paymentSuccessLayout.setVisibility(View.VISIBLE);

                            ProcessPayment(storedFullName, USERNAME, storedEmail, storedPaymentMethod, storedTransactionId, dbTimeStamp, Double.parseDouble(storedAmount));

                            startCountDownTimer();

                        } else if ("PENDING".equals(status)) {
                            // Handle payment pending case
                            Toast.makeText(PaymentActivity.this, "Your payment transaction is now on Pending", Toast.LENGTH_SHORT).show();
                        } else if ("ERROR".equals(status)) {
                            // Handle payment error case
                            Toast.makeText(PaymentActivity.this, "404 Error! Try again later!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        };

        UddoktaPay uddoktapay = new UddoktaPay(paymentWebView, paymentCallback);
        uddoktapay.loadPaymentForm(API_KEY, FULL_NAME, EMAIL, ENTEREDAMOUNT, CHECKOUT_URL, VERIFY_PAYMENT_URL, REDIRECT_URL, CANCEL_URL, metadataMap);


    }
    private void ProcessPayment(String fullName, String username, String email, String paymentMethod, String transactionId, Timestamp transactionTime, double paidAmount) {
        String userId = mAuth.getCurrentUser().getUid();  // Get current user ID

        // Create a new payment entry
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("userFullName", fullName);
        paymentData.put("userName", username);
        paymentData.put("userEmail", email);
        paymentData.put("paymentMethod", paymentMethod);
        paymentData.put("transactionId", transactionId);
        paymentData.put("transactionTime", transactionTime);
        paymentData.put("paidAmount", paidAmount);

        // First, fetch the user's available balance
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                availableBalance = task.getResult().getDouble("availableBalance");
                if (availableBalance == null) {
                    availableBalance = 0.0;
                }

                // Update the available balance
                availableBalance += paidAmount;

                // Update Firestore with the new available balance
                firestore.collection("users").document(userId).update("availableBalance", availableBalance)
                        .addOnCompleteListener(balanceUpdateTask -> {
                            if (balanceUpdateTask.isSuccessful()) {
                                // Store payment details in subcollection
                                firestore.collection("users").document(userId)
                                        .collection("payments")
                                        .document(transactionId)
                                        .set(paymentData)
                                        .addOnCompleteListener(paymentTask -> {
                                            if (paymentTask.isSuccessful()) {

                                                Toast.makeText(PaymentActivity.this, "Payment recorded successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PaymentActivity.this, "Failed to record payment", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(PaymentActivity.this, "Failed to update balance", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(PaymentActivity.this, "Failed to retrieve user balance", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startCountDownTimer() {
        final int[] countdownTime = {10};
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (countdownTime[0] > 0) {
                    tvCountDown.setText("Redirecting in " + countdownTime[0] + "s...");
                    countdownTime[0]--;
                    handler.postDelayed(this, 1000);  // Update every second
                } else {
                    // Redirect to ProfileFragment or Homepage after 10 seconds
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main, new ProfileFragment()) // Replace with your fragment container
                            .commit();

                    // Alternatively, if using an intent:
                    // startActivity(new Intent(PaymentActivity.this, MainActivity.class)); // If ProfileFragment is part of MainActivity
                    finish(); // Finish this activity
                }
            }
        };

        // Start the countdown
        handler.post(runnable);
    }
}