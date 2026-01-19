package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

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

            String foodName = etFoodName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();


            if (foodName.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String foodId = restaurantRef.child(uid)
                    .child("menu").push().getKey();

            HashMap<String, Object> foodMap = new HashMap<>();
            foodMap.put("itemName", foodName);
            foodMap.put("price", price);

            restaurantRef.child(uid)
                    .child("menu")
                    .child(foodId)
                    .setValue(foodMap)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Food Added Successfully",
                                    Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    });
        });
    }
}
