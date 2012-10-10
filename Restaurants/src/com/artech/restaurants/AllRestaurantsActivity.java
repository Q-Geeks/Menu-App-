package com.artech.restaurants;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class AllRestaurantsActivity extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> RestaurantsList;
 
    // url to get all Restaurant list
    private static String url_all_restaurants = "http://droid.tta.com.sa/hassan/rest/get_all_rest.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESTAURANTS = "rest";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
 
    // Restaurants JSONArray
    JSONArray restaurants = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_restaurants);
 
        // Hashmap for ListView
        RestaurantsList = new ArrayList<HashMap<String, String>>();
 
        // Loading Restaurant in Background Thread
        new LoadAllRestaurants().execute();
 
        // Get listview
        ListView lv = getListView();
 
        // on seleting single Restaurant
        // launching Edit Restaurant Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	
            	Log.d("Jest before", "Just before launching edit activity");
            	
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditRestaurantActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_ID, pid);
 
                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
                
                Log.d("Jest After", "Just after launching edit activity");
            }
        });
 
    }
 
    // Response from Edit Restaurant Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted Restaurant
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all Restaurants by making HTTP Request
     * */
    class LoadAllRestaurants extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllRestaurantsActivity.this);
            pDialog.setMessage("Loading Restaurants. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All Restaurants from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_restaurants, "GET", params);
 
            // Check your log cat for JSON response
            Log.d("All Restaurants: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // Restaurants found
                    // Getting Array of Restaurants
                    restaurants = json.getJSONArray(TAG_RESTAURANTS);
 
                    // looping through All Restaurants
                    for (int i = 0; i < restaurants.length(); i++) {
                        JSONObject c = restaurants.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_TITLE);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_TITLE, name);
 
                        // adding HashList to ArrayList
                        RestaurantsList.add(map);
                    }
                } else {
                    // no Restaurants found
                    // Launch Add New Restaurant Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewRestaurantActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
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
            // dismiss the dialog after getting all Restaurants
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            AllRestaurantsActivity.this, RestaurantsList,
                            R.layout.list_item, new String[] { TAG_ID,
                                    TAG_TITLE},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
}
