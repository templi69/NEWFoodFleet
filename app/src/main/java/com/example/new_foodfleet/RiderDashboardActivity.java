package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RiderDashboardActivity extends AppCompatActivity {

    ListView listOrders;
    Button btnAccept;

    ArrayList<String> orders = new ArrayList<>();   // Text shown in list
    ArrayList<String> orderIds = new ArrayList<>(); // Order IDs
    int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        listOrders = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAccept);

        // Load DUMMY orders
        loadDummyOrders();

        // Select order
        listOrders.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            Toast.makeText(this, "Selected order #" + (position + 1), Toast.LENGTH_SHORT).show();
        });

        // Accept order button
        btnAccept.setOnClickListener(v -> {
            if (selectedIndex == -1) {
                Toast.makeText(this, "Please select an order first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedIndex >= orderIds.size()) {
                Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderId = orderIds.get(selectedIndex);

            Toast.makeText(this, "✅ Order Accepted: " + orderId, Toast.LENGTH_SHORT).show();

            // Open order details
            Intent intent = new Intent(
                    RiderDashboardActivity.this,
                    RiderOrderDetailActivity.class
            );
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });
    }

    void loadDummyOrders() {
        // Clear previous data
        orders.clear();
        orderIds.clear();

        // DUMMY ORDERS DATA
        String[][] dummyOrders = {
                {"ORD001", "Burger King", "Ali Ahmed", "G-10/4, Islamabad", "0300-1234567", "750"},
                {"ORD002", "Pizza Hut", "Sara Khan", "DHA Phase 5, Lahore", "0312-9876543", "1200"},
                {"ORD003", "KFC", "Ahmed Raza", "Bahria Town, Rawalpindi", "0333-4567890", "950"},
                {"ORD004", "McDonald's", "Fatima Noor", "F-7, Islamabad", "0345-1122334", "650"}
        };

        for (String[] order : dummyOrders) {
            String orderId = order[0];
            String restaurant = order[1];
            String customer = order[2];
            String address = order[3];
            String phone = order[4];
            String amount = order[5];

            // Build display text
            String displayText = " " + orderId +
                    "\n " + restaurant +
                    "\n " + customer +
                    "\n " + (address.length() > 25 ? address.substring(0, 25) + "..." : address) +
                    "\n " + phone +
                    "\n Rs " + amount +
                    "\n Status: Accepted";

            orders.add(displayText);
            orderIds.add(orderId);
        }

        // Update list adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                RiderDashboardActivity.this,
                android.R.layout.simple_list_item_single_choice,
                orders
        );

        listOrders.setAdapter(adapter);

        // Show count
        Toast.makeText(RiderDashboardActivity.this,
                orders.size() + " orders available",
                Toast.LENGTH_SHORT).show();
    }
}