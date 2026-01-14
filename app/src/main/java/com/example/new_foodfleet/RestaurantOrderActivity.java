package com.example.new_foodfleet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RestaurantOrderActivity extends AppCompatActivity {

    DatabaseReference ordersRef;
    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);

        restaurantId = FirebaseAuth.getInstance().getUid();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        findViewById(R.id.btnAcceptOrder).setOnClickListener(v -> {


            ordersRef.child("order1").child("restaurantId")
                    .setValue(restaurantId);

            ordersRef.child("order1").child("status")
                    .setValue("Accepted");
        });
    }
}
