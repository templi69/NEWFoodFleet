package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RestaurantOrderActivity extends AppCompatActivity {

    ListView listView;
    Button btnAccept;

    ArrayList<String> orders = new ArrayList<>();
    ArrayAdapter<String> adapter;

    DatabaseReference ordersRef;

    String restaurantName = "Unknown";
    String userName = "Unknown";
    String userPhone = "Unknown";

    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchUserData(userId);

        listView = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAcceptOrder);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                orders);
        listView.setAdapter(adapter);

        restaurantId = getRestaurantId();

        if (restaurantId == null || restaurantId.isEmpty()) {
            Toast.makeText(this, "Restaurant not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // IMPORTANT: Fetch restaurant name before loading orders
        fetchRestaurantName(restaurantId);

        ordersRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantId)
                .child("orders");

        loadOrders();

        btnAccept.setOnClickListener(v -> {
            acceptOrder();
        });
    }

    String getRestaurantId() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        String fromIntent = getIntent().getStringExtra("restaurantId");
        if (fromIntent != null && !fromIntent.isEmpty()) {
            return fromIntent;
        }

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

    void fetchRestaurantName(String restaurantId) {
        DatabaseReference restRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantId);

        restRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    restaurantName = name != null ? name : "Unknown";
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }
    void fetchUserData(String userId) {
        DatabaseReference riderRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId);

        riderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String.class);
                    userPhone = snapshot.child("phone").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
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

                        // Update status in restaurant node
                        ordersRef.child(orderId).child("status").setValue("accepted");

                        // Get customer id from order
                        String customerId = orderSnap.child("userId").getValue(String.class);

                        // Fetch customer data from Users node
                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(customerId);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                String customerName = userSnapshot.child("name").getValue(String.class);
                                String customerPhone = userSnapshot.child("phone").getValue(String.class);

                                Map<String, Object> orderData = new HashMap<>();

                                orderData.put("status", "accepted");
                                orderData.put("orderId", orderId);
                                orderData.put("customerEmail",
                                        orderSnap.child("customerEmail").getValue());
                                orderData.put("customerName", customerName);
                                orderData.put("customerPhone", customerPhone);
                                orderData.put("customerAddress",
                                        orderSnap.child("customerAddress").getValue());
                                orderData.put("totalAmount",
                                        orderSnap.child("totalAmount").getValue());

                                orderData.put("restaurantName", restaurantName);

                                DatabaseReference riderRef = FirebaseDatabase.getInstance()
                                        .getReference("RiderOrders")
                                        .child(orderId);

                                riderRef.setValue(orderData);

                                Toast.makeText(RestaurantOrderActivity.this,
                                        "Order accepted and sent to rider",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) { }
                        });

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
