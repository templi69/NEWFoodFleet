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

        String orderId = getIntent().getStringExtra("orderId");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                tvDetails.setText(
                        ds.child("customerName").getValue(String.class) + "\n" +
                                ds.child("customerAddress").getValue(String.class)
                );
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        btnDelivered.setOnClickListener(v -> {
            ref.child("status").setValue("Delivered");
            Toast.makeText(this, "Order Delivered", Toast.LENGTH_SHORT).show();
        });
    }
}
