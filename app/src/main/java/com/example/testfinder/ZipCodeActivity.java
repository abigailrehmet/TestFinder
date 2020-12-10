package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZipCodeActivity extends AppCompatActivity {
    Intent intent;
    Button   mButton;
    Button mButton2;
    EditText mEdit;

    private TextInputEditText weather;
    private EditText from;
    private EditText to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_code);

        weather = findViewById(R.id.weather2);
        from = findViewById(R.id.from2);
        to = findViewById(R.id.to2);

        mButton = findViewById(R.id.button);
        mButton2 = findViewById(R.id.button2);
        mEdit   = findViewById(R.id.edittext);
        intent = new Intent(this, MapsActivity.class);

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Log.v("EditText", mEdit.getText().toString());
                        Geocoder geocoder = new Geocoder(ZipCodeActivity.this);
                        List<Address> list = new ArrayList<>();

                        try{
                            list = geocoder.getFromLocationName(mEdit.getText().toString(), 1);
                        }catch (IOException e) {
                            Toast.makeText(view.getContext(), "Invalid Zip Code!",Toast.LENGTH_SHORT).show();
                        }

                        boolean valid = true;
                        if (weather.getText().toString().isEmpty()) {
                            weather.setError("Field Required!");
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

                        if(list.size() > 0 && valid) {
                            intent.putExtra("ZIP", mEdit.getText().toString());
                            intent.putExtra("weather", weather.getText().toString().trim());
                            intent.putExtra("from", from.getText().toString().trim());
                            intent.putExtra("to", to.getText().toString().trim());
                            startActivity(intent);
                        }
                    }
                });

        mButton2.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        boolean valid = true;
                        if (weather.getText().toString().isEmpty()) {
                            weather.setError("Field Required!");
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
                            intent.putExtra("ZIP", "None");
                            intent.putExtra("weather", weather.getText().toString().trim());
                            intent.putExtra("from", from.getText().toString().trim());
                            intent.putExtra("to", to.getText().toString().trim());
                            startActivity(intent);
                        }
                    }
                });


    }
}
