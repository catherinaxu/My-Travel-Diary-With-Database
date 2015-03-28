package com.example.catherinaxu.mycityfinder;


import android.app.*;
import android.content.*;
import android.graphics.Typeface;
import android.location.*;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.util.*;


public class CityFinderActivity extends Activity
        implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private static final int GET_DESTINATION = 10;
    private static final String DEBUG = "DEBUG";
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_finder);
        db = new DatabaseHandler(this);

        //change font of title
        TextView title = (TextView) findViewById(R.id.title);
        Typeface font_bold = Typeface.createFromAsset(getAssets(), "ostrich-black.ttf");
        title.setTypeface(font_bold);

        //change font of button
        Typeface font = Typeface.createFromAsset(getAssets(), "ostrich-regular.ttf");
        Button button = (Button) findViewById(R.id.button);
        button.setTypeface(font);

        getActionBar().hide();
        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.the_map);
        mf.getMapAsync(this);                  // calls onMapReady when loaded

        //places markers all markers that are currently in the database
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {    // map is loaded but not laid out yet
        this.map = map;
        map.setOnMapLoadedCallback(this);      // calls onMapLoaded when layout done
    }

    @Override
    public void onMapLoaded() {
        // code to run when the map has loaded
        map.setOnMarkerClickListener(this);

        List<Loc> locations = db.getAllLocations();

        for (Loc loc : locations) {
            Log.d(DEBUG, "List size: " + locations.size() + " Feature name: " + loc.getFeatureName() + " Lat: " + loc.getLatitude() +
                    " Long: " + loc.getLongitude() + " Description: " + loc.getDescription());
            map.addMarker(new MarkerOptions()
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .title(loc.getFeatureName())
                            .snippet(loc.getDescription())
            );
        }
        // read user's current location, if possible
    }

    public void createNewEntry(View view) {
        Intent intent = new Intent(this, NewEntryActivity.class);
        startActivityForResult(intent, GET_DESTINATION);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == GET_DESTINATION) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            String name = intent.getStringExtra("name");
            String info = intent.getStringExtra("info");
            map.addMarker(new MarkerOptions()
                              .position(new LatLng(latitude, longitude))
                              .title(name)
                              .snippet(info)
            );
            db.addLocation(name, latitude, longitude, info);
        }
    }

    /*
     * Returns the user's current location as a LatLng object.
     * Returns null if location could not be found (such as in an AVD emulated virtual device).
     */
    private LatLng getMyLocation() {
        // try to get location three ways: GPS, cell/wifi network, and 'passive' mode
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            // fall back to network if GPS is not available
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (loc == null) {
            // fall back to "passive" location if GPS and network are not available
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (loc == null) {
            return null;   // could not get user's location
        } else {
            double myLat = loc.getLatitude();
            double myLng = loc.getLongitude();
            return new LatLng(myLat, myLng);
        }
    }

    /*
     * Called when user clicks on any of the city map markers.
     * Adds a line from the user's location to that city.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
