package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class user_resturantmenue extends AppCompatActivity {

    TextView tvRestaurantName;
    ListView listMenu;

    ArrayList<MenueItemModel> menuList = new ArrayList<>();
    MenueAdapter adapter;

    DatabaseReference menuRef;
    String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_resturantmenue);

        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        listMenu = findViewById(R.id.listMenu);

        restaurantId = getIntent().getStringExtra("restaurantId");
        String restaurantName = getIntent().getStringExtra("restaurantName");

        tvRestaurantName.setText(restaurantName);

        adapter = new MenueAdapter(this, menuList, restaurantId);
        listMenu.setAdapter(adapter);

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
                    String priceStr = ds.child("price").getValue(String.class);

                    //price is String in Firebase
                    if (name != null && priceStr != null) {
                        menuList.add(new MenueItemModel(name, priceStr, 0));
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(user_resturantmenue.this,
                        "Failed to load menu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
