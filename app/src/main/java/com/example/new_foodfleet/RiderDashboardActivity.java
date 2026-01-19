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

    ArrayList<String> orders = new ArrayList<>();      // Order display text
    ArrayList<String> orderIds = new ArrayList<>();    // Order IDs from Firebase
    int selectedIndex = -1;

    DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        listOrders = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAccept);

        orderRef = FirebaseDatabase.getInstance().getReference("Orders");

        //  Load only restaurant accepted orders
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orders.clear();
                orderIds.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = ds.child("status").getValue(String.class);

                    // Only show orders accepted by restaurant
                    if ("Accepted".equals(status)) {
                        orderIds.add(ds.getKey());

                        String restaurant = ds.child("restaurantName").getValue(String.class);
                        String address = ds.child("customerAddress").getValue(String.class);

                        orders.add(restaurant + "\n" + address);
                    }
                }

                // Show in single-choice ListView
                listOrders.setAdapter(new ArrayAdapter<>(
                        RiderDashboardActivity.this,
                        android.R.layout.simple_list_item_single_choice,
                        orders
                ));
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        //  Select an order from list
        listOrders.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;

            // Open RiderOrderDetailActivity on click
            String orderId = orderIds.get(position);
            Intent intent = new Intent(RiderDashboardActivity.this,
                    RiderOrderDetailActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });

        //  Accept button logic
        btnAccept.setOnClickListener(v -> {
            if (selectedIndex == -1) {
                Toast.makeText(this, "Select an order first", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderId = orderIds.get(selectedIndex);

            // Update order status in Firebase
            orderRef.child(orderId).child("status").setValue("Picked");
            orderRef.child(orderId).child("riderId").setValue("demo_rider"); // replace with UID later

            Toast.makeText(this, "Order Accepted", Toast.LENGTH_SHORT).show();
        });
    }
}
