package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiderSignupActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail, etPassword;
    Button btnSignup;

    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_rider_signup);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        btnSignup.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            // Phone number validation
            if (phone.length() != 11) {
                Toast.makeText(this, "Phone number must be exactly 11 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || pass.isEmpty() || pass.length() < 6) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                db.child("Riders").child(uid).child("role").setValue("rider");
                                db.child("Riders").child(uid).child("name").setValue(name);
                                db.child("Riders").child(uid).child("phone").setValue(phone)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(this, "Rider Registered", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, RiderDashboardActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Failed to save rider", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
