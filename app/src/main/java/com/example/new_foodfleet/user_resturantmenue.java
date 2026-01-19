package com.example.new_foodfleet;


import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class user_resturantmenue extends AppCompatActivity {

    TextView tvRestaurantName;
    ListView listMenu;

    ArrayList<String> menuList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    DatabaseReference menuRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_resturantmenue);

        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        listMenu = findViewById(R.id.listMenu);

        String restaurantId = getIntent().getStringExtra("restaurantId");
        String restaurantName = getIntent().getStringExtra("restaurantName");

        tvRestaurantName.setText(restaurantName);

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                menuList
        );
        listMenu.setAdapter(adapter);

        // 🔥 CORRECT MENU PATH
        menuRef = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(restaurantId)
                .child("menu");

        loadMenu();
    }

    private void loadMenu() {
        menuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                menuList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("itemName").getValue(String.class);
                    String price = ds.child("price").getValue(String.class);

                    if (name != null && price != null) {
                        menuList.add(name + " - Rs " + price);
                    }
                }

                if (menuList.isEmpty()) {
                    menuList.add("No items available");
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(user_resturantmenue.this,
                        "Failed to load menu",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}