package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ZipCodeActivity extends AppCompatActivity {
    Intent intent;
    Button   mButton;
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip_code);

        mButton = findViewById(R.id.button);
        mEdit   = findViewById(R.id.edittext);
        intent = new Intent(this, MapsActivity.class);

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        Log.v("EditText", mEdit.getText().toString());
                        startActivity(intent);
                    }
                });



    }
}
