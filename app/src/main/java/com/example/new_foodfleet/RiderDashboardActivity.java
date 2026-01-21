package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class RiderDashboardActivity extends AppCompatActivity {

    ListView listOrders;
    Button btnAccept;

    ArrayList<String> orders = new ArrayList<>();
    ArrayList<String> orderIds = new ArrayList<>();
    ArrayList<String> userIds = new ArrayList<>();

    int selectedIndex = -1;

    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        listOrders = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAccept);

        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Load accepted orders
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orders.clear();
                orderIds.clear();
                userIds.clear();
                selectedIndex = -1;

                for (DataSnapshot userSnap : snapshot.getChildren()) {

                    String userId = userSnap.getKey();
                    DataSnapshot ordersSnap = userSnap.child("orders");

                    for (DataSnapshot orderSnap : ordersSnap.getChildren()) {

                        String status = orderSnap.child("status").getValue(String.class);

                        if ("Accepted".equalsIgnoreCase(status)) {

                            String orderId = orderSnap.getKey();
                            String userName = userSnap.child("name").getValue(String.class);

                            orders.add(
                                    "Customer: " + userName + "\n" +
                                            "Order ID: " + orderId
                            );

                            orderIds.add(orderId);
                            userIds.add(userId);
                        }
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        RiderDashboardActivity.this,
                        android.R.layout.simple_list_item_single_choice,
                        orders
                );
                listOrders.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        listOrders.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            Toast.makeText(this, "Order Selected", Toast.LENGTH_SHORT).show();
        });

        btnAccept.setOnClickListener(v -> {

            if (selectedIndex == -1) {
                Toast.makeText(this, "Select an order first", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderId = orderIds.get(selectedIndex);
            String userId = userIds.get(selectedIndex);

            DatabaseReference orderRef =
                    usersRef.child(userId).child("orders").child(orderId);

            orderRef.child("status").setValue("Picked");
            orderRef.child("riderId").setValue("demo_rider");

            Toast.makeText(this, "Order Picked", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(
                    RiderDashboardActivity.this,
                    RiderOrderDetailActivity.class
            );
            intent.putExtra("orderId", orderId);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }
}
