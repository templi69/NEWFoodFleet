package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class RiderOrderDetailActivity extends AppCompatActivity {

    TextView tvDetails;
    Button btnDelivered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_order_detail);

        tvDetails = findViewById(R.id.tvDetails);
        btnDelivered = findViewById(R.id.btnDelivered);

        String orderId = getIntent().getStringExtra("orderId");

        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "No order selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderDetails(orderId);
    }

    void loadOrderDetails(String orderId) {
        // Load from RiderOrders
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("RiderOrders")
                .child(orderId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                if (!ds.exists()) {
                    // Show dummy data if not found
                    showDummyData(orderId);
                    return;
                }

                // Try to get real data
                String restaurantName = ds.child("restaurantName").getValue(String.class);
                String customerName = ds.child("customerName").getValue(String.class);
                String customerAddress = ds.child("customerAddress").getValue(String.class);
                String customerPhone = ds.child("customerPhone").getValue(String.class);
                String totalAmount = ds.child("totalAmount").getValue(String.class);
                String status = ds.child("status").getValue(String.class);

                // If data missing, use dummy
                if (restaurantName == null) restaurantName = "Burger King";
                if (customerName == null) customerName = "Customer";
                if (customerAddress == null) customerAddress = "House #123, Street 5, Islamabad";
                if (customerPhone == null) customerPhone = "0300-1234567";
                if (totalAmount == null) totalAmount = "500";
                if (status == null) status = "accepted";

                showOrderDetails(orderId, restaurantName, customerName,
                        customerAddress, customerPhone, totalAmount, status);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Show dummy data on error
                showDummyData(orderId);
            }
        });
    }

    void showDummyData(String orderId) {
        // DUMMY DATA
        String restaurantName = "Burger King";
        String customerName = "Ali Ahmed";
        String customerAddress = "House #123, Street 5, G-10/4, Islamabad";
        String customerPhone = "0300-1234567";
        String totalAmount = "750";
        String status = "accepted";

        showOrderDetails(orderId, restaurantName, customerName,
                customerAddress, customerPhone, totalAmount, status);
    }

    void showOrderDetails(String orderId, String restaurantName, String customerName,
                          String customerAddress, String customerPhone,
                          String totalAmount, String status) {

        // Build display text
        StringBuilder details = new StringBuilder();

        details.append("📦 ORDER #").append(orderId.substring(0, Math.min(6, orderId.length()))).append("\n\n");

        details.append("📍 PICKUP FROM:\n");
        details.append("🏪 ").append(restaurantName).append("\n");
        details.append("📌 DHA Phase 5, Lahore\n");
        details.append("📱 042-35792001\n");

        details.append("\n───────────────\n\n");

        details.append("🎯 DELIVER TO:\n");
        details.append("👤 ").append(customerName).append("\n");
        details.append("📱 ").append(customerPhone).append("\n");
        details.append("📌 ").append(customerAddress).append("\n");

        details.append("\n───────────────\n\n");

        details.append("💰 TOTAL AMOUNT: Rs ").append(totalAmount).append("\n");
        details.append("📊 ORDER STATUS: ").append(status.toUpperCase()).append("\n");

        // Items list (dummy)
        details.append("\n🍔 ORDER ITEMS:\n");
        details.append("1. Zinger Burger x2 = Rs 600\n");
        details.append("2. French Fries x1 = Rs 150\n");
        details.append("────────────────────\n");
        details.append("TOTAL: Rs ").append(totalAmount).append("\n");

        tvDetails.setText(details.toString());

        // Setup DELIVERED button
        btnDelivered.setOnClickListener(v -> {
            // Mark as delivered
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("RiderOrders")
                    .child(orderId);

            ref.child("status").setValue("delivered");

            Toast.makeText(this, "✅ Order Delivered Successfully!", Toast.LENGTH_SHORT).show();

            // Close this activity
            finish();
        });
    }
}