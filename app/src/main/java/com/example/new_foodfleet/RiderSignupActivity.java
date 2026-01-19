package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiderSignupActivity extends AppCompatActivity {

    EditText etName, etPhone;
    Button btnSignup;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_rider_signup);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .push()
                    .getKey();

            if (id == null) return;

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(id);

            ref.child("name").setValue(name);
            ref.child("phone").setValue(phone);
            ref.child("role").setValue("rider")
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Rider Registered", Toast.LENGTH_SHORT).show();

                            //  GO TO RIDER DASHBOARD
                            Intent intent = new Intent(
                                    RiderSignupActivity.this,
                                    RiderDashboardActivity.class
                            );
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
