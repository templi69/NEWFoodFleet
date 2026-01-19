package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;


public class usercart extends AppCompatActivity {

    ListView listCart;
    ArrayList<String> cartItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercart);

        listCart = findViewById(R.id.listCart);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cartItems);
        listCart.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("cart");

        loadCart();
    }

    private void loadCart() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cartItems.clear();

                for (DataSnapshot rest : snapshot.getChildren()) {
                    for (DataSnapshot item : rest.getChildren()) {

                        String name = item.child("itemName").getValue(String.class);
                        String price = item.child("price").getValue(String.class);
                        String qty = item.child("qty").getValue(String.class);

                        cartItems.add(name + " x" + qty + " = Rs " + (Integer.parseInt(price) * Integer.parseInt(qty)));
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(usercart.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

