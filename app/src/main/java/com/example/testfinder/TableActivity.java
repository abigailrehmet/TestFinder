package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TableActivity extends AppCompatActivity {
    private static final String EVENT_URL = "https://www.ugrad.cs.jhu.edu/~jcanedy1/get_events.php";
    private static final String DEM_URL = "https://www.ugrad.cs.jhu.edu/~jcanedy1/populations.php";
    private static final String BH_URL = "https://www.ugrad.cs.jhu.edu/~jcanedy1/BH_percentage.php";
    private static final String StateTemp_URL = "https://www.ugrad.cs.jhu.edu/~jcanedy1/State_temps.php";
    private static final String TotalDeath_URL = "https://www.ugrad.cs.jhu.edu/~arehmet1/total_deaths.php";
    private static final String EventCount_URL = "https://www.ugrad.cs.jhu.edu/~arehmet1/event_count.php";
    private static final String NatDisaster_URL = "https://www.ugrad.cs.jhu.edu/~arehmet1/natty_disasters.php";
    private ArrayList<Event> events;
    private ArrayList<Demographic> demographics;
    private ArrayList<BHPercentage> bhPercentages;
    private ArrayList<StateTemp> stateTemps;
    private ArrayList<TotalDeath> totalDeaths;
    private ArrayList<EventCount> eventCounts;
    private ArrayList<NattyDisaster> nattyDisasters;

    private Button showEvents;
    private Button demButton;
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

    private BarChart barChart;

    private boolean processingDemo = false;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        intent = new Intent(this, ZipCodeActivity.class);

        state = findViewById(R.id.state);
        county = findViewById(R.id.county);
        weather = findViewById(R.id.weather);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);


        event = findViewById(R.id.event);
        demButton = findViewById(R.id.demographicsBtn);

        direct_death = findViewById(R.id.direct_deaths);
        indirect_death = findViewById(R.id.indirect_deaths);
        direct_injury = findViewById(R.id.direct_injuries);
        indirect_injury = findViewById(R.id.indirect_injuries);
        buttonResponse = "";

        showEvents = findViewById(R.id.showEventsBtn);
        showEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                county.setError(null);
                if (state.getText().toString().isEmpty()) {
                    state.setError("Field Required!");
                    valid = false;
                } else if (state.getText().toString().length() != 2) {
                    state.setError("Must be 2 letter abbr.!");
                    valid = false;
                }

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = df.parse(from.getText().toString());
                } catch (Exception e) {
                    from.setError("Invalid date!");
                    valid = false;
                }

                try {
                    Date date = df.parse(to.getText().toString());
                } catch (Exception e) {
                    to.setError("Invalid date!");
                    valid = false;
                }


                if (valid) {
                    //getTestEvents();
                    //getStateTemps();
                    //getBHPercentage();
                    //getTotalDeaths();
                    getEventCount();
                    //getNattyDisasters();
                }
            }
        });

        demButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                if (state.getText().toString().isEmpty()) {
                    state.setError("Field Required!");
                    valid = false;
                } else if (state.getText().toString().length() != 2) {
                    state.setError("Must be 2 letter abbr.!");
                    valid = false;
                }

                if (county.getText().toString().isEmpty()) {
                    county.setError("Field Required!");
                    valid = false;
                }

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    Date date = df.parse(from.getText().toString());
                } catch (Exception e) {
                    from.setError("Invalid date!");
                    valid = false;
                }

                try {
                    Date date = df.parse(to.getText().toString());
                } catch (Exception e) {
                    to.setError("Invalid date!");
                    valid = false;
                }

                if (valid && !processingDemo) {
                    getDemographics();
                }
            }
        });



        events = new ArrayList<>();
        demographics = new ArrayList<>();
        bhPercentages = new ArrayList<>();
        stateTemps = new ArrayList<>();
        totalDeaths = new ArrayList<>();
        eventCounts = new ArrayList<>();
        nattyDisasters = new ArrayList<>();

    }

    //radio button values (death/injuries)
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

    //radio button values (advanced sql queries)
    public void onAdvancedStatementsClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            //do nothing for noe
        }
    }

    private void getTestEvents() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EVENT_URL,
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
                                int direct_injuries = object.getInt("Direct_injuries");
                                String source = object.getString("Source");

                                Event e = new Event(county, state, date, event_id, episode_id, event_type, lat, lng, mag, year, direct_deaths, indirect_deaths, dp_cost, dc_cost, indirect_injuries, direct_injuries, source);
                                events.add(e);
                            }
                        } catch (Exception e) {

                        }

                        //event.setVisibility(View.INVISIBLE);
                        event.setText("");
                        event.append("Event: " + "    " + "County" + "    " + "State" + "    " + "Date" + "         " + "Type" + "\n");

                        for (int i = 0; i < events.size(); i++) {
                            if (buttonResponse.equals("")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + "\n");
                            }
                            else if (buttonResponse.equals("Direct death")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + " " + events.get(i).getDirect_deaths() + "\n");
                            }
                            else if (buttonResponse.equals("Indirect death")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + " " + events.get(i).getIndirect_deaths() + "\n");
                            }
                            else if (buttonResponse.equals("Direct injury")) {
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + " " + events.get(i).getDirect_injuries() + "\n");
                            }
                            else {
                                //indirect injury
                                event.append("Event " + i + ": " + events.get(i).getCounty() + " " + events.get(i).getState() + " " + events.get(i).getDate() + " " + events.get(i).getEvent_type() + " " + events.get(i).getIndirect_injuries() + "\n");
                            }
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
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


    private void getDemographics() {
        processingDemo = true;
        progress = findViewById(R.id.progressBar1);
        progress.setVisibility(View.VISIBLE);
        progress = findViewById(R.id.progressBar2);
        progress.setVisibility(View.VISIBLE);
        barChart = findViewById(R.id.bargraph1);
        barChart.setVisibility(View.GONE);
        barChart = findViewById(R.id.bargraph2);
        barChart.setVisibility(View.GONE);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DEM_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            demographics.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                String STNAME = object.getString("STNAME");
                                String CTYNAME = object.getString("CTYNAME");
                                int YEAR = object.getInt("YEAR");
                                int AGEGRP = object.getInt("AGEGRP");
                                int TOT_POP = object.getInt("TOT_POP");
                                int TOT_MALE = object.getInt("TOT_MALE");
                                int TOT_FEMALE = object.getInt("TOT_FEMALE");
                                int WA_MALE = object.getInt("WA_MALE");
                                int WA_FEMALE = object.getInt("WA_FEMALE");
                                int BA_MALE = object.getInt("BA_MALE");
                                int BA_FEMALE = object.getInt("BA_FEMALE");
                                int IA_MALE = object.getInt("IA_MALE");
                                int IA_FEMALE = object.getInt("IA_FEMALE");
                                int AA_MALE = object.getInt("AA_MALE");
                                int AA_FEMALE = object.getInt("AA_FEMALE");
                                int NA_MALE = object.getInt("NA_MALE");
                                int NA_FEMALE = object.getInt("NA_FEMALE");
                                int TOM_MALE = object.getInt("TOM_MALE");
                                int TOM_FEMALE = object.getInt("TOM_FEMALE");
                                int WAC_MALE = object.getInt("WAC_MALE");
                                int WAC_FEMALE = object.getInt("WAC_FEMALE");
                                int BAC_MALE = object.getInt("BAC_MALE");
                                int BAC_FEMALE = object.getInt("BAC_FEMALE");
                                int IAC_MALE = object.getInt("IAC_MALE");
                                int IAC_FEMALE = object.getInt("IAC_FEMALE");
                                int AAC_MALE = object.getInt("AAC_MALE");
                                int AAC_FEMALE = object.getInt("AAC_FEMALE");
                                int NAC_MALE = object.getInt("NAC_MALE");
                                int NAC_FEMALE = object.getInt("NAC_FEMALE");
                                int NH_MALE = object.getInt("NH_MALE");
                                int NH_FEMALE = object.getInt("NH_FEMALE");
                                int NHWA_MALE = object.getInt("NHWA_MALE");
                                int NHWA_FEMALE = object.getInt("NHWA_FEMALE");
                                int NHBA_MALE = object.getInt("NHBA_MALE");
                                int NHBA_FEMALE = object.getInt("NHBA_FEMALE");
                                int NHIA_MALE = object.getInt("NHIA_MALE");
                                int NHIA_FEMALE = object.getInt("NHIA_FEMALE");
                                int NHAA_MALE = object.getInt("NHAA_MALE");
                                int NHAA_FEMALE = object.getInt("NHAA_FEMALE");
                                int NHNA_MALE = object.getInt("NHNA_MALE");
                                int NHNA_FEMALE = object.getInt("NHNA_FEMALE");
                                int NHTOM_MALE = object.getInt("NHTOM_MALE");
                                int NHTOM_FEMALE = object.getInt("NHTOM_FEMALE");
                                int NHWAC_MALE = object.getInt("NHWAC_MALE");
                                int NHWAC_FEMALE = object.getInt("NHWAC_FEMALE");
                                int NHBAC_MALE = object.getInt("NHBAC_MALE");
                                int NHBAC_FEMALE = object.getInt("NHBAC_FEMALE");
                                int NHIAC_MALE = object.getInt("NHIAC_MALE");
                                int NHIAC_FEMALE = object.getInt("NHIAC_FEMALE");
                                int NHAAC_MALE = object.getInt("NHAAC_MALE");
                                int NHAAC_FEMALE = object.getInt("NHAAC_FEMALE");
                                int NHNAC_MALE = object.getInt("NHNAC_MALE");
                                int NHNAC_FEMALE = object.getInt("NHNAC_FEMALE");
                                int H_MALE = object.getInt("H_MALE");
                                int H_FEMALE = object.getInt("H_FEMALE");
                                int HWA_MALE = object.getInt("HWA_MALE");
                                int HWA_FEMALE = object.getInt("HWA_FEMALE");
                                int HBA_MALE = object.getInt("HBA_MALE");
                                int HBA_FEMALE = object.getInt("HBA_FEMALE");
                                int HIA_MALE = object.getInt("HIA_MALE");
                                int HIA_FEMALE = object.getInt("HIA_FEMALE");
                                int HAA_MALE = object.getInt("HAA_MALE");
                                int HAA_FEMALE = object.getInt("HAA_FEMALE");
                                int HNA_MALE = object.getInt("HNA_MALE");
                                int HNA_FEMALE = object.getInt("HNA_FEMALE");
                                int HTOM_MALE = object.getInt("HTOM_MALE");
                                int HTOM_FEMALE = object.getInt("HTOM_FEMALE");
                                int HWAC_MALE = object.getInt("HWAC_MALE");
                                int HWAC_FEMALE = object.getInt("HWAC_FEMALE");
                                int HBAC_MALE = object.getInt("HBAC_MALE");
                                int HBAC_FEMALE = object.getInt("HBAC_FEMALE");
                                int HIAC_MALE = object.getInt("HIAC_MALE");
                                int HIAC_FEMALE = object.getInt("HIAC_FEMALE");
                                int HAAC_MALE = object.getInt("HAAC_MALE");
                                int HAAC_FEMALE = object.getInt("HAAC_FEMALE");
                                int HNAC_MALE = object.getInt("HNAC_MALE");
                                int HNAC_FEMALE = object.getInt("HNAC_FEMALE");

                                Demographic demo = new Demographic(STNAME, CTYNAME,YEAR ,AGEGRP ,TOT_POP ,TOT_MALE ,TOT_FEMALE ,WA_MALE ,WA_FEMALE ,BA_MALE ,BA_FEMALE ,IA_MALE ,IA_FEMALE ,AA_MALE ,AA_FEMALE ,NA_MALE ,NA_FEMALE ,TOM_MALE ,TOM_FEMALE ,WAC_MALE ,WAC_FEMALE ,BAC_MALE ,BAC_FEMALE ,IAC_MALE ,IAC_FEMALE ,AAC_MALE ,AAC_FEMALE ,NAC_MALE ,NAC_FEMALE ,NH_MALE ,NH_FEMALE ,NHWA_MALE ,NHWA_FEMALE ,NHBA_MALE ,NHBA_FEMALE ,NHIA_MALE ,NHIA_FEMALE ,NHAA_MALE ,NHAA_FEMALE ,NHNA_MALE ,NHNA_FEMALE ,NHTOM_MALE ,NHTOM_FEMALE ,NHWAC_MALE ,NHWAC_FEMALE ,NHBAC_MALE ,NHBAC_FEMALE ,NHIAC_MALE ,NHIAC_FEMALE ,NHAAC_MALE ,NHAAC_FEMALE ,NHNAC_MALE ,NHNAC_FEMALE ,H_MALE ,H_FEMALE ,HWA_MALE ,HWA_FEMALE ,HBA_MALE ,HBA_FEMALE ,HIA_MALE ,HIA_FEMALE ,HAA_MALE ,HAA_FEMALE ,HNA_MALE ,HNA_FEMALE ,HTOM_MALE ,HTOM_FEMALE ,HWAC_MALE ,HWAC_FEMALE ,HBAC_MALE ,HBAC_FEMALE ,HIAC_MALE ,HIAC_FEMALE ,HAAC_MALE ,HAAC_FEMALE ,HNAC_MALE ,HNAC_FEMALE);

                                demographics.add(demo);
                            }
                        } catch (Exception e) {
                            processingDemo = false;
                        }

                        //event.setVisibility(View.INVISIBLE);
                        event.setText("");
                        System.out.println(demographics.size());
                        /*for (int i = 0; i < demographics.size(); i++) {
                            event.append("Demographic " + i + ": " + demographics.get(i).getCTYNAME() + " " +
                                    demographics.get(i).getSTNAME() + " " +
                                    demographics.get(i).getAA_FEMALE() + " " +
                                    demographics.get(i).getAA_MALE() + " " +
                                    demographics.get(i).getAAC_FEMALE() + " " +
                                    demographics.get(i).getAAC_MALE() + " " +
                                    demographics.get(i).getAGEGRP() + " " +
                                    demographics.get(i).getBA_FEMALE() + " " +
                                    demographics.get(i).getBA_MALE() + " " +
                                    demographics.get(i).getBAC_FEMALE() + " " +
                                    demographics.get(i).getBAC_MALE() + " " +
                                    demographics.get(i).getH_FEMALE() + " " +
                                    demographics.get(i).getH_MALE() + " " +
                                    demographics.get(i).getHAA_FEMALE() + " " +
                                    demographics.get(i).getHAA_MALE() + " " +
                                    demographics.get(i).getHAAC_FEMALE() + " " +
                                    demographics.get(i).getHAAC_MALE() + " " +
                                    demographics.get(i).getHBA_FEMALE() + " " +
                                    demographics.get(i).getHBA_MALE() + " " +
                                    demographics.get(i).getHIA_FEMALE() + " " +
                                    demographics.get(i).getHIA_MALE() + " " +
                                    demographics.get(i).getHIAC_FEMALE() + " " +
                                    demographics.get(i).getHIAC_MALE() + " " +
                                    demographics.get(i).getHNA_FEMALE() + " " +
                                    demographics.get(i).getHNA_MALE() + " " +
                                    demographics.get(i).getHNAC_FEMALE() + " " +
                                    demographics.get(i).getHNAC_MALE() + " " +
                                    demographics.get(i).getHTOM_FEMALE() + " " +
                                    demographics.get(i).getHTOM_MALE() + " " +
                                    demographics.get(i).getHWA_FEMALE() + " " +
                                    demographics.get(i).getHWA_MALE() + " " +
                                    demographics.get(i).getHWAC_FEMALE() + " " +
                                    demographics.get(i).getHWAC_MALE() + " " +
                                    demographics.get(i).getIA_FEMALE() + " " +
                                    demographics.get(i).getIA_MALE() + " " +
                                    demographics.get(i).getIAC_FEMALE() + " " +
                                    demographics.get(i).getIAC_MALE() + " " +
                                    demographics.get(i).getNA_FEMALE() + " " +
                                    demographics.get(i).getNA_MALE() + " " +
                                    demographics.get(i).getNAC_FEMALE() + " " +
                                    demographics.get(i).getNAC_MALE() + " " +
                                    demographics.get(i).getNH_FEMALE() + " " +
                                    demographics.get(i).getNH_MALE() + " " +
                                    demographics.get(i).getNHAA_FEMALE() + " " +
                                    demographics.get(i).getNHAA_MALE() + " " +
                                    demographics.get(i).getNHAAC_FEMALE() + " " +
                                    demographics.get(i).getNHAAC_MALE() + " " +
                                    demographics.get(i).getNHBA_FEMALE() + " " +
                                    demographics.get(i).getNHBA_MALE() + " " +
                                    demographics.get(i).getNHBAC_FEMALE() + " " +
                                    demographics.get(i).getNHBAC_MALE() + " " +
                                    demographics.get(i).getNHIA_FEMALE() + " " +
                                    demographics.get(i).getNHIA_MALE() + " " +
                                    demographics.get(i).getNHIAC_FEMALE() + " " +
                                    demographics.get(i).getNHIAC_MALE() + " " +
                                    demographics.get(i).getNHNA_FEMALE() + " " +
                                    demographics.get(i).getNHNA_MALE() + " " +
                                    demographics.get(i).getNHNAC_FEMALE() + " " +
                                    demographics.get(i).getNHNAC_MALE() + " " +
                                    demographics.get(i).getTOT_MALE() + " " +
                                    demographics.get(i).getTOT_FEMALE() + " " +
                                    demographics.get(i).getTOT_POP() +

                                    //cut some short
                                    "\n");
                        }*/
                        //Set up first chart
                        barChart = findViewById(R.id.bargraph1);
                        barChart.setVisibility(View.VISIBLE);
                        createChart(barEntriesM(demographics, 2018), barEntriesF(demographics, 2018), 2018);
                        progress = findViewById(R.id.progressBar1);
                        progress.setVisibility(View.GONE);

                        //Set up second chart
                        barChart = findViewById(R.id.bargraph2);
                        barChart.setVisibility(View.VISIBLE);
                        createChart(barEntriesM(demographics, 2019), barEntriesF(demographics, 2019), 2019);
                        progress = findViewById(R.id.progressBar2);
                        progress.setVisibility(View.GONE);

                        processingDemo = false;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                processingDemo = false;
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("state", state.getText().toString().trim());
                params.put("county", county.getText().toString().trim());
                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }

    private void createChart(ArrayList<BarEntry> barEntriesM, ArrayList<BarEntry> barEntriesF, int year) {
        //Y-axis
        BarDataSet barDataSet1 = new BarDataSet(barEntriesM, "Male (" + year + ")");
        barDataSet1.setColor(Color.RED);
        BarDataSet barDataSet2 = new BarDataSet(barEntriesF, "Female (" + year + ")");
        barDataSet2.setColor(Color.BLUE);

        BarData data = new BarData(barDataSet1, barDataSet2);
        barChart.setData(data);
        barChart.getDescription().setEnabled(false);

        //X-axis
        String[] pops = new String[] {"Total", "White", "Black"};
        final int NUM_CATEGORIES = 3;
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(pops));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawAxisLine(false);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(3);

        float barSpace = 0;
        float groupSpace = 0.68f;

        data.setBarWidth(0.16f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace,barSpace)*NUM_CATEGORIES);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.groupBars(0, groupSpace, barSpace);

        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private ArrayList<BarEntry> barEntriesM(ArrayList<Demographic> demographics, int year) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int TOT_MALE = 0;
        int WA_MALE = 0;
        int BA_MALE = 0;
        for (Demographic d: demographics) {
            //Totals rows
            if (d.getAGEGRP() == 0 && d.getYEAR() == year) {
                TOT_MALE += d.getTOT_MALE();
                WA_MALE += d.getWA_MALE();
                BA_MALE += d.getBA_MALE();
            }
        }
        barEntries.add(new BarEntry(1, TOT_MALE));
        barEntries.add(new BarEntry(2, WA_MALE));
        barEntries.add(new BarEntry(3, BA_MALE));
        return barEntries;
    }

    private ArrayList<BarEntry> barEntriesF(ArrayList<Demographic> demographics, int year) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        int TOT_FEMALE = 0;
        int WA_FEMALE = 0;
        int BA_FEMALE = 0;
        for (Demographic d: demographics) {
            //Totals rows
            if (d.getAGEGRP() == 0 && d.getYEAR() == year) {
                TOT_FEMALE += d.getTOT_FEMALE();
                WA_FEMALE += d.getWA_FEMALE();
                BA_FEMALE += d.getBA_FEMALE();
            }
        }
        barEntries.add(new BarEntry(1, TOT_FEMALE));
        barEntries.add(new BarEntry(2, WA_FEMALE));
        barEntries.add(new BarEntry(3, BA_FEMALE));

        return barEntries;
    }

    private void getBHPercentage() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BH_URL,
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

                                int mind = object.getInt("Mind");
                                String county = object.getString("County");
                                String state = object.getString("State");
                                Double hispanic_rate = Double.parseDouble(object.getString("Hispanic_rate"));
                                Double black_rate = Double.parseDouble(object.getString("Black_rate"));

                                BHPercentage b = new BHPercentage(mind, hispanic_rate, black_rate, date, state, county);
                                bhPercentages.add(b);
                                System.out.println("THESE ARE THE BH PERCENTAGES!" + bhPercentages);
                            }
                        } catch (Exception e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("event_type", weather.getText().toString().trim());

                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }

    private void getStateTemps() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, StateTemp_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            stateTemps.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                String state = object.getString("State");
                                Double avgMax = Double.parseDouble(object.getString("avgMax"));
                                Double avgMin = Double.parseDouble(object.getString("avgMin"));
                                Double max = Double.parseDouble(object.getString("max"));
                                Double min = Double.parseDouble(object.getString("min"));

                                StateTemp s = new StateTemp(state, avgMax, avgMin, max, min);
                                stateTemps.add(s);
                                System.out.println("THESE ARE THE BH PERCENTAGES!" + stateTemps);
                            }
                        } catch (Exception e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("event_type", weather.getText().toString().trim());
                params.put("month", "07");
                params.put("year", "2018");

                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }

    private void getTotalDeaths() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, TotalDeath_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            totalDeaths.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                int direct_deaths = object.getInt("Direct_deaths");

                                TotalDeath t = new TotalDeath(direct_deaths);
                                totalDeaths.add(t);
                                System.out.println("total deaths or injuries-->" + totalDeaths);
                            }
                        } catch (Exception e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("weather", weather.getText().toString().trim());
                params.put("year", "2018");
                params.put("number", "4"); //ARE YOU FREAKING KIDDING MEEE LOL

                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }

    private void getEventCount() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EventCount_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            eventCounts.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                int event_id = object.getInt("Event_id");

                                EventCount e = new EventCount(event_id);
                                eventCounts.add(e);
                                System.out.println("number of events-->" + eventCounts);
                            }
                        } catch (Exception e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("state1", "1");
                params.put("state2", "2");
                params.put("state3", "3");
                params.put("weather", weather.getText().toString().trim());
                params.put("number", "4");

                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }

    private void getNattyDisasters() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, NatDisaster_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            System.out.println(array);
                            nattyDisasters.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);

                                String event_type = object.getString("Event_type");
                                String county = object.getString("County");

                                NattyDisaster n = new NattyDisaster(event_type, county);
                                nattyDisasters.add(n);
                                System.out.println("natural disasters-->" + nattyDisasters);
                            }
                        } catch (Exception e) {

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TableActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("number", "1");
                params.put("state", state.getText().toString().trim());

                return params;
            }
        };
        //execute your request
        queue.add(stringRequest);
    }
}

