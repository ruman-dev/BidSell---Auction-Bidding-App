package com.rumanweb.bidsell.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rumanweb.bidsell.R;
import com.rumanweb.bidsell.adapters.BidHistoryAdapter;
import com.rumanweb.bidsell.models.BidHistoryLVModel;
import com.rumanweb.bidsell.models.ProductItemModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView imgProductDetails, imgArrowHistory, imgArrowDetail;
    EditText edMaxBid;
    Button btnPlaceBid;
    TextView product_title, tvCurrentBid, tvHighlights, tvDesc, tvCountTimer, tvAuctionStatus;
    RelativeLayout bidDetailCol, bidHistoryCol, productDetailsLayout;
    LinearLayout placeBidLayout;
    RecyclerView bidHistoryRecycler;
    private BidHistoryAdapter bidHistoryAdapter;
    CardView bidHistoryCardView, bidDescCardView, topCardView;
    private final Handler countDownHandler = new Handler();
    private Runnable countdownRunnable;
    private String productTitle;
    public long listingNo;
    private SimpleDateFormat displayDateFormat, inputDateFormat;
    ProductItemModel productItemModel = null;
    private boolean isToastShown = false;
    private double currentBid, availableBalance;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private double productStartingPrice, biddingPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("product_serial");
        if (obj instanceof ProductItemModel) {
            productItemModel = (ProductItemModel) obj;
        }

        imgProductDetails = findViewById(R.id.imgProductDetails);
        imgArrowHistory = findViewById(R.id.imgArrowHistory);
        imgArrowDetail = findViewById(R.id.imgArrowDtail);
        edMaxBid = findViewById(R.id.edMaxBid);
        btnPlaceBid = findViewById(R.id.btnPlaceBid);
        product_title = findViewById(R.id.product_title);
        tvCurrentBid = findViewById(R.id.tvCurrentBid);
        tvHighlights = findViewById(R.id.tvHighlights);
        tvDesc = findViewById(R.id.tvDesc);
        tvCountTimer = findViewById(R.id.tvCountTimer);
        tvAuctionStatus = findViewById(R.id.tvAuctionStatus);
        bidDetailCol = findViewById(R.id.bidDtailCol);
        bidHistoryCol = findViewById(R.id.bidHistoryCol);
        bidHistoryCardView = findViewById(R.id.bidHistoryCardView);
        bidDescCardView = findViewById(R.id.bidDetailCardView);
        topCardView = findViewById(R.id.topCardView);
        placeBidLayout = findViewById(R.id.placeBidLayout);
        productDetailsLayout = findViewById(R.id.productDetailsLayout);
        bidHistoryRecycler = findViewById(R.id.bidHistoryRecycler);
        bidHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));

        bidHistoryAdapter = new BidHistoryAdapter();
        bidHistoryRecycler.setAdapter(bidHistoryAdapter);

        if (productItemModel != null) {
            Glide.with(this).load(productItemModel.getImg_url()).into(imgProductDetails);
            productTitle = productItemModel.getTitle();
            productStartingPrice = productItemModel.getStarting_price();
            listingNo = productItemModel.getListing_no();
            tvHighlights.setText(productItemModel.getHighlights());
            tvDesc.setText(productItemModel.getDescription());

            tvCurrentBid.setText(String.valueOf(productStartingPrice));
            product_title.setText(productTitle);
            fetchHighestBid(productStartingPrice, listingNo);
            startCountDownTimer();
        }

        topCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog myDialog = new Dialog(ProductDetailsActivity.this);
                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myDialog.setContentView(R.layout.bottom_sheet_layout);

//                TextView tvNoBidsSum = myDialog.findViewById(R.id.tvNoBidsSum);
                TextView tvQuantitySum = myDialog.findViewById(R.id.tvQuantitySum);
                TextView tvListingNoSum = myDialog.findViewById(R.id.tvListingNoSum);
                TextView tvOpenDateSum = myDialog.findViewById(R.id.tvOpenDateSum);
                TextView tvCloseDateSum = myDialog.findViewById(R.id.tvCloseDateSum);

                // Adjust the date format to match your needs
                displayDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault()); // Don't change this format, Database issues

                if (productItemModel != null) {
//                    tvNoBidsSum.setText(String.valueOf(productItemModel.getBid_count()));
                    tvQuantitySum.setText(String.valueOf(productItemModel.getQuantity()));

                    Log.d("ListingIssue", String.valueOf(listingNo));
                    tvListingNoSum.setText(String.valueOf(listingNo));

                    try {
                        // Parse the String dates to Date objects
                        Date openDate = inputDateFormat.parse(String.valueOf(productItemModel.getOpen_date()));
                        Date closeDate = inputDateFormat.parse(String.valueOf(productItemModel.getClose_date()));

                        // Format the Date objects to the desired format
                        assert openDate != null && closeDate != null;
                        String formattedOpenDate = displayDateFormat.format(openDate);
                        String formattedCloseDate = displayDateFormat.format(closeDate);

                        tvOpenDateSum.setText(formattedOpenDate);
                        tvCloseDateSum.setText(formattedCloseDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    myDialog.show();
                    Objects.requireNonNull(myDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    myDialog.getWindow().setGravity(Gravity.BOTTOM);
                }
            }
        });

        btnPlaceBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the entered bidding price is valid
                if (edMaxBid.getText().toString().isEmpty()) {
                    Toast.makeText(ProductDetailsActivity.this, "Please enter a valid bid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                biddingPrice = Double.parseDouble(edMaxBid.getText().toString());
                Date currentDateTime = new Date();
                Timestamp timestamp = new Timestamp(currentDateTime);

                currentBid = Double.parseDouble(tvCurrentBid.getText().toString());

                String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                // Fetch the available balance of the current user
                if (currentUserId != null) {
                    db.collection("users").document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                availableBalance = documentSnapshot.getDouble("availableBalance");

                                if (availableBalance >= 0.0) {
                                    // Check bidding conditions
                                    if (biddingPrice > productStartingPrice && biddingPrice > currentBid) {
                                        if (availableBalance >= biddingPrice) {
                                            // Create a map to store the bidding details
                                            final HashMap<String, Object> biddingMap = new HashMap<>();
                                            biddingMap.put("productTitle", productTitle);
                                            biddingMap.put("productListingNo", listingNo);
                                            biddingMap.put("biddingPrice", biddingPrice);
                                            biddingMap.put("biddingTime", timestamp);
                                            biddingMap.put("bidderImg", R.drawable.ic_launcher_background);

                                            // Add bidder details (name or email)
                                            String bidderName = auth.getCurrentUser().getEmail();
                                            String bidderUserName = documentSnapshot.getString("userName");
//                                            if (bidderName == null || bidderName.isEmpty()) {
//                                                // If display name is not set, fallback to email
//                                                bidderName = auth.getCurrentUser().getEmail();
//                                            }
                                            biddingMap.put("bidderName", bidderName);
                                            biddingMap.put("bidderUserName", bidderUserName);

                                            // Log the bidder's name for debugging
                                            Log.d("FirestoreQuery", "Bidder name: " + bidderName);

                                            // Save the bid to Firestore under the biddingList collection
                                            db.collection("biddingList")
                                                    .document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                                                    .collection("User")
                                                    .add(biddingMap)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(ProductDetailsActivity.this, "Bidding Successful!", Toast.LENGTH_SHORT).show();
                                                                finish(); // Finish the activity
                                                            } else {
                                                                Toast.makeText(ProductDetailsActivity.this, "Error in placing bid, please try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            // Insufficient balance
                                            Toast.makeText(ProductDetailsActivity.this, "Insufficient Balance! Please add balance to your account", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Invalid bidding approach (bid is less than required amounts)
                                        Snackbar snackbar = Snackbar.make(productDetailsLayout, "Invalid bidding approach!", Snackbar.LENGTH_SHORT);
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
                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, "Unable to fetch balance", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProductDetailsActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProductDetailsActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "No user found! Please login first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        bidHistoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fetchBidHistory(listingNo);

                AutoTransition autoTransition = new AutoTransition();
                if (bidHistoryCol.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(bidHistoryCardView, autoTransition);
                    bidHistoryCol.setVisibility(View.VISIBLE);
                    imgArrowHistory.setImageResource(R.drawable.icon_drop_up);
                } else {
                    TransitionManager.beginDelayedTransition(bidHistoryCardView, autoTransition);
                    bidHistoryCol.setVisibility(View.GONE);
                    imgArrowHistory.setImageResource(R.drawable.icon_drop_down);
                }
            }
        });

        bidDescCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AutoTransition autoTransition = new AutoTransition();
                if (bidDetailCol.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(bidDescCardView, autoTransition);
                    bidDetailCol.setVisibility(View.VISIBLE);
                    imgArrowDetail.setImageResource(R.drawable.icon_drop_up);
                } else {
                    TransitionManager.beginDelayedTransition(bidDescCardView, autoTransition);
                    bidDetailCol.setVisibility(View.GONE);
                    imgArrowDetail.setImageResource(R.drawable.icon_drop_down);
                }
            }
        });
    }

    private void startCountDownTimer() {
        countdownRunnable = new Runnable() {
            @Override
            public void run() {
                updateCountdownTimer();
                countDownHandler.postDelayed(this, 1000); // Update every second
            }
        };
        countDownHandler.post(countdownRunnable);
    }

    private void updateCountdownTimer() {
        Date currentDateTime = new Date();
        Date openDate = productItemModel.getOpen_date();
        Date closeDate = productItemModel.getClose_date();

        String countdownText;

        if (currentDateTime.before(openDate)) {
            // Auction hasn't started yet
            countdownText = getCountDown(currentDateTime, openDate);
            isToastShown = false;
            tvAuctionStatus.setText("Auction Starts");
            placeBidLayout.setVisibility(View.GONE);
        } else if (currentDateTime.before(closeDate)) {
            // Auction is open, countdown until it closes
            countdownText = getCountDown(currentDateTime, closeDate);
            isToastShown = false;
            tvAuctionStatus.setText("Auction Ends");
            placeBidLayout.setVisibility(View.VISIBLE);
        } else {
            // Auction has closed
            countdownText = "00d 00h 00m 00s";
            if (!isToastShown) {
                Toast.makeText(ProductDetailsActivity.this, "Auction has ended", Toast.LENGTH_SHORT).show();
                tvAuctionStatus.setText("Auction Ended");
                isToastShown = true;
            }
            placeBidLayout.setVisibility(View.GONE);
            countDownHandler.removeCallbacks(countdownRunnable); // Stop the countdown
        }

        runOnUiThread(() -> tvCountTimer.setText(countdownText));
    }

    private static String getCountDown(Date startDate, Date endDate) {
        long diffInMillis = endDate.getTime() - startDate.getTime();

        long days = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffInMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis) % 60;

        return String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }


    private void fetchHighestBid(double startingPrice, long listingNo) {

        db.collectionGroup("User")
                .whereEqualTo("productListingNo", listingNo)
                .orderBy("biddingPrice", Query.Direction.DESCENDING) // Get the highest bid
                .limit(1) // Limit to the highest bid only
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            Log.d("FirestoreQuery", "Query completed successfully");

                            if (result != null && !result.isEmpty()) {
                                // Get the highest bid
                                DocumentSnapshot highestBid = result.getDocuments().get(0);
                                double highestBiddingPrice = highestBid.getDouble("biddingPrice");

                                Log.d("FirestoreQuery", "Highest bid found: " + highestBiddingPrice);
                                // Update the UI with the highest bid value
                                tvCurrentBid.setText(String.valueOf(highestBiddingPrice));
                            } else {
                                Log.d("FirestoreQuery", "No bids found, showing starting price: " + startingPrice);
                                // No bid found for this product, show starting price
                                tvCurrentBid.setText(String.valueOf(startingPrice));
                            }
                        } else {
                            // Handle errors
                            Log.e("FirestoreError", "Error fetching bids: " + task.getException());
                            // If there's an error, default to starting price
                            tvCurrentBid.setText(String.valueOf(startingPrice));
                        }
                    }
                });

    }

    private void fetchBidHistory(long listingNo) {
        db.collectionGroup("User")
                .whereEqualTo("productListingNo", listingNo)
                .orderBy("biddingPrice", Query.Direction.DESCENDING) // Get bids in descending order
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<BidHistoryLVModel> bidList = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                // Extract data
                                String bidderName = doc.getString("bidderName"); // Assuming you store this
                                String bidderUserName = doc.getString("bidderUserName"); // Assuming you store this
                                Double bidPrice = doc.getDouble("biddingPrice");
                                Timestamp biddingTime = doc.getTimestamp("biddingTime");
                                String bidderImgUrl = doc.getString("bidderImgUrl");

                                // Make sure bidPrice is not null
                                if (bidPrice != null && bidderUserName != null) {
                                    // Add the bid to the list
                                    if (bidderUserName != null) {
                                        bidList.add(new BidHistoryLVModel(bidderUserName, bidPrice, biddingTime, bidderImgUrl));
                                    } else {
                                        bidList.add(new BidHistoryLVModel(bidderName, bidPrice, biddingTime, bidderImgUrl));
                                    }
                                }
                            }
                            // Update ListView with the bid data if the bidList is not empty
                            if (!bidList.isEmpty()) {
                                bidHistoryAdapter.setBidHistory(bidList);

                                // Notify the adapter that the data set has changed
                                bidHistoryAdapter.notifyDataSetChanged();
                            } else {
                                Log.d("FirestoreQuery", "No bid history found for the listing number: " + listingNo);
                            }

                        } else {
                            Log.e("FirestoreError", "Error fetching bids: " + task.getException());
                        }
                    }
                });
    }
}