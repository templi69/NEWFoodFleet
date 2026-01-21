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

public class UserSignup extends AppCompatActivity {

    EditText etuserName, etuserEmail, etuserPassword, etuserPhoneNumber;
    Button btnuserSignup;

    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);

        etuserName = findViewById(R.id.etUserName);
        etuserEmail = findViewById(R.id.etUserEmail);
        etuserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
        etuserPassword = findViewById(R.id.etUserPassword);
        btnuserSignup = findViewById(R.id.btnUserSignup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        btnuserSignup.setOnClickListener(v -> {
            String userName = etuserName.getText().toString().trim();
            String userEmail = etuserEmail.getText().toString().trim();
            String userPhoneNumber = etuserPhoneNumber.getText().toString().trim();
            String userPassword = etuserPassword.getText().toString().trim();

            if (userPhoneNumber.length() != 11) {
                Toast.makeText(this, "Phone number must be exactly 11 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userName.isEmpty() || userPhoneNumber.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userPassword.length() < 6) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                db.child("Users").child(uid).child("role").setValue("user");
                                db.child("Users").child(uid).child("name").setValue(userName);
                                db.child("Users").child(uid).child("phone").setValue(userPhoneNumber)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(this, UserDashboard.class));
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show();
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
