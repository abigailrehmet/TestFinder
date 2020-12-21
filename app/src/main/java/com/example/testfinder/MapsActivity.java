package com.example.testfinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;

import static java.sql.DriverManager.println;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;
    private String zip;
    private String weather;
    private String from;
    private String to;
    private String state;
    private String county;
    private Double lat;
    private Double lng;
    private HashMap<String, String> ids;
    private LatLng latLng;
    private HashMap<String, HashMap<String, String>> markerMap;

    private static final String BASE_URL = "https://www.ugrad.cs.jhu.edu/~jcanedy1/get_events_map.php";
    private ArrayList<Event> events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        events = new ArrayList<>();
        markerMap = new HashMap<>();
        zip = getIntent().getStringExtra("ZIP");
        weather = getIntent().getStringExtra("weather");
        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");

        ids = new HashMap<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        //initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        //check permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when the permission is granted call method
            getCurrentLocation();
        } else {
            //when permission is not granted (denied)
            //request permission
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


    }


    private void getCurrentLocation() {
        //supresses warning- not used bc we already check for permission in OnCreate
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //when success
                if (location != null) {
                    //Sync the map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //mMap = googleMap;
                            if (zip.equals("None")) {
                                //initialize lat lng
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                //create marker options
                                MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                //Zoom map
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 50));
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                //add marker on map
                                googleMap.addMarker(options);

                                //Initialize url
                                /*String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + //Url
                                        "location=" + location.getLatitude() + "," + location.getLongitude() + //location latitude and lagitude
                                        "&radius=25000" + //Nearby radius
                                        "&keyword=" + "house" + //Place type
                                        "&sensor=true" + //Sensor
                                        "&key=AIzaSyBHLg1nZsUZhncmApmHksetMhXNzp9cZdU"; //Google maps api key

                                //Execute place task method and download json data
                                new PlaceTask().execute(url);*/

                                getTestEvents(googleMap);
                            } else {
                                geoLocate(googleMap, zip);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap.clear();
    }

    private void geoLocate(GoogleMap googleMap, String zip) {
        String sString = zip;
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(sString, 1);
        } catch (IOException e) {

        }

        if (list.size() > 0) {
            MarkerOptions options = new MarkerOptions().position(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            latLng = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude()), 10));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            googleMap.addMarker(options);

            //Initialize url
            /*String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + //Url
                    "location=" + list.get(0).getLatitude() + "," + list.get(0).getLongitude() + //location latitude and lagitude
                    "&radius=25000" + //Nearby radius
                    "&keyword=" + "earthquake" + //Place type
                    "&sensor=true" + //Sensor
                    "&key=AIzaSyBHLg1nZsUZhncmApmHksetMhXNzp9cZdU"; //Google maps api key

            //Execute place task method and download json data
            new PlaceTask().execute(url);*/
            getTestEvents(googleMap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //when permission granted call method
                getCurrentLocation();
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

         */

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.

        //if(marker.getId() != ) {
            String markID = marker.getId();
            String url = "https://maps.googleapis.com/maps/api/place/details/json?" + //Url
                    "place_id=" + ids.get(markID) + //marker id
                    "&key=AIzaSyBHLg1nZsUZhncmApmHksetMhXNzp9cZdU"; //Google maps api key

            //Execute place task method and download json data
            new PlaceTask().execute(url);
        //}

        /*
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_high_score_key), "name");
        editor.commit();
        //need key and place id to get data

         */
        String id = marker.getId();
        if (markerMap.get(id) != null) {
            findViewById(R.id.hide).setVisibility(View.VISIBLE);

            TextView weather = findViewById(R.id.rating);
            weather.setText("Event: " + (markerMap.get(id)).get("Event_type"));
            weather.setVisibility(View.VISIBLE);

            TextView place = findViewById(R.id.name);
            place.setText(markerMap.get(id).get("County").toLowerCase() + ", " + (markerMap.get(id)).get("State"));
            place.setVisibility(View.VISIBLE);

            TextView date = findViewById(R.id.monday);
            date.setVisibility(View.VISIBLE);
            date.setText((markerMap.get(id)).get("Date"));

            /////////////////////////////////////////

            TextView eventID = findViewById(R.id.open);
            String idString = (markerMap.get(id)).get("Event id");
            if (idString != null) {
                eventID.setVisibility(View.VISIBLE);
                eventID.setText(("Event ID: " + idString));
            }


            TextView magnitude = findViewById(R.id.tuesday);
            String mag = (markerMap.get(id)).get("Magnitude");
            if (mag != null) {
                magnitude.setVisibility(View.VISIBLE);
                magnitude.setText(("Magnitude: " + mag));
            }

            TextView dir_deaths = findViewById(R.id.wednesday);
            String ddeath = (markerMap.get(id)).get("Dir_death");
            if (ddeath != null) {
                dir_deaths.setVisibility(View.VISIBLE);
                dir_deaths.setText(("Direct Deaths: " + ddeath));
            }

            TextView indir_deaths = findViewById(R.id.thursday);
            String ideath = (markerMap.get(id)).get("Indir_death");
            if (ideath != null) {
                indir_deaths.setVisibility(View.VISIBLE);
                indir_deaths.setText(("Indirect Deaths: " + ideath));
            }

            TextView dir_inj = findViewById(R.id.friday);
            String dinj = (markerMap.get(id)).get("Dir_inj");
            if (dinj != null) {
                dir_inj.setVisibility(View.VISIBLE);
                dir_inj.setText(("Direct Injuries: " + dinj));
            }

            TextView indir_inj = findViewById(R.id.saturday);
            String iinj = (markerMap.get(id)).get("Indir_inj");
            if (iinj != null) {
                indir_inj.setVisibility(View.VISIBLE);
                indir_inj.setText(("Indirect Injuries: " + iinj));
            }

            TextView dp_cost = findViewById(R.id.sunday);
            String dp = (markerMap.get(id)).get("DP_cost");
            if (dp != null) {
                dp_cost.setVisibility(View.VISIBLE);
                dp_cost.setText(("Damaged Property Costs: " + dp));
            }

            TextView dc_cost = findViewById(R.id.website);
            String dc = (markerMap.get(id)).get("DC_cost");
            if (dc != null) {
                dc_cost.setVisibility(View.VISIBLE);
                dc_cost.setText(("Crop Damage Costs: " + dc));
            }

        } else {
            findViewById(R.id.hide).setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Your Selected Location", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //Initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //Execute parser task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        //Initialize url
        URL url = new URL(string);

        //Initialize connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //Connect connection
        connection.connect();
        //Initialize input stream
        InputStream stream = connection.getInputStream();
        //Initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //Initialize string builder
        StringBuilder builder = new StringBuilder();
        //Initialize string variable
        String line = "";
        //Use while loop
        while ((line = reader.readLine()) != null) {
            //Append line
            builder.append(line);
        }
        //Get append data
        String data = builder.toString();
        //Close reader
        reader.close();
        //Return data
        return data;


    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //Create json parser class
            JsonParser jsonParser = new JsonParser();
            //Initialize hash map list
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;
            try {
                //Init json object
                object = new JSONObject(strings[0]);
                //Parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Return map list
            return mapList;
        }


        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //Clear map
            //----------mMap.clear();------------
            //Use for loop
            /*if (!(hashMaps == null)) {
            for (int i = 0; i < hashMaps.size(); i++) {
                //Initialize hsh map
                final HashMap<String, String> hashMapList = hashMaps.get(i);


                findViewById(R.id.hide).setVisibility(View.VISIBLE);

                if (hashMapList.containsKey("yo")) {
                    TextView name = findViewById(R.id.name);
                    name.setText(hashMapList.get("name"));
                    name.setVisibility(View.VISIBLE);

                    TextView rating = findViewById(R.id.rating);
                    rating.setText("Rating: " + hashMapList.get("rating") + " out of 5");
                    rating.setVisibility(View.VISIBLE);

                    TextView open = findViewById(R.id.open);
                    if (hashMapList.get("open").equals("true")) {
                        open.setText("Open now");
                    } else {
                        open.setText("Closed right now");
                    }
                    open.setVisibility(View.VISIBLE);

                    TextView monday = findViewById(R.id.monday);
                    monday.setVisibility(View.VISIBLE);
                    monday.setText(hashMapList.get("monday"));

                    TextView tuesday = findViewById(R.id.tuesday);
                    tuesday.setVisibility(View.VISIBLE);
                    tuesday.setText(hashMapList.get("tuesday"));

                    TextView wednesday = findViewById(R.id.wednesday);
                    wednesday.setVisibility(View.VISIBLE);
                    wednesday.setText(hashMapList.get("wednesday"));

                    TextView thursday = findViewById(R.id.thursday);
                    thursday.setVisibility(View.VISIBLE);
                    thursday.setText(hashMapList.get("thursday"));

                    TextView friday = findViewById(R.id.friday);
                    friday.setVisibility(View.VISIBLE);
                    friday.setText(hashMapList.get("friday"));

                    TextView saturday = findViewById(R.id.saturday);
                    saturday.setVisibility(View.VISIBLE);
                    saturday.setText(hashMapList.get("saturday"));

                    TextView sunday = findViewById(R.id.sunday);
                    sunday.setVisibility(View.VISIBLE);
                    sunday.setText(hashMapList.get("sunday"));

                    TextView website = findViewById(R.id.website);
                    website.setVisibility(View.VISIBLE);
                    SpannableString content = new SpannableString("Visit Website");
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    website.setText(content);
                    website.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(hashMapList.get("website")));
                            startActivity(browserIntent);
                        }
                    });
                    website.setMovementMethod(LinkMovementMethod.getInstance());

                    Button more = findViewById(R.id.more);
                    more.setVisibility(View.VISIBLE);
                    more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), SiteActivity.class);
                            intent.putExtra("place_id", hashMapList.get("place_id"));
                            startActivity(intent);
                        }
                    });
                } else {

                    if (!(hashMapList.isEmpty())) {
                        //Get latitude
                        double lat = Double.parseDouble(hashMapList.get("lat"));
                        //Get longitude
                        double lng = Double.parseDouble(hashMapList.get("lng"));

                        double rating = Double.parseDouble(hashMapList.get("rating"));
                        //Get name
                        String name = hashMapList.get("name");
                        //Concat latitude and longitude
                        LatLng latLng = new LatLng(lat, lng);
                        //Init marker options
                        MarkerOptions options = new MarkerOptions();
                        //Set position
                        options.position(latLng);
                        //Set title
                        options.title(name);
                        //Add marker on map
                        if (rating > 4) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        } else if (rating > 2.5) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        } else {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        }
                        ids.put(mMap.addMarker(options).getId(), hashMapList.get("placeid"));

                    } else {
                        Toast.makeText(getApplicationContext(), "Site has no data", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.hide).setVisibility(View.INVISIBLE);

                    }
                }
            }
            }else {
                Toast.makeText(getApplicationContext(), "Site has no data",Toast.LENGTH_SHORT).show();
                findViewById(R.id.hide).setVisibility(View.INVISIBLE);
            }*/


        }
    }

    private void getTestEvents(final GoogleMap googleMap) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            double thislat = latLng.latitude;
                            double thislng = latLng.longitude;
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                double lat = Double.parseDouble(object.getString("Latitude"));
                                double lng = Double.parseDouble(object.getString("Longitude"));

                                //If the distances are within 250 miles
                                if(distance(thislat, thislng, lat, lng, "M") < 250) {
                                    MarkerOptions options1 = new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                                    Date d = df.parse(object.getString("Date"));
                                    String date = df2.format(d);

                                    int event_id = object.getInt("Event_id");
                                    int episode_id = object.getInt("Episode_id");
                                    String event_type = object.getString("Event_type");
                                    String county = object.getString("County");
                                    String state = object.getString("State");

                                    options1.title(event_type);
                                    Marker gm = googleMap.addMarker(options1);

                                    System.out.println("THE ID:" + gm.getId());


                                    markerMap.put(gm.getId(), new HashMap<String, String>());
                                    markerMap.get(gm.getId()).put("Event_id", String.valueOf(event_id));
                                    markerMap.get(gm.getId()).put("Episode_id", String.valueOf(episode_id));
                                    markerMap.get(gm.getId()).put("Event_type", event_type);
                                    markerMap.get(gm.getId()).put("County", county);
                                    markerMap.get(gm.getId()).put("State", state);
                                    markerMap.get(gm.getId()).put("Date", date);
                                    int mag = object.getInt("Magnitude");
                                    markerMap.get(gm.getId()).put("Magnitude", Integer.toString(mag));
                                    int year = object.getInt("Year");
                                    markerMap.get(gm.getId()).put("Year", Integer.toString(year));
                                    int direct_deaths = object.getInt("Direct_deaths");
                                    markerMap.get(gm.getId()).put("Dir_death", Integer.toString(direct_deaths));
                                    int indirect_deaths = object.getInt("Indirect_deaths");
                                    markerMap.get(gm.getId()).put("Ind_death", Integer.toString(indirect_deaths));
                                    int dp_cost = object.getInt("DP_cost");
                                    markerMap.get(gm.getId()).put("DP_cost", Integer.toString(dp_cost));
                                    int dc_cost = object.getInt("DC_cost");
                                    markerMap.get(gm.getId()).put("DC_cost", Integer.toString(dc_cost));
                                    int indirect_injuries = object.getInt("Indirect_injuries");
                                    markerMap.get(gm.getId()).put("Ind_inj", Integer.toString(indirect_injuries));
                                    int direct_injuries = object.getInt("direct_injuries");
                                    markerMap.get(gm.getId()).put("Dir_inj", Integer.toString(direct_injuries));
                                    String source = object.getString("Source");
                                    markerMap.get(gm.getId()).put("Source", source);

                                    Event e = new Event(county, state, date, event_id, episode_id, event_type, lat, lng, mag, year, direct_deaths, indirect_deaths, dp_cost, dc_cost, indirect_injuries, direct_injuries, source);
                                    events.add(e);
                                }
                            }
                        } catch (Exception e) {

                        }

                        /*event.setText("");
                        for (int i = 0; i < events.size(); i++) {
                            event.append("Event " + i + ": " + events.get(i).getDate() + " " + events.get(i).getEvent_id() + " " + events.get(i).getEpisode_id() + " " + events.get(i).getEvent_type() + "\n");
                        }*/

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("event_type", weather);
                params.put("from", from);
                params.put("to", to);
                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }
}
