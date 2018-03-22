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

public class MainActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {
    ConnectivityCheck myCheck;
    private String MYURL = "http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    JSONArray jsonArray;
    ArrayList<Pets> pets;
    Spinner petSpinner;
    private SharedPreferences myPreference;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = null;
    private boolean enablePreferenceListener = false;
    WebImageView_KP mv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mv = (WebImageView_KP)findViewById(R.id.image_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        petSpinner = (Spinner) findViewById(R.id.spinner);
        petSpinner.setOnItemSelectedListener(this);
        myCheck = new ConnectivityCheck(this);
        myPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        pets = new ArrayList<>();
        setPreferenceChangeListener();
        pullJSONData();
    }

    public void pullJSONData(){
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // make sure the network is up before you attempt a connection
        // notify user of problem? Not very good, maybe wait a little while and
        // try later? remember make users life easier
        ConnectivityCheck myCheck = new ConnectivityCheck(this);
        if (myCheck.isNetworkReachable()) {
            //A common async task
            DownloadTask_KP myTask = new DownloadTask_KP(this);
            myTask.execute(MYURL);
        }
    }

    public void setPreferenceChangeListener() {
        //if not created yet then do so
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals("PREF_LIST")) {
                        String myString = myPreference.getString("PREF_LIST", "Nothing Found");
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

    public void processJSON(String string) {
        if(string == null){
            mv.setImageResource(R.drawable.errorimg);
            petSpinner.setVisibility(View.GONE);
            return;
        }
        else {
            petSpinner.setVisibility(View.VISIBLE);
        }
        try {
            JSONObject jsonobject = new JSONObject(string);
            jsonArray = jsonobject.getJSONArray("pets");
            addPetsToList(jsonArray);
            setPetAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPetsToList(JSONArray j){
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject JsonPetObject = j.getJSONObject(i);
                String name = JsonPetObject.getString("name");
                String file = JsonPetObject.getString("file");
                Pets aPet = new Pets(name,file);
                pets.add(aPet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPetAdapter(){
        ArrayList<String> names = new ArrayList<>();
        for (Pets p : pets){
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent_action_setting = new Intent(this, SettingsActivity.class);
        startActivity(myIntent_action_setting);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String fileOfSelectedImage = pets.get(position).getFile();
        String beginingOfURL = "http://www.pcs.cnu.edu/~kperkins/pets/";
        String fullUrl = beginingOfURL + fileOfSelectedImage;
        mv.setImageUrl(fullUrl);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
