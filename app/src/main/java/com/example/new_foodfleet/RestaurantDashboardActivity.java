package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RestaurantDashboardActivity extends AppCompatActivity {

    ListView listMenu;
    ArrayList<String> menuList;
    ArrayAdapter<String> adapter;

    DatabaseReference ref;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_dashboard);

        listMenu = findViewById(R.id.listMenu);

        menuList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                menuList
        );

        listMenu.setAdapter(adapter);

        uid = FirebaseAuth.getInstance().getUid();

        ref = FirebaseDatabase.getInstance()
                .getReference("Restaurants")
                .child(uid)
                .child("menu");

        loadMenu();
    }

    private void loadMenu() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                menuList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String name = ds.child("itemName").getValue(String.class);
                    String price = ds.child("price").getValue(String.class);

                    menuList.add(name + " - Rs " + price);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
