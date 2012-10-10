package com.artech.restaurants;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewRestaurantActivity extends Activity {

   // Progress Dialog
   private ProgressDialog pDialog;

   JSONParser jsonParser = new JSONParser();
   EditText inputName;
   EditText inputDesc;

   // url to create new Restaurant
   private static String url_create_restaurant = "http://droid.tta.com.sa/hassan/rest/create_rest.php";

   // JSON Node names
   private static final String TAG_SUCCESS = "success";

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.add_restaurant);

       // Edit Text
       inputName = (EditText) findViewById(R.id.inputName);
       inputDesc = (EditText) findViewById(R.id.inputDesc);

       // Create button
       Button btnCreateRestaurant = (Button) findViewById(R.id.btnCreateRestaurant);

       // button click event
       btnCreateRestaurant.setOnClickListener(new View.OnClickListener() {

           public void onClick(View view) {
               // creating new Restaurant in background thread
               new CreateNewRestaurant().execute();
           }
       });
   }

   /**
    * Background Async Task to Create new Restaurant
    * */
   class CreateNewRestaurant extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(NewRestaurantActivity.this);
           pDialog.setMessage("Creating Restaurant..");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(true);
           pDialog.show();
       }

       /**
        * Creating Restaurant
        * */
       protected String doInBackground(String... args) {
           String name = inputName.getText().toString();
           String description = inputDesc.getText().toString();

           // Building Parameters
           List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("name", name));
           params.add(new BasicNameValuePair("description", description));

           // getting JSON Object
           // Note that create Restaurant url accepts POST method
           JSONObject json = jsonParser.makeHttpRequest(url_create_restaurant,
                   "POST", params);

           // check log cat for response
           Log.d("Create Response", json.toString());
           
           // check for success tag
           try {
               int success = json.getInt(TAG_SUCCESS);

               if (success == 1) {
                   // successfully created Restaurant
                   Intent i = new Intent(getApplicationContext(), AllRestaurantsActivity.class);
                   startActivity(i);

                   // closing this screen
                   finish();
               } else {
                   // failed to create Restaurant
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           return null;
       }

       /**
        * After completing background task Dismiss the progress dialog
        * **/
       protected void onPostExecute(String file_url) {
           // dismiss the dialog once done
           pDialog.dismiss();
       }

   }
}