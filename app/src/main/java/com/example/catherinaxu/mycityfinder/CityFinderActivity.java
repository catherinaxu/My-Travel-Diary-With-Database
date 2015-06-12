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
    private HashMap<Marker, Loc> allMarkers = new HashMap();
    private DatabaseHandler db;
    private Marker clickedMarker;

    private static final int GET_DESTINATION = 10;
    private static final int GET_DESCRIPTION = 11;
    private static final int NO_RESULT = 9;
    private static final String DEBUG = "DEBUG";
    private static final String DEBUG2 = "DEBUG2";

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
        Button delete = (Button) findViewById(R.id.delete);
        Button update = (Button) findViewById(R.id.update);
        button.setTypeface(font);
        delete.setTypeface(font);
        update.setTypeface(font);

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

        // disable toolbar
        UiSettings uisettings = map.getUiSettings();
        uisettings.setMapToolbarEnabled(false);

        List<Loc> locations = db.getAllLocations();

        for (Loc loc : locations) {
            Log.d("debug", loc.feature_name);
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .title(loc.getFeatureName())
                            .snippet(loc.getDescription())
            );
            allMarkers.put(marker, loc);
        }

        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng pt) {
                Button delete = (Button) findViewById(R.id.delete);
                Button update = (Button) findViewById(R.id.update);

                delete.setVisibility(View.INVISIBLE);
                update.setVisibility(View.INVISIBLE);
            }
        });



    }

    public void createNewEntry(View view) {
        Intent intent = new Intent(this, NewEntryActivity.class);
        startActivityForResult(intent, GET_DESTINATION);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == GET_DESTINATION) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            String name = intent.getStringExtra("name");
            String info = intent.getStringExtra("info");
            Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(name)
                            .snippet(info)
            );

            db.addLocation(name, latitude, longitude, info);

            Loc loc = db.getLocationByCoordinates(latitude, longitude);
            allMarkers.put(marker, loc);
            //if the person presses back before the result a result is obtained
        } else if (resultCode == GET_DESCRIPTION) {
            String description = intent.getStringExtra("description");

            //delete old marker
            Loc loc = allMarkers.get(clickedMarker);
            allMarkers.remove(clickedMarker);
            clickedMarker.setVisible(false);

            Marker newMarker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                            .title(loc.getFeatureName())
                            .snippet(description)
            );

            //update new marker
            loc.description = description;
            allMarkers.put(newMarker, loc);

            db.updateLocation(loc);
        } else if (resultCode == NO_RESULT) {
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
     * Called when user clicks on any of the city map markers. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        final Loc loc = allMarkers.get(marker);
        Button delete = (Button) findViewById(R.id.delete);
        Button update = (Button) findViewById(R.id.update);

        delete.setVisibility(View.VISIBLE);
        update.setVisibility(View.VISIBLE);

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db.deleteLocation(loc);
                marker.setVisible(false);
                allMarkers.remove(marker);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickedMarker = marker;
                Intent intent = new Intent(v.getContext(), NewEntryDescriptionActivity.class);
                startActivityForResult(intent, GET_DESCRIPTION);
            }
        });
        return false;
    }
}
