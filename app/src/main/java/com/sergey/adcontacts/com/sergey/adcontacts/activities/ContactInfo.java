package com.sergey.adcontacts.com.sergey.adcontacts.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sergey.adcontacts.R;

public class ContactInfo extends AppCompatActivity {

    //Views
    TextView FIOTextView;
    TextView titleTextView;
    TextView phoneTextView;
    TextView emailTextView;
    TextView companyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        //Getting intent
        Intent intent = getIntent();
        String FIO = intent.getStringExtra("FIO");
        String title = intent.getStringExtra("title");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String company = intent.getStringExtra("company");

        //Initialize Views
        FIOTextView = (TextView) findViewById(R.id.FIOTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
        companyTextView = (TextView) findViewById(R.id.companyTextView);

        //Setting text to views
        FIOTextView.setText(FIO);
        titleTextView.setText(title);
        phoneTextView.setText(phone);
        emailTextView.setText(email);
        companyTextView.setText(company);
    }
}
