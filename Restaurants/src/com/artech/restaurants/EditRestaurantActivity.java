package com.artech.restaurants;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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

public class EditRestaurantActivity extends Activity {

   EditText txtName;
   EditText txtDesc;
   EditText txtCreatedAt;
   Button btnSave;
   Button btnDelete;

   String pid;

   // Progress Dialog
   private ProgressDialog pDialog;

   // JSON parser class
   JSONParser jsonParser = new JSONParser();

   // single product url
   private static final String url_restaurant_detials = "http://droid.tta.com.sa/hassan/rest/get_rest_details.php";

   // url to update product
   private static final String url_update_restaurant = "http://droid.tta.com.sa/hassan/rest/update_rest.php";

   // url to delete product
   private static final String url_delete_restaurant = "http://droid.tta.com.sa/hassan/rest/delete_rest.php";

   // JSON Node names
   private static final String TAG_SUCCESS = "success";
   private static final String TAG_RESTAURANT = "rest";
   private static final String TAG_PID = "id";
   private static final String TAG_TITLE = "title";
   private static final String TAG_BODY = "body";

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.edit_restaurant);
       
       Log.d("Jest in", "Just entered edit activity");
       
       // save button
       btnSave = (Button) findViewById(R.id.btnSave);
       btnDelete = (Button) findViewById(R.id.btnDelete);

       // getting restaurant details from intent
       Intent i = getIntent();

       // getting restaurant id (pid) from intent
       pid = i.getStringExtra(TAG_PID);

       // Getting complete product details in background thread
       new GetRestaurantDetails().execute();

       // save button click event
       btnSave.setOnClickListener(new View.OnClickListener() {

           public void onClick(View arg0) {
               // starting background task to update Restaurant
               new SaveRestaurantDetails().execute();
           }
       });

       // Delete button click event
       btnDelete.setOnClickListener(new View.OnClickListener() {

           public void onClick(View arg0) {
               // deleting product in background thread
               new DeleteRestaurant().execute();
           }
       });

   }

   /**
    * Background Async Task to Get complete Restaurant details
    * */
   class GetRestaurantDetails extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(EditRestaurantActivity.this);
           pDialog.setMessage("Loading Restaurant details. Please wait...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(true);
           pDialog.show();
       }

       /**
        * Getting Restaurant details in background thread
        * */
       protected String doInBackground(String... params) {

           // updating UI from Background Thread
           runOnUiThread(new Runnable() {
               public void run() {
                   // Check for success tag
                   int success;
                   try {
                	   
                	   Log.d("Before Call", "the pid is " + pid);
                	   
                       // Building Parameters
                       List<NameValuePair> params = new ArrayList<NameValuePair>();
                       params.add(new BasicNameValuePair("pid", pid));

                       // getting Restaurant details by making HTTP request
                       // Note that Restaurant details url will use GET request
                       JSONObject json = jsonParser.makeHttpRequest(
                               url_restaurant_detials, "GET", params);
                       
                       // check your log for json response
                       Log.d("Single Restaurant Details", json.toString());

                       // json success tag
                       success = json.getInt(TAG_SUCCESS);
                       if (success == 1) {
                           // successfully received Restaurant details
                           JSONArray productObj = json
                                   .getJSONArray(TAG_RESTAURANT); // JSON Array

                           // get first product object from JSON Array
                           JSONObject restaurant = productObj.getJSONObject(0);

                           // product with this pid found
                           // Edit Text
                           txtName = (EditText) findViewById(R.id.inputName);
                           txtDesc = (EditText) findViewById(R.id.inputDesc);

                           // display product data in EditText
                           txtName.setText(restaurant.getString(TAG_TITLE));
                           txtDesc.setText(restaurant.getString(TAG_BODY));

                       }else{
                           // Restaurant with pid not found
                       }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
           });

           return null;
       }

       /**
        * After completing background task Dismiss the progress dialog
        * **/
       protected void onPostExecute(String file_url) {
           // dismiss the dialog once got all details
           pDialog.dismiss();
       }
   }

   /**
    * Background Async Task to  Save Restaurant Details
    * */
   class SaveRestaurantDetails extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(EditRestaurantActivity.this);
           pDialog.setMessage("Saving Restaurant ...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(true);
           pDialog.show();
       }

       /**
        * Saving Restaurant
        * */
       protected String doInBackground(String... args) {

           // getting updated data from EditTexts
           String name = txtName.getText().toString();
           String description = txtDesc.getText().toString();

           // Building Parameters
           List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair(TAG_PID, pid));
           params.add(new BasicNameValuePair(TAG_TITLE, name));
           params.add(new BasicNameValuePair(TAG_BODY, description));

           // sending modified data through http request
           // Notice that update Restaurant url accepts POST method
           JSONObject json = jsonParser.makeHttpRequest(url_update_restaurant,
                   "POST", params);

           // check json success tag
           try {
               int success = json.getInt(TAG_SUCCESS);

               if (success == 1) {
                   // successfully updated
                   Intent i = getIntent();
                   // send result code 100 to notify about product update
                   setResult(100, i);
                   finish();
               } else {
                   // failed to update product
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
           // dismiss the dialog once product updated
           pDialog.dismiss();
       }
   }

   /*****************************************************************
    * Background Async Task to Delete Product
    * */
   class DeleteRestaurant extends AsyncTask<String, String, String> {

       /**
        * Before starting background thread Show Progress Dialog
        * */
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           pDialog = new ProgressDialog(EditRestaurantActivity.this);
           pDialog.setMessage("Deleting Restaurant...");
           pDialog.setIndeterminate(false);
           pDialog.setCancelable(true);
           pDialog.show();
       }

       /**
        * Deleting Restaurant
        * */
       protected String doInBackground(String... args) {

           // Check for success tag
           int success;
           try {
               // Building Parameters
               List<NameValuePair> params = new ArrayList<NameValuePair>();
               params.add(new BasicNameValuePair("pid", pid));

               // getting Restaurant details by making HTTP request
               JSONObject json = jsonParser.makeHttpRequest(
                       url_delete_restaurant, "POST", params);

               // check your log for json response
               Log.d("Delete Restaurant", json.toString());

               // json success tag
               success = json.getInt(TAG_SUCCESS);
               if (success == 1) {
                   // Restaurant successfully deleted
                   // notify previous activity by sending code 100
                   Intent i = getIntent();
                   // send result code 100 to notify about product deletion
                   setResult(100, i);
                   finish();
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
           // dismiss the dialog once product deleted
           pDialog.dismiss();

       }

   }
}
