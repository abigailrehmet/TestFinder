package com.example.testfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class SiteActivity extends AppCompatActivity {

    DatabaseReference reference;

    ImageButton btn_send;
    TextView text_send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);

        reference = FirebaseDatabase.getInstance().getReference().child("messages");

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {

                }
                else {
                    Toast.makeText(SiteActivity.this, "You can't send empty message", Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });
    }



}
