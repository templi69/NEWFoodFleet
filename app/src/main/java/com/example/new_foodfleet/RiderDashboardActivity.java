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

    ArrayList<String> orders = new ArrayList<>();   // Text shown in list
    ArrayList<String> orderIds = new ArrayList<>(); // Firebase order IDs
    int selectedIndex = -1;

    DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);

        listOrders = findViewById(R.id.listOrders);
        btnAccept = findViewById(R.id.btnAccept);

        orderRef = FirebaseDatabase.getInstance().getReference("Orders");

        //  Load only Accepted orders (by restaurant)
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orders.clear();
                orderIds.clear();
                selectedIndex = -1;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = ds.child("status").getValue(String.class);

                    if ("Accepted".equalsIgnoreCase(status)) {
                        orderIds.add(ds.getKey());

                        String restaurant = ds.child("restaurantName").getValue(String.class);
                        String address = ds.child("customerAddress").getValue(String.class);

                        orders.add(restaurant + "\n" + address);
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

        //  Select order ONLY (no navigation here)
        listOrders.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            Toast.makeText(this, "Order Selected", Toast.LENGTH_SHORT).show();
        });

        //  Accept order button
        btnAccept.setOnClickListener(v -> {
            if (selectedIndex == -1) {
                Toast.makeText(this, "Please select an order first", Toast.LENGTH_SHORT).show();
                return;
            }

            String orderId = orderIds.get(selectedIndex);

            // Update Firebase
            orderRef.child(orderId).child("status").setValue("Picked");
            orderRef.child(orderId).child("riderId").setValue("demo_rider");

            Toast.makeText(this, "Order Accepted", Toast.LENGTH_SHORT).show();

            // Open order details AFTER accept
            Intent intent = new Intent(
                    RiderDashboardActivity.this,
                    RiderOrderDetailActivity.class
            );
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });
    }
}
