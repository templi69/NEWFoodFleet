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

    ArrayList<String> restaurantNames = new ArrayList<>();
    ArrayList<String> restaurantIds = new ArrayList<>();
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
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                restaurantNames
        );
        listMenu.setAdapter(adapter);

        // Firebase reference
        restaurantRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants");

        // Fetch restaurants from Firebase
        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restaurantNames.clear();
                restaurantIds.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String id = data.getKey();
                    String name = data.child("name").getValue(String.class);

                    if (id != null && name != null) {
                        restaurantIds.add(id);
                        restaurantNames.add(name);
                    }
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

            // Get clicked restaurant id and name
            String restaurantId = restaurantIds.get(position);
            String restaurantName = restaurantNames.get(position);

            // Send data using intent
            Intent intent = new Intent(UserDashboard.this, user_resturantmenue.class);
            intent.putExtra("restaurantId", restaurantId);
            intent.putExtra("restaurantName", restaurantName);
            startActivity(intent);
        });

        // View Cart button
        btnViewCart.setOnClickListener(v ->
                startActivity(new Intent(UserDashboard.this, usercart.class)) );

        // Logout button
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish(); // or go to LoginActivity
        });
    }

}