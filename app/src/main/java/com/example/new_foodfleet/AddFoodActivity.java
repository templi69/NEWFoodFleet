package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodActivity extends AppCompatActivity {

    EditText etFoodName, etPrice;
    Button btnAddFood;

    DatabaseReference restaurantRef;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        etFoodName = findViewById(R.id.etFoodName);
        etPrice = findViewById(R.id.etPrice);
        btnAddFood = findViewById(R.id.btnAddFood);

        uid = FirebaseAuth.getInstance().getUid();
        restaurantRef = FirebaseDatabase.getInstance().getReference("Restaurants");

        btnAddFood.setOnClickListener(v -> {
            String foodName = etFoodName.getText().toString();
            String price = etPrice.getText().toString();

            String foodId = restaurantRef.child(uid)
                    .child("menu").push().getKey();

            restaurantRef.child(uid).child("menu").child(foodId)
                    .child("itemName").setValue(foodName);

            restaurantRef.child(uid).child("menu").child(foodId)
                    .child("price").setValue(price);

            etFoodName.setText("");
            etPrice.setText("");
        });
    }
}
