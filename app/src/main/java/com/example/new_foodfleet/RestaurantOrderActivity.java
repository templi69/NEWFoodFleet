package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.*;

public class RestaurantOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        ListView list = findViewById(R.id.listOrders);
        ArrayList<String> orders = new ArrayList<>();

        // Load orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orders.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("customerName").getValue(String.class);
                    String total = ds.child("total").getValue(String.class);
                    orders.add(name + " - Rs" + total);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        RestaurantOrderActivity.this,
                        android.R.layout.simple_list_item_1,
                        orders
                );
                list.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RestaurantOrderActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        // Accept button (simple)
        Button btn = findViewById(R.id.btnAcceptOrder);
        btn.setOnClickListener(v -> {
            // Accept first order (for demo)
            if (!orders.isEmpty()) {
                ref.child("order1").child("status").setValue("Accepted");
                Toast.makeText(this, "Order Accepted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}