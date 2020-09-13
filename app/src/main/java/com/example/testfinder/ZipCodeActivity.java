package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.Marker;

public class ZipCodeActivity extends AppCompatActivity {
    Intent intent;
    Button   mButton;
    Button mButton2;
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_code);

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
                        intent.putExtra("ZIP", mEdit.getText().toString());
                        startActivity(intent);
                    }
                });

        mButton2.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        intent.putExtra("ZIP", "None");
                        startActivity(intent);
                    }
                });


    }
}
