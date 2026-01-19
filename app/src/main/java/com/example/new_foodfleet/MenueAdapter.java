package com.example.new_foodfleet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.new_foodfleet.ui.login.MenueItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MenueAdapter extends ArrayAdapter<MenueItemModel>{

        Context context;
        ArrayList<MenueItemModel> list;
        DatabaseReference cartRef;

        public MenueAdapter(@NonNull Context context, ArrayList<MenueItemModel> list, String restaurantId) {
            super(context, 0, list);
            this.context = context;
            this.list = list;

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            cartRef = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("cart")
                    .child(restaurantId);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.activity_item_menue, parent, false);
            }

            TextView tvName = convertView.findViewById(R.id.tvItemName);
            TextView tvPrice = convertView.findViewById(R.id.tvItemPrice);
            TextView tvQty = convertView.findViewById(R.id.tvQty);

            Button btnInc = convertView.findViewById(R.id.btnInc);
            Button btnDec = convertView.findViewById(R.id.btnDec);
            Button btnAdd = convertView.findViewById(R.id.btnAddToCart);

            MenueItemModel item = list.get(position);

            tvName.setText(item.itemName);
            tvPrice.setText("Rs " + item.price);
            tvQty.setText(String.valueOf(item.qty));

            btnInc.setOnClickListener(v -> {
                if (item.qty < 10) {
                    item.qty++;
                    tvQty.setText(String.valueOf(item.qty));
                }
            });

            btnDec.setOnClickListener(v -> {
                if (item.qty > 0) {
                    item.qty--;
                    tvQty.setText(String.valueOf(item.qty));
                }
            });

            btnAdd.setOnClickListener(v -> {
                if (item.qty == 0) {
                    Toast.makeText(context, "Select quantity first", Toast.LENGTH_SHORT).show();
                    return;
                }

                cartRef.child(item.itemName).setValue(item);
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            });

            return convertView;
        }
    }


