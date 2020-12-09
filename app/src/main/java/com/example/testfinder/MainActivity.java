package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private ArrayList<Event> events;

    private Button get;
    private TextView event;

    private TextInputEditText state;
    private TextInputEditText county;
    private TextInputEditText weather;
    private EditText from;
    private EditText to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = findViewById(R.id.state);
        county = findViewById(R.id.county);
        weather = findViewById(R.id.weather);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

        get = findViewById(R.id.get);
        event = findViewById(R.id.event);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTestEvents();
            }
        });

        events = new ArrayList<>();
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
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
                                Date d = df.parse(object.getString("Date"));
                                String date = df2.format(d);

                                int event_id = object.getInt("Event_id");
                                int episode_id = object.getInt("Episode_id");
                                String event_type = object.getString("Event_type");
                                Event e = new Event(date, event_id, episode_id, event_type);
                                events.add(e);
                            }
                        } catch (Exception e) {

                        }
                        event.setText("");
                        for (int i = 0; i < events.size(); i++) {
                            event.append("Event " + i + ": " + events.get(i).getDate() + " " + events.get(i).getEvent_id() + " " + events.get(i).getEpisode_id() + " " + events.get(i).getEvent_type() + "\n");
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