package com.example.testfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;

public class SiteActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOrdersDatabaseReference;
    private ChildEventListener mChildEventListener;
    private String place_id;
    private Button btn;
    private TextView message;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Messages> messageList;
    private MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        place_id = getIntent().getStringExtra("place_id");
        messageList = new ArrayList<>();
        message = findViewById(R.id.msg_input);
        btn = findViewById(R.id.send_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = message.getText().toString();
                Messages mes = new Messages(m);

                mOrdersDatabaseReference.child(place_id).push().setValue(mes);
            }
        });

        buildRecyclerView();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOrdersDatabaseReference = mFirebaseDatabase.getReference().child("reviews"); //Get by place_id

            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ArrayList<String> mes = new ArrayList<>();
                    if (dataSnapshot.getKey().equals(place_id)) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            mes.add(ds.child("message").getValue(String.class));
                        }

                        for (String m : mes) {
                            Messages msg = new Messages(m);
                            messageList.add(msg);
                        }

                        messagesAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ArrayList<String> mes = new ArrayList<>();
                    if (dataSnapshot.getKey().equals(place_id)) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            mes.add(ds.child("message").getValue(String.class));
                        }

                        for (String m : mes) {
                            Messages msg = new Messages(m);
                            messageList.add(msg);
                        }

                        messagesAdapter.notifyDataSetChanged();

                    }
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mOrdersDatabaseReference.addChildEventListener(mChildEventListener);
        }

    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.r);

        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        messagesAdapter = new MessagesAdapter(messageList, this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(messagesAdapter);

    }
}