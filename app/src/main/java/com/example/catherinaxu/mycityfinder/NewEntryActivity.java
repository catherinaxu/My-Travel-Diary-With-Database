package com.example.catherinaxu.mycityfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NewEntryActivity extends Activity {

private static final int NUM_RESULTS = 10;
private static final int GET_DESTINATION = 10;
private static final int GET_DESCRIPTION = 11;
private static final int NO_RESULT = 9;
private static final int NUM_MATCHES_TO_DISPLAY = 10;
private static List<String> posAddresses;
private static List<Address> matches;
private static Address address;
private static String description;
ListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);
        getActionBar().hide();

        //sets font of title
        TextView title = (TextView) findViewById(R.id.title);
        Typeface font_bold = Typeface.createFromAsset(getAssets(), "ostrich-black.ttf");
        title.setTypeface(font_bold);

        //sets font of buttons
        Typeface font_reg = Typeface.createFromAsset(getAssets(), "ostrich-regular.ttf");
        Button button1 = (Button) findViewById(R.id.find);
        button1.setTypeface(font_reg);

        TextView status = (TextView) findViewById(R.id.currentColorLabel);
        status.setTypeface(font_reg);

        if (mAdapter != null) {
            mAdapter.deselectAll();
        }
    }

    //go back with the NO_RESULT result code
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult (NO_RESULT, intent);
        finish();
    }

    public void putAddressInList(List<Address> matches) {
        posAddresses = new ArrayList<>();
        if (matches.size() >= NUM_MATCHES_TO_DISPLAY) {
            for (int i = 0; i < 10; i++) {
                Address addr = matches.get(i);
                if (addr.getAdminArea() != null && addr.getCountryName() != null) {
                    posAddresses.add(addr.getFeatureName() + ", " + addr.getAdminArea() + ", " + addr.getCountryName());
                } else {
                    posAddresses.add(addr.getFeatureName());
                }
            }
        } else {
            for (int i = 0; i < matches.size(); i++) {
                Address addr = matches.get(i);
                if (addr.getAdminArea() != null && addr.getCountryName() != null) {
                    posAddresses.add(addr.getFeatureName() + ", " + addr.getAdminArea() + ", " + addr.getCountryName());
                } else {
                    posAddresses.add(addr.getFeatureName());
                }
            }
        }
    }

    public void searchLocation(View view) {
        EditText edittext = (EditText) findViewById(R.id.destination);
        String text = edittext.getText().toString();

        //case to handle empty string input
        if (text.equals("")) {
            Toast.makeText(this, "Please enter the destination.", Toast.LENGTH_SHORT).show();

        //initiate geocoder
        } else {
            Geocoder geocoder = new Geocoder(this, Locale.US);
            if (!geocoder.isPresent()) { //what is this error?
                Toast.makeText(this, "Please install Google Maps to continue", Toast.LENGTH_SHORT).show();
            }
            try {
                matches = geocoder.getFromLocationName(text, NUM_RESULTS);

                //if there are no matching results
                if (matches.size() == 0) {
                    Toast.makeText(this, "Destination not found. Please try again.", Toast.LENGTH_SHORT).show();

                //if there are a number of matches
                } else {
                    Toast.makeText(this, "Destination found! Please write a short description.", Toast.LENGTH_SHORT).show();
                    putAddressInList(matches);
                    mAdapter = new ListViewAdapter(this, posAddresses, "ostrich-regular.ttf");

                    ListView listview = (ListView) findViewById(R.id.list);
                    listview.setAdapter(mAdapter);

                    //code to handle clicks
                    listview.setOnItemClickListener(
                            new AdapterView.OnItemClickListener()  {
                                @Override
                                public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                                    CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                                    if (!checkBox.isChecked()) {
                                        mAdapter.deselectAll();
                                        mAdapter.markSelected(position, !checkBox.isChecked());
                                        mAdapter.notifyDataSetChanged();

                                        address = matches.get(position);

                                        Intent intent = new Intent(v.getContext(), NewEntryDescriptionActivity.class);
                                        startActivityForResult(intent, GET_DESCRIPTION);
                                    }
                                }
                            }
                    );

                    TextView select = (TextView) findViewById(R.id.currentColorLabel);
                    select.setVisibility(View.VISIBLE);
                }

            //catches case of no internet connection
            } catch (IOException exception) {
                Toast.makeText(this, "No network connection. Please check and try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Passes relevant fields back to map so it can be added */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //pass information back
        if (resultCode == GET_DESCRIPTION) {
            description = intent.getStringExtra("description");

            Intent newIntent = new Intent();
            newIntent.putExtra("latitude", address.getLatitude());
            newIntent.putExtra("longitude", address.getLongitude());
            newIntent.putExtra("name", address.getFeatureName());
            newIntent.putExtra("info", description);
            setResult(GET_DESTINATION, newIntent);

            finish();
        }
        //do nothing
        if (resultCode == NO_RESULT) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
