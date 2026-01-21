package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class RestaurantOrderActivity extends AppCompatActivity {

    ListView listView;
    Button btnAccept;

    ArrayList<String> orders = new ArrayList<>();
    ArrayAdapter<String> adapter;

    DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        listView = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAcceptOrder);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                orders);
        listView.setAdapter(adapter);

        // Get restaurant ID
        String restaurantId = getRestaurantId();

        if (restaurantId == null || restaurantId.isEmpty()) {
            Toast.makeText(this, "Restaurant not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ordersRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantId)
                .child("orders");

        loadOrders();

        btnAccept.setOnClickListener(v -> {
            acceptOrder();
        });
    }

    // Method to get restaurant ID
    String getRestaurantId() {
        // From Firebase Auth (logged in user)
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // From intent (if coming from login)
        String fromIntent = getIntent().getStringExtra("restaurantId");
        if (fromIntent != null && !fromIntent.isEmpty()) {
            return fromIntent;
        }

        // If not logged in and no intent, show error
        return "";
    }

    void loadOrders() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orders.clear();

                if (!snapshot.exists()) {
                    orders.add("No orders yet");
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String orderId = orderSnap.getKey();
                    String status = orderSnap.child("status").getValue(String.class);

                    if (status != null && status.equals("pending")) {
                        String customer = orderSnap.child("customerEmail").getValue(String.class);
                        Long total = orderSnap.child("totalAmount").getValue(Long.class);

                        String display = "Order: " + orderId.substring(0, 6) +
                                "\nCustomer: " + (customer != null ? customer : "Unknown") +
                                "\nTotal: Rs " + (total != null ? total : 0) +
                                "\nStatus: " + status;

                        orders.add(display);
                    }
                }

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
        if (orders.isEmpty() || orders.get(0).equals("No pending orders")) {
            Toast.makeText(this, "No orders to accept", Toast.LENGTH_SHORT).show();
            return;
        }

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    String status = orderSnap.child("status").getValue(String.class);

                    if (status != null && status.equals("pending")) {
                        String orderId = orderSnap.getKey();

                        ordersRef.child(orderId).child("status").setValue("accepted");

                        DatabaseReference riderRef = FirebaseDatabase.getInstance()
                                .getReference("RiderOrders")
                                .child(orderId);

                        riderRef.setValue(orderSnap.getValue());

                        Toast.makeText(RestaurantOrderActivity.this,
                                "Order accepted and sent to rider",
                                Toast.LENGTH_LONG).show();

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