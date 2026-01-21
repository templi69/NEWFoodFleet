package com.example.new_foodfleet;


import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
public class RiderOrdersActivity  extends AppCompatActivity {

        ListView listRiderOrders;
        TextView tvTitle;

        ArrayList<String> orderIds = new ArrayList<>();
        ArrayAdapter<String> adapter;

        DatabaseReference riderOrdersRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_rider_orders);

            tvTitle = findViewById(R.id.tvTitle);
            listRiderOrders = findViewById(R.id.listRiderOrders);

            adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    orderIds
            );
            listRiderOrders.setAdapter(adapter);

            riderOrdersRef = FirebaseDatabase.getInstance()
                    .getReference("RiderOrders");

            loadAcceptedOrders();

            listRiderOrders.setOnItemClickListener((parent, view, position, id) -> {

                String selected = orderIds.get(position);

                // Prevent clicking dummy text
                if (selected.equals("No accepted orders")) return;

                Intent intent = new Intent(RiderOrdersActivity.this,
                        RiderOrderDetailActivity.class);

                intent.putExtra("orderId", selected);
                startActivity(intent);
            });
        }

        void loadAcceptedOrders() {
            riderOrdersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    orderIds.clear();

                    if (!snapshot.exists()) {
                        orderIds.add("No accepted orders");
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (DataSnapshot orderSnap : snapshot.getChildren()) {
                        String status = orderSnap.child("status").getValue(String.class);

                        if (status != null && status.equals("accepted")) {
                            orderIds.add(orderSnap.getKey());
                        }
                    }

                    if (orderIds.isEmpty()) {
                        orderIds.add("No accepted orders");
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(RiderOrdersActivity.this,
                            "Failed to load orders",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


