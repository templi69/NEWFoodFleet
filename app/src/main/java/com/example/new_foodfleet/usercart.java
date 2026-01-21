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
import java.util.HashMap;

public class usercart extends AppCompatActivity {

    ListView listCart;
    Button btnConfirmOrder;
    ArrayList<String> cartItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatabaseReference cartRef;

    // Store context for inner classes
    usercart myActivity = this;

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

        // Cart reference
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
                    String restaurantName = rest.getKey();

                    for (DataSnapshot item : rest.getChildren()) {
                        String name = item.child("itemName").getValue(String.class);
                        String priceStr = item.child("price").getValue(String.class);
                        Long qty = item.child("qty").getValue(Long.class);

                        if (name == null || priceStr == null || qty == null) continue;

                        long price = Long.parseLong(priceStr);
                        long total = price * qty;

                        cartItems.add(restaurantName + ": " + name + " x" + qty + " = Rs " + total);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(myActivity, "Failed to load cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // SIMPLE ORDER CONFIRM LOGIC
    void confirmOrder() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(myActivity, "Cart is empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Loop through each restaurant in cart
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {

                    String restaurantId = restaurantSnapshot.getKey();

                    // 1. Create order in Restaurant's node
                    DatabaseReference restaurantOrdersRef = FirebaseDatabase.getInstance()
                            .getReference("Restaurants")
                            .child(restaurantId)
                            .child("orders");

                    String orderId = restaurantOrdersRef.push().getKey();

                    // 2. Prepare order data
                    HashMap<String, Object> orderData = new HashMap<>();
                    orderData.put("orderId", orderId);
                    orderData.put("userId", userId);
                    orderData.put("restaurantId", restaurantId);
                    orderData.put("status", "pending");
                    orderData.put("timestamp", System.currentTimeMillis());
                    orderData.put("items", restaurantSnapshot.getValue());

                    // Add customer info
                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
                        orderData.put("customerEmail",
                                FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    }

                    // 3. Save to Restaurant
                    restaurantOrdersRef.child(orderId).setValue(orderData);

                    // 4. Save to User's orders
                    DatabaseReference userOrdersRef = FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(userId)
                            .child("orders")
                            .child(orderId);

                    userOrdersRef.setValue(orderData);
                }

                // 5. Clear cart
                cartRef.removeValue();

                Toast.makeText(myActivity,
                        "Order Placed Successfully!",
                        Toast.LENGTH_LONG).show();

                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(myActivity, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}