package com.artech.restaurants;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
 
public class MainScreenActivity extends Activity{
 
    Button btnViewRestaurants;
    Button btnNewRestaurantt;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
 
        // Buttons
        btnViewRestaurants = (Button) findViewById(R.id.btnViewRestaurants);
        btnNewRestaurantt = (Button) findViewById(R.id.btnCreateRViewRestaurants);
 
        // view products click event
        btnViewRestaurants.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                // Launching All products Activity
                Intent i = new Intent(getApplicationContext(), AllRestaurantsActivity.class);
                startActivity(i);
 
            }
        });
 
        // view products click event
        btnNewRestaurantt.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                // Launching create new product activity
                Intent i = new Intent(getApplicationContext(), NewRestaurantActivity.class);
                startActivity(i);
 
            }
        });
    }
}