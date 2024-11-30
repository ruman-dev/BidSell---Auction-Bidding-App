package com.rumanweb.bidsell.activities;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rumanweb.bidsell.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class CreateAuctionActivity extends AppCompatActivity {

    EditText auctionName, startingPrice, imageLink, highlights, description, quantity, openingTime, closingTime;
    String auctionNameStr, startingPriceStr, imageLinkStr, highlightsStr, descriptionStr, quantityStr;
    MaterialButton btnSubmitARequest;
    Timestamp openingTimestamp, closingTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_auction);

        // Initialize UI elements
        auctionName = findViewById(R.id.auctionName);
        startingPrice = findViewById(R.id.startingPrice);
        imageLink = findViewById(R.id.imageLink);
        highlights = findViewById(R.id.highlights);
        description = findViewById(R.id.description);
        quantity = findViewById(R.id.quantity);
        openingTime = findViewById(R.id.openingTime);
        closingTime = findViewById(R.id.closingTime);
        btnSubmitARequest = findViewById(R.id.btnSubmitARequest);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        openingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openingTimePicker();
            }
        });

        closingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingTimePicker();
            }
        });

        btnSubmitARequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from UI elements
                auctionNameStr = auctionName.getText().toString();
                startingPriceStr = startingPrice.getText().toString();
                imageLinkStr = imageLink.getText().toString();
                highlightsStr = highlights.getText().toString();
                descriptionStr = description.getText().toString();
                quantityStr = quantity.getText().toString();

                if (auctionNameStr.isEmpty() || startingPriceStr.isEmpty() || imageLinkStr.isEmpty() || highlightsStr.isEmpty()
                        || descriptionStr.isEmpty() || quantityStr.isEmpty() || openingTimestamp == null || closingTimestamp == null) {
                    Toast.makeText(CreateAuctionActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        String userEmail = currentUser.getEmail();

                        long quantityTxt = Long.parseLong(quantityStr);
                        double startingPriceTxt = Double.parseDouble(startingPriceStr);

                        // Create a new auction object
                        Map<String, Object> createAuction = new HashMap<>();
                        createAuction.put("userEmail", userEmail);
                        createAuction.put("auctionName", auctionNameStr);
                        createAuction.put("startingPrice", startingPriceTxt);
                        createAuction.put("imageLink", imageLinkStr);
                        createAuction.put("highlights", highlightsStr);
                        createAuction.put("description", descriptionStr);
                        createAuction.put("quantity", quantityTxt);
                        createAuction.put("openingTime", openingTimestamp);
                        createAuction.put("closingTime", closingTimestamp);
                        createAuction.put("status", "Pending");
                        createAuction.put("userId", userId);

                        // Add a new document with the auction details
                        db.collection("CreateAuctions")
                                .add(createAuction)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CreateAuctionActivity.this, "Auction created successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(CreateAuctionActivity.this, "Error creating auction: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        finish();
                    }
                }
            }
        });
    }

    private void openingTimePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Opening Date");
        final MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "OPENING_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateAuctionActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                Date selectedDate = calendar.getTime();

                                // Convert date to Firestore Timestamp
                                openingTimestamp = new Timestamp(selectedDate);

                                // Format the date for display
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm yyyy", Locale.getDefault());
                                String formattedDate = dateFormat.format(selectedDate);

                                openingTime.setText(formattedDate);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }

    private void closingTimePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Closing Date");
        final MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(), "CLOSING_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateAuctionActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                Date selectedDate = calendar.getTime();

                                // Convert date to Firestore Timestamp
                                closingTimestamp = new Timestamp(selectedDate);

                                // Format the date for display
                                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm yyyy", Locale.getDefault());
                                String formattedDate = dateFormat.format(selectedDate);

                                closingTime.setText(formattedDate);
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });
    }
}