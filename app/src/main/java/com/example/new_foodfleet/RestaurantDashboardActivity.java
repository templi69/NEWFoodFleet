package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class RestaurantDashboardActivity extends AppCompatActivity {

    ListView listMenu;
    Button btnAddFood, btnViewOrders;
    ArrayList<String> menuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_dashboard);

        // Find views
        listMenu = findViewById(R.id.listMenu);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnViewOrders = findViewById(R.id.btnViewOrders);

        // Setup adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                menuList
        );
        listMenu.setAdapter(adapter);

        // Get restaurant ID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load menu from Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(uid)
                .child("menu");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                menuList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String name = ds.child("itemName").getValue(String.class);
                        String price = ds.child("price").getValue(String.class);

                        if (name != null && price != null) {
                            menuList.add(name + " - ₹" + price);
                        }
                    }
                }

                if (menuList.isEmpty()) {
                    menuList.add("No items in menu");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RestaurantDashboardActivity.this,
                        "Error loading menu", Toast.LENGTH_SHORT).show();
            }
        });

        // Button click listeners
        btnAddFood.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFoodActivity.class));
        });

        btnViewOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, RestaurantOrderActivity.class));
        });
    }
}