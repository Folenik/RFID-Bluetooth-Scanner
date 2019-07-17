package com.kamil.rfid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WiFiActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private AppCompatTextView mTextView;
    private AppCompatButton mButton;
    public String mString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        init();
    }

    private void init()
    {
        mTextView = (AppCompatTextView) findViewById(R.id.testTEXTVIEW);
        mButton = (AppCompatButton) findViewById(R.id.testBUTTON);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mButton.setOnClickListener(this);
    }

    private void checkDataBase() {
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mString = dataSnapshot.child("admin").getValue().toString();
                mTextView.setText(mString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == mButton) {
            checkDataBase();
        }
    }
}
