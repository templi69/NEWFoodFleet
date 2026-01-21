package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class RiderDashboardActivity extends AppCompatActivity {

    ListView listOrders;
    Button btnAccept;

    ArrayList<String> orders = new ArrayList<>();    // Text shown in list
    ArrayList<String> orderIds = new ArrayList<>();  // Actual Firebase order IDs

    int selectedIndex = -1;

    DatabaseReference riderOrdersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        listOrders = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAccept);

        riderOrdersRef = FirebaseDatabase.getInstance()
                .getReference("RiderOrders");

        loadAcceptedOrders();

        // Select order from list
        listOrders.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            Toast.makeText(this,
                    "Selected order " + orderIds.get(position),
                    Toast.LENGTH_SHORT).show();
        });

        // Accept / Open Order
        btnAccept.setOnClickListener(v -> {

            if (selectedIndex == -1) {
                Toast.makeText(this,
                        "Please select an order first",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedIndex >= orderIds.size()) {
                Toast.makeText(this,
                        "Invalid order selection",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String orderId = orderIds.get(selectedIndex);

            // Open order details
            Intent intent = new Intent(
                    RiderDashboardActivity.this,
                    RiderOrderDetailActivity.class
            );
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });
    }

    // 🔹 Load accepted orders from Firebase
    void loadAcceptedOrders() {

        riderOrdersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                orders.clear();
                orderIds.clear();
                selectedIndex = -1;

                if (!snapshot.exists()) {
                    orders.add("No accepted orders");
                    updateList();
                    return;
                }

                for (DataSnapshot orderSnap : snapshot.getChildren()) {

                    String status = orderSnap.child("status").getValue(String.class);

                    if (status != null && status.equals("accepted")) {

                        String orderId = orderSnap.getKey();
                        String restaurant =
                                orderSnap.child("restaurantName").getValue(String.class);
                        String customer =
                                orderSnap.child("customerName").getValue(String.class);
                        String address =
                                orderSnap.child("customerAddress").getValue(String.class);
                        Object total =
                                orderSnap.child("totalAmount").getValue();

                        // Safe defaults
                        if (restaurant == null) restaurant = "Restaurant";
                        if (customer == null) customer = "Customer";
                        if (address == null) address = "Address not available";

                        String displayText =
                                "📦 Order: " + orderId.substring(0, Math.min(6, orderId.length())) +
                                        "\n🏪 " + restaurant +
                                        "\n👤 " + customer +
                                        "\n📍 " + (address.length() > 25
                                        ? address.substring(0, 25) + "..."
                                        : address) +
                                        "\n💰 Rs " + (total != null ? total : 0) +
                                        "\n📊 Status: ACCEPTED";

                        orders.add(displayText);
                        orderIds.add(orderId);
                    }
                }

                if (orders.isEmpty()) {
                    orders.add("No accepted orders");
                }

                updateList();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RiderDashboardActivity.this,
                        "Failed to load rider orders",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔹 Update ListView
    void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                RiderDashboardActivity.this,
                android.R.layout.simple_list_item_single_choice,
                orders
        );
        listOrders.setAdapter(adapter);
        listOrders.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}
