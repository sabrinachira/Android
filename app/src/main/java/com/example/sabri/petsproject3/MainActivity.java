/*
 * Authors: Sabrina Chira 50% & Dillion Skyes 50%
 * Date: 3/25/18
 * Project 3 Pets
 */
package com.example.sabri.petsproject3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
 * MainActivity is cthe class that creates the application
 */
public class MainActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {
    ConnectivityCheck myCheck;
    private String MYURL; //getString(R.string.link_json);
    JSONArray jsonArray;
    ArrayList<Pets> pets;
    Spinner petSpinner;
    private SharedPreferences myPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private boolean enablePreferenceListener = false;
    WebImageView_KP mv;

    /*
     * onCreate is called when the app is started
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mv = (WebImageView_KP) findViewById(R.id.image_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        petSpinner = (Spinner) findViewById(R.id.spinner);
        petSpinner.setOnItemSelectedListener(this);
        myCheck = new ConnectivityCheck(this);
        myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        MYURL = myPreference.getString(getString(R.string.PREF_LIST), getString(R.string.Nothing_Found));
        if (MYURL.equals(getString(R.string.Nothing_Found))) { //nothing selected, must be showing the pets
            MYURL = "http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
        }
        pets = new ArrayList<>();
        setPreferenceChangeListener();

        pullJSONData();

    }

    /*
     * onResume is called when the application is closed and resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        ConnectivityCheck myCheck = new ConnectivityCheck(this);
        if (myCheck.isNetworkReachable() || myCheck.isWifiReachable()) {
            //only reload json data if our pets arrayList is empty. When the network checks return no network I clear to the pets arrayList
            if (pets.isEmpty()) {
                pullJSONData();
            }
        }
    }

    /*
     * onSaveInstanceState saves the current instance of MYURL and saves it for when the application is accessed again
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("myURL", this.MYURL);
    }

    /*
     * pullJSONData pulls the JSON file from the requested URL
     */
    public void pullJSONData() {
        // make sure the network is up before you attempt a connection
        ConnectivityCheck myCheck = new ConnectivityCheck(this);
        if (myCheck.isNetworkReachable() || myCheck.isWifiReachable()) { //ADDED
            //A common async task
            DownloadTask_KP myTask = new DownloadTask_KP(this);
            myTask.execute(MYURL);
        } else {
            mv.setImageResource(R.drawable.network_unreachable);
            petSpinner.setVisibility(View.GONE);
            pets.clear();
        }
    }

    /*
     * setPreferenceChangeListener looks for if there is a change in the JSON info selector
     */
    public void setPreferenceChangeListener() {
        //if not created yet then do so
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals(getString(R.string.PREF_LIST))) {
                        String myString = myPreference.getString(getString(R.string.PREF_LIST), getString(R.string.Nothing_Found));
                        pets.clear();
                        MYURL = myString;
                        pullJSONData();
                    }
                }
            };
        }
        //toggle listener
        enablePreferenceListener = !enablePreferenceListener;
        if (enablePreferenceListener)
            // register the listener
            myPreference.registerOnSharedPreferenceChangeListener(listener);
        else
            myPreference.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /*
     * processJSON process the JSON file, shows the pictures from the JSON,
     * shows errors images if there is no JSON file,
     * displays the spinner when CNU JSON is selected, and doesn't display
     * the CNU JSON when the Teton JSON is selected.
     */
    public void processJSON(String string) {
        if (MYURL.equals(getString(R.string.Nothing_Found))) { //nothing selected
            string = "http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
            MYURL = string;
            petSpinner.setVisibility(View.VISIBLE);
        } else if (string == null) { //teton selected
            mv.setImageResource(R.drawable.erroring);
            petSpinner.setVisibility(View.GONE);
        } else { //pets selected
            petSpinner.setVisibility(View.VISIBLE);
        }
        try {
            JSONObject jsonobject = new JSONObject(string);
            jsonArray = jsonobject.getJSONArray(getString(R.string.pets));
            addPetsToList(jsonArray);
            setPetAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * addPetsToList adds each pet to the ArrayList that imports the images into the spinner
     */
    public void addPetsToList(JSONArray j) {
        //make sure pets arrayList if empty before you add more to it.
        if (pets.size() > 0) {
            pets.clear();
        }
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject JsonPetObject = j.getJSONObject(i);
                String name = JsonPetObject.getString(getString(R.string.name));
                String file = JsonPetObject.getString(getString(R.string.file));
                Pets aPet = new Pets(name, file);
                pets.add(aPet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     *setPetAdapter sets the pet spinner
     */
    public void setPetAdapter() {
        ArrayList<String> names = new ArrayList<>();
        for (Pets p : pets) {
            String name = p.getName();
            names.add(name);
        }
        //Setting adapter to show the items in the spinner
        petSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, names));
    }

    /*
     * onCreateOptionsMenu creates the application's menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * onOptionsItemSelected creates an intent when a item is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent_action_setting = new Intent(this, SettingsActivity.class);
        startActivity(myIntent_action_setting);
        return true;
    }

    /*
     * onItemSelected sets the image when the there is no network connection available.
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String fileOfSelectedImage = pets.get(position).getFile();
        String beginingOfURL = "http://www.pcs.cnu.edu/~kperkins/pets/"; //getString(R.string.link);
        String fullUrl = beginingOfURL + fileOfSelectedImage;
        if (!myCheck.isNetworkReachable() && !myCheck.isWifiReachable()) {
            mv.setImageResource(R.drawable.network_unreachable);
            petSpinner.setVisibility(View.GONE);
            pets.clear();
        } else {
            mv.setImageUrl(fullUrl);
        }
    }

    /*
     * onNothingSelected doesn't do anything
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
