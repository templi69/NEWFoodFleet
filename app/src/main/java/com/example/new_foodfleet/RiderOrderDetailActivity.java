package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class RiderOrderDetailActivity extends AppCompatActivity {

    TextView tvDetails;
    Button btnDelivered;

    DatabaseReference orderRef;

    String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_order_detail);

        tvDetails = findViewById(R.id.tvDetails);
        btnDelivered = findViewById(R.id.btnDelivered);

        orderId = getIntent().getStringExtra("orderId");

        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "No order selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        orderRef = FirebaseDatabase.getInstance()
                .getReference("RiderOrders")
                .child(orderId);

        loadOrderDetails();

        btnDelivered.setOnClickListener(v -> markAsDelivered());
    }

    // 🔹 Load order details (REAL DATA ONLY)
    void loadOrderDetails() {

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {

                if (!ds.exists()) {
                    Toast.makeText(RiderOrderDetailActivity.this,
                            "Order not found",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                String restaurantName =
                        ds.child("restaurantName").getValue(String.class);
                String customerName =
                        ds.child("customerName").getValue(String.class);
                String customerAddress =
                        ds.child("customerAddress").getValue(String.class);
                String customerPhone =
                        ds.child("customerPhone").getValue(String.class);
                Object totalAmountObj =
                        ds.child("totalAmount").getValue();
                String status =
                        ds.child("status").getValue(String.class);

                // Safe defaults
                if (restaurantName == null) restaurantName = "Restaurant";
                if (customerName == null) customerName = "Customer";
                if (customerAddress == null) customerAddress = "Address not available";
                if (customerPhone == null) customerPhone = "N/A";
                if (status == null) status = "accepted";

                String totalAmount = String.valueOf(
                        totalAmountObj != null ? totalAmountObj : 0
                );

                showOrderDetails(
                        restaurantName,
                        customerName,
                        customerAddress,
                        customerPhone,
                        totalAmount,
                        status
                );
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RiderOrderDetailActivity.this,
                        "Failed to load order",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Display order details
    void showOrderDetails(String restaurantName,
                          String customerName,
                          String customerAddress,
                          String customerPhone,
                          String totalAmount,
                          String status) {

        StringBuilder details = new StringBuilder();

        details.append("📦 ORDER #")
                .append(orderId.substring(0, Math.min(6, orderId.length())))
                .append("\n\n");

        details.append("📍 PICKUP FROM:\n");
        details.append("🏪 ").append(restaurantName).append("\n\n");

        details.append("🎯 DELIVER TO:\n");
        details.append("👤 ").append(customerName).append("\n");
        details.append("📱 ").append(customerPhone).append("\n");
        details.append("📌 ").append(customerAddress).append("\n\n");

        details.append("💰 TOTAL AMOUNT: Rs ").append(totalAmount).append("\n");
        details.append("📊 ORDER STATUS: ").append(status.toUpperCase()).append("\n");

        tvDetails.setText(details.toString());

        // Disable button if already delivered
        if ("delivered".equals(status)) {
            btnDelivered.setEnabled(false);
            btnDelivered.setText("DELIVERED");
        }
    }

    // 🔹 Mark order as delivered
    void markAsDelivered() {

        orderRef.child("status").setValue("delivered")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,
                            "✅ Order Delivered Successfully!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to update status",
                                Toast.LENGTH_SHORT).show()
                );
    }
}
