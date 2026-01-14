package com.example.new_foodfleet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RestaurantDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_dashboard);

        findViewById(R.id.btnAddFood).setOnClickListener(v ->
                startActivity(new Intent(this, AddFoodActivity.class))
        );

        findViewById(R.id.btnViewOrders).setOnClickListener(v ->
                startActivity(new Intent(this, RestaurantOrderActivity.class))
        );
    }
}
