package com.example.new_foodfleet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class RestaurantOrderActivity extends AppCompatActivity {

    ListView listView;
    Button btnAccept;

    ArrayList<String> orders = new ArrayList<>();
    ArrayAdapter<String> adapter;

    DatabaseReference ordersRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        // Find views
        listView = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAcceptOrder);

        // Setup adapter
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                orders);
        listView.setAdapter(adapter);

        // Firebase reference
        String restaurantId = "restaurant1"; // Your restaurant ID
        ordersRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantId)
                .child("orders");

        // Load orders
        loadOrders();

        // Accept button
        btnAccept.setOnClickListener(v -> {
            acceptOrder();
        });
    }

    void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orders.clear();

                // Check if data exists
                if (!snapshot.exists()) {
                    orders.add("No orders yet");
                    adapter.notifyDataSetChanged();
                    return;
                }

                // Loop through orders
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String orderId = orderSnap.getKey();
                    String status = orderSnap.child("status").getValue(String.class);

                    // Show only pending orders
                    if (status != null && status.equals("pending")) {
                        String customer = orderSnap.child("customerEmail").getValue(String.class);
                        Long total = orderSnap.child("totalAmount").getValue(Long.class);

                        // Create display string
                        String display = "Order: " + orderId.substring(0, 6) +
                                "\nCustomer: " + (customer != null ? customer : "Unknown") +
                                "\nTotal: Rs " + (total != null ? total : 0) +
                                "\nStatus: " + status;

                        orders.add(display);
                    }
                }

                // If no pending orders
                if (orders.isEmpty()) {
                    orders.add("No pending orders");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RestaurantOrderActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void acceptOrder() {
        // Check if there are orders
        if (orders.isEmpty() || orders.get(0).equals("No pending orders")) {
            Toast.makeText(this, "No orders to accept", Toast.LENGTH_SHORT).show();
            return;
        }

        // Find first pending order
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String status = orderSnap.child("status").getValue(String.class);

                    if (status != null && status.equals("pending")) {
                        String orderId = orderSnap.getKey();

                        // 1. Update status in restaurant
                        ordersRef.child(orderId).child("status").setValue("accepted");

                        // 2. Send to rider
                        DatabaseReference riderRef = FirebaseDatabase.getInstance()
                                .getReference("RiderOrders")
                                .child(orderId);

                        // Copy order data to rider
                        riderRef.setValue(orderSnap.getValue());

                        Toast.makeText(RestaurantOrderActivity.this,
                                "Order accepted and sent to rider",
                                Toast.LENGTH_LONG).show();

                        // Stop after accepting one order
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RestaurantOrderActivity.this,
                        "Failed to accept order",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}