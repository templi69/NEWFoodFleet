package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserDashboard extends AppCompatActivity {

    Button btnViewCart, btnLogout;
    ListView listMenu;

    ArrayList<String> restaurantList;
    ArrayAdapter<String> adapter;

    DatabaseReference restaurantRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        // Bind XML views
        btnViewCart = findViewById(R.id.btnViewCart);
        btnLogout = findViewById(R.id.btnLogout);
        listMenu = findViewById(R.id.listMenu);

        // List setup
        restaurantList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                restaurantList
        );
        listMenu.setAdapter(adapter);

        // Firebase reference
        restaurantRef = FirebaseDatabase.getInstance()
                .getReference("restaurant");

        // Fetch restaurants from Firebase
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restaurantList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String restaurantName;

                    // Case 1: restaurant stored as key
                    restaurantName = data.getKey();

                    // Case 2: restaurant stored with "name" field
                    if (data.child("name").exists()) {
                        restaurantName = data.child("name").getValue(String.class);
                    }

                    restaurantList.add(restaurantName);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDashboard.this,
                        "Failed to load restaurants",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // List item click
        listMenu.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRestaurant = restaurantList.get(position);
            Toast.makeText(this,
                    selectedRestaurant + " selected",
                    Toast.LENGTH_SHORT).show();

            // Example:
            // Intent intent = new Intent(this, MenuActivity.class);
            // intent.putExtra("restaurant", selectedRestaurant);
            // startActivity(intent);
        });

        // View Cart button
        btnViewCart.setOnClickListener(v ->
                Toast.makeText(this, "View Cart clicked", Toast.LENGTH_SHORT).show()
        );

        // Logout button
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish(); // or go to LoginActivity
        });
    }

}