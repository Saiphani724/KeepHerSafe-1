package com.example.keephersafeGPStrial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    TextView user, password, height, weight, bloodGroup, age, emg1, emg2;
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = new DatabaseHelper(this);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("user");
        user = (TextView)findViewById(R.id.user);
//        password = (TextView)findViewById(R.id.password);
        height = (TextView)findViewById(R.id.height);
        weight = (TextView)findViewById(R.id.weight);
        age = (TextView)findViewById(R.id.age);
//        bloodGroup = (TextView)findViewById(R.id.bloodGroup);
        emg1 = (TextView)findViewById(R.id.emg1);
        emg2 = (TextView)findViewById(R.id.emg2);
        user.setText(userName);
//        password.setText(db.getPassword(userName));
        height.setText(db.getHeight(userName));
        weight.setText(db.getWeight(userName));
//        bloodGroup.setText("O+");
        age.setText(db.getAge(userName));
        emg1.setText(db.getEmg1(userName));
        emg2.setText(db.getEmg2(userName));
    }
}