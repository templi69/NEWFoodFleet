package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RiderOrderDetailActivity extends AppCompatActivity {

    TextView tvDetails;
    Button btnDelivered;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_rider_order_detail);

        tvDetails = findViewById(R.id.tvDetails);
        btnDelivered = findViewById(R.id.btnDelivered);

        // REQUIRED extras
        String userId = getIntent().getStringExtra("userId");
        String orderId = getIntent().getStringExtra("orderId");

        DatabaseReference userRef =
                FirebaseDatabase.getInstance().getReference("Users").child(userId);

        DatabaseReference orderRef =
                userRef.child("orders").child(orderId);

        // Load order + user info
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnap) {

                String name = userSnap.child("name").getValue(String.class);

                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot orderSnap) {

                        String status = orderSnap.child("status").getValue(String.class);
                        String items = orderSnap.child("items").getValue().toString();

                        tvDetails.setText(
                                "Customer: " + name + "\n" +
                                        "Items: " + items + "\n" +
                                        "Status: " + status
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Mark order delivered
        btnDelivered.setOnClickListener(v -> {
            orderRef.child("status").setValue("Delivered");
            Toast.makeText(this, "Order Delivered", Toast.LENGTH_SHORT).show();
        });
    }
}
