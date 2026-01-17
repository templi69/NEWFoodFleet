package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestaurantDashboardActivity extends AppCompatActivity {

    private ListView listMenu;
    private Button btnAddFood, btnViewOrders;
    private ArrayList<String> menuList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_dashboard);

        // Initialize ALL views
        listMenu = findViewById(R.id.listMenu);
        btnAddFood = findViewById(R.id.btnAddFood); // Add this button in XML
        btnViewOrders = findViewById(R.id.btnViewOrders); // Add this button in XML

        menuList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                menuList
        );

        listMenu.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(currentUser.getUid())
                .child("menu");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String name = ds.child("itemName").getValue(String.class);
                        String price = ds.child("price").getValue(String.class);

                        if (name != null && price != null) {
                            menuList.add(name + " - Rs" + price);
                        }
                    }
                } else {
                    menuList.add("No menu items found. Add your first item!");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestaurantDashboardActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Add button click listeners
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        // Add Food Button Click
        if (btnAddFood != null) {
            btnAddFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open AddFoodActivity
                    Intent intent = new Intent(RestaurantDashboardActivity.this, AddFoodActivity.class);
                    startActivity(intent);
                }
            });
        }

        // View Orders Button Click
        if (btnViewOrders != null) {
            btnViewOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open ViewOrdersActivity
                    Intent intent = new Intent(RestaurantDashboardActivity.this, RestaurantOrderActivity.class);
                    startActivity(intent);
                }
            });
        }
    }


}