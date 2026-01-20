package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restaurant Signup
        findViewById(R.id.btnRest).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        RestaurantSignupActivity.class
                ))
        );


        findViewById(R.id.btnUser).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, UserSignup.class))
        );
        // Rider Signup
        findViewById(R.id.btnRider).setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        RiderSignupActivity.class
                ))
        );

    }
}
