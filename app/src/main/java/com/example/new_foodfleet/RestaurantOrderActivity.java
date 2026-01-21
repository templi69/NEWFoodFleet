package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class RestaurantOrderActivity extends AppCompatActivity {

    ListView listOrders;
    Button btnAcceptOrder;

    ArrayList<String> orderList = new ArrayList<>();
    ArrayList<String> orderIds = new ArrayList<>();

    ArrayAdapter<String> adapter;

    DatabaseReference ordersRef;
    String restaurantId = "restaurant1"; // 🔴 replace with actual logged-in restaurant ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        listOrders = findViewById(R.id.listOrders);
        btnAcceptOrder = findViewById(R.id.btnAcceptOrder);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                orderList
        );
        listOrders.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantId)
                .child("orders");

        loadOrders();

        btnAcceptOrder.setOnClickListener(v -> acceptFirstOrder());
    }

    void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                orderList.clear();
                orderIds.clear();

                for (DataSnapshot orderSnap : snapshot.getChildren()) {

                    String orderId = orderSnap.getKey();
                    String status = orderSnap.child("status").getValue(String.class);

                    if (status == null || !status.equals("pending")) continue;

                    int totalItems = 0;

                    for (DataSnapshot item : orderSnap.child("items").getChildren()) {
                        Long qty = item.child("qty").getValue(Long.class);
                        if (qty != null) totalItems += qty;
                    }

                    orderIds.add(orderId);
                    orderList.add("Order ID: " + orderId +
                            "\nItems: " + totalItems +
                            "\nStatus: Pending");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RestaurantOrderActivity.this,
                        "Failed to load orders",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void acceptFirstOrder() {

        if (orderIds.isEmpty()) {
            Toast.makeText(this, "No pending orders", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = orderIds.get(0);

        // 1️⃣ Update status in restaurant
        ordersRef.child(orderId)
                .child("status")
                .setValue("accepted");

        // 2️⃣ Send order to Riders
        DatabaseReference riderOrdersRef = FirebaseDatabase.getInstance()
                .getReference("RiderOrders")
                .child(orderId);

        ordersRef.child(orderId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        riderOrdersRef.setValue(snapshot.getValue());
                        Toast.makeText(RestaurantOrderActivity.this,
                                "Order accepted & sent to rider",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }
}
