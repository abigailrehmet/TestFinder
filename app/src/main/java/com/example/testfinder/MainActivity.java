package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://www.ugrad.cs.jhu.edu/~jcanedy1/get_events.php";
    //private static final String BASE_URL = "https://www.ugrad.cs.jhu.edu/~arehmet1/get_deaths.php";
    private ArrayList<Event> events;

    private Button get;
    private Button mmode;
    private TextView event;

    Intent intent;


    private TextInputEditText state;
    private TextInputEditText county;
    private TextInputEditText weather;
    private EditText from;
    private EditText to;

    private RadioButton direct_death;
    private RadioButton indirect_death;
    private RadioButton direct_injury;
    private RadioButton indirect_injury;
    private String buttonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, ZipCodeActivity.class);


        state = findViewById(R.id.state);
        county = findViewById(R.id.county);
        weather = findViewById(R.id.weather);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

        get = findViewById(R.id.get);
        mmode = findViewById(R.id.mmode);
        event = findViewById(R.id.event);

        direct_death = findViewById(R.id.direct_deaths);
        indirect_death = findViewById(R.id.indirect_deaths);
        direct_injury = findViewById(R.id.direct_injuries);
        indirect_injury = findViewById(R.id.indirect_injuries);
        buttonResponse = "";

        mmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTestEvents();
            }
        });

        events = new ArrayList<>();
    }

    //radio button values
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.direct_deaths:
                if (checked)
                    buttonResponse = "Direct death";
                    break;
            case R.id.indirect_deaths:
                if (checked)
                    buttonResponse = "Indirect death";
                    break;
            case R.id.direct_injuries:
                if (checked)
                    buttonResponse = "Direct injury";
                    break;
            case R.id.indirect_injuries:
                if (checked)
                    buttonResponse = "Indirect injury";
                    break;
        }
    }

    private void getTestEvents() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            events.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                                Date d = df.parse(object.getString("Date"));
                                String date = df2.format(d);

                                int event_id = object.getInt("Event_id");
                                int episode_id = object.getInt("Episode_id");
                                String event_type = object.getString("Event_type");
                                String county = object.getString("County");
                                String state = object.getString("State");
                                Double lat = Double.parseDouble(object.getString("Latitude"));
                                Double lng = Double.parseDouble(object.getString("Longitude"));
                                int mag = object.getInt("Magnitude");
                                int year = object.getInt("Year");
                                int direct_deaths = object.getInt("Direct_deaths");
                                int indirect_deaths = object.getInt("Indirect_deaths");
                                int dp_cost = object.getInt("DP_cost");
                                int dc_cost = object.getInt("DC_cost");
                                int indirect_injuries = object.getInt("Indirect_injuries");
                                int direct_injuries = object.getInt("direct_injuries");
                                String source = object.getString("Source");

                                Event e = new Event(county, state, date, event_id, episode_id, event_type, lat, lng, mag, year, direct_deaths, indirect_deaths, dp_cost, dc_cost, indirect_injuries, direct_injuries, source);
                                events.add(e);
                            }
                        } catch (Exception e) {

                        }

                        //event.setVisibility(View.INVISIBLE);
                        event.setText("");

                        for (int i = 0; i < events.size(); i++) {
                            if (buttonResponse.equals("")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + "\n");
                            }
                            else if (buttonResponse.equals("Direct death")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + "" + events.get(i).getDirect_deaths() + "\n");
                            }
                            else if (buttonResponse.equals("Indirect death")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + "" + events.get(i).getIndirect_deaths() + "\n");
                            }
                            else if (buttonResponse.equals("Direct injury")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + "" + events.get(i).getDirect_injuries() + "\n");
                            }
                            else {
                                //indirect injury
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + "" + events.get(i).getIndirect_injuries() + "\n");
                            }
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("state", state.getText().toString().trim());
                params.put("county", county.getText().toString().trim());
                params.put("event_type", weather.getText().toString().trim());
                params.put("from", from.getText().toString().trim());
                params.put("to", to.getText().toString().trim());


                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }
}