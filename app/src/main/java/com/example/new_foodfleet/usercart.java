package com.example.new_foodfleet;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class usercart extends AppCompatActivity {

    ListView listCart;
    Button btnConfirmOrder;

    ArrayList<String> cartItems = new ArrayList<>();
    ArrayAdapter<String> adapter;

    DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercart);

        listCart = findViewById(R.id.listCart);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                cartItems);

        listCart.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cartRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("cart");

        loadCart();

        btnConfirmOrder.setOnClickListener(v -> confirmOrder());
    }

    void loadCart() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cartItems.clear();

                for (DataSnapshot rest : snapshot.getChildren()) {
                    for (DataSnapshot item : rest.getChildren()) {

                        String name = item.child("itemName").getValue(String.class);
                        String priceStr = item.child("price").getValue(String.class);
                        Long qtyLong = item.child("qty").getValue(Long.class);

                        if (name == null || priceStr == null || qtyLong == null) continue;

                        long price = 0;
                        try {
                            price = Long.parseLong(priceStr);
                        } catch (Exception e) {
                            price = 0;
                        }

                        long total = price * qtyLong;
                        cartItems.add(name + " x" + qtyLong + " = Rs " + total);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(usercart.this,
                        "Failed to load cart",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 CONFIRM ORDER LOGIC
    void confirmOrder() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(usercart.this,
                            "Cart is empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot rest : snapshot.getChildren()) {

                    String restaurantId = rest.getKey();

                    DatabaseReference restOrdersRef = FirebaseDatabase.getInstance()
                            .getReference("Restaurants")
                            .child(restaurantId)
                            .child("orders");

                    String orderId = restOrdersRef.push().getKey();
                    if (orderId == null) return;

                    // Order info
                    restOrdersRef.child(orderId).child("userId").setValue(userId);
                    restOrdersRef.child(orderId).child("status").setValue("pending");
                    restOrdersRef.child(orderId).child("timestamp")
                            .setValue(System.currentTimeMillis());

                    // Order items
                    restOrdersRef.child(orderId)
                            .child("items")
                            .setValue(rest.getValue());

                    // Save order for user
                    DatabaseReference userOrdersRef = FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(userId)
                            .child("orders")
                            .child(orderId);

                    userOrdersRef.child("restaurantId").setValue(restaurantId);
                    userOrdersRef.child("status").setValue("pending");
                    userOrdersRef.child("items").setValue(rest.getValue());
                }

                // Clear cart
                cartRef.removeValue();

                Toast.makeText(usercart.this,
                        "Order placed successfully!",
                        Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(usercart.this,
                        "Failed to place order",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
