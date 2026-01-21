package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;


public class Singlelogin extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    FirebaseAuth auth;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlelogin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        btnLogin.setOnClickListener(v -> loginUser());
    }

    void loginUser() {

        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    checkRole(authResult.getUser().getUid());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    void checkRole(String uid) {

        // 1️⃣ Check Users
        rootRef.child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            openUserDashboard();
                        } else {
                            checkRestaurant(uid);
                        }
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    void checkRestaurant(String uid) {
        rootRef.child("Restaurants").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            openRestaurantPanel();
                        } else {
                            checkRider(uid);
                        }
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    void checkRider(String uid) {
        rootRef.child("Riders").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            openRiderPanel();
                        } else {
                            Toast.makeText(Singlelogin.this,
                                    "Role not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    void openUserDashboard() {
        startActivity(new Intent(this, UserDashboard.class));
        finish();
    }

    void openRestaurantPanel() {
        startActivity(new Intent(this, RestaurantDashboardActivity.class));
        finish();
    }

    void openRiderPanel() {
        startActivity(new Intent(this, RiderDashboardActivity.class));
        finish();
    }
}