package com.example.sabri.petsproject3;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {
    ConnectivityCheck myCheck;
    private static final String TAG = "ParseJSON";
    private static final String MYURL = "http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    JSONArray jsonArray;
    ArrayList<Pets> pets = new ArrayList<>();
    Spinner petSpinner;
    ImageView imageView;
    private SharedPreferences myPref;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        petSpinner = (Spinner) findViewById(R.id.spinner);
        petSpinner.setOnItemSelectedListener(this);
        myCheck = new ConnectivityCheck(this);
        myPref = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                ListPreference lp = (ListPreference) sharedPreferences;
                int index = lp.findIndexOfValue(s);
                CharSequence[] entries = lp.getEntries();
                Toast.makeText(getApplicationContext(),entries[index],Toast.LENGTH_SHORT).show();
            }
        };
        myPref.registerOnSharedPreferenceChangeListener(listener);
        // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // make sure the network is up before you attempt a connection
        // notify user of problem? Not very good, maybe wait a little while and
        // try later? remember make users life easier
        ConnectivityCheck myCheck = new ConnectivityCheck(this);
        if (myCheck.isNetworkReachable()) {

            //A common async task
            DownloadTask_KP myTask = new DownloadTask_KP(this);


            // //////////////////////////////////////////////////// demo this
            // telescoping initilization pattern
            //myTask.setnameValuePair("screen_name", "maddow").setnameValuePair("day", "today");
            // myTask.execute(TWITTER_RACHEL);

            myTask.execute(MYURL);
        }

    }
    public void processJSON(String string) {
        try {
            JSONObject jsonobject = new JSONObject(string);

            // you must know what the data format is, a bit brittle
            jsonArray = jsonobject.getJSONArray("pets");

            // how many entries
            //numberentries = jsonArray.length();
            addPetsToList(jsonArray);
            setPetAdapter();
            //currententry = 0;
            //setJSONUI(currententry); // parse out object currententry

           // Log.i(TAG, "Number of entries " + numberentries);

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
    public void doNetworkCheck(View view) {
        String res = myCheck.isNetworkReachable()?"Network Reachable":"No Network";

        Toast.makeText(this, res,Toast.LENGTH_SHORT).show();
    }

    public void doWirelessCheck(View view) {
        String res = myCheck.isWifiReachable()?"WiFi Reachable":"No WiFi";
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
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
        //lets get an image
        WebImageView_KP mv = (WebImageView_KP)findViewById(R.id.image_view);
        String beginingOfURL = "http://www.pcs.cnu.edu/~kperkins/pets/";
        String fullUrl = beginingOfURL + fileOfSelectedImage;
        mv.setImageUrl(fullUrl);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
