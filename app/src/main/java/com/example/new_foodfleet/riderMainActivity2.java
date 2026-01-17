package com.example.new_foodfleet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class riderMainActivity2 extends AppCompatActivity {
    EditText etOrderId, etPickup, etDelivery;
    Button btnUpdateStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rider_main2);
        etOrderId = findViewById(R.id.etOrderId);
        etPickup = findViewById(R.id.etPickup);
        etDelivery = findViewById(R.id.etDelivery);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String orderId = etOrderId.getText().toString().trim();
                String pickup = etPickup.getText().toString().trim();
                String delivery = etDelivery.getText().toString().trim();

                // Simple validation
                if (orderId.isEmpty() || pickup.isEmpty() || delivery.isEmpty()) {
                    Toast.makeText(riderMainActivity2.this,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Here you can update status in database (Firebase / SQLite)
                    Toast.makeText(riderMainActivity2.this,
                            "Order Delivered Successfully",
                            Toast.LENGTH_LONG).show();

                    // Clear fields after delivery
                    etOrderId.setText("");
                    etPickup.setText("");
                    etDelivery.setText("");
                }
            }
        });
    }
}