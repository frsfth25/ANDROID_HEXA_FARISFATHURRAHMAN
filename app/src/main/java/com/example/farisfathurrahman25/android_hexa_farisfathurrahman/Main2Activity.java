package com.example.farisfathurrahman25.android_hexa_farisfathurrahman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;


public class Main2Activity extends AppCompatActivity {

    CircleImageView profileImage;

    TextView textFullname;
    TextView textUsername;
    TextView textEmail;
    TextView textAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);

        textFullname = findViewById(R.id.txtFullname);
        textUsername = findViewById(R.id.txtUsername);
        textEmail = findViewById(R.id.txtEmail);
        textAddress = findViewById(R.id.txtAddress);

        Glide.with(this).load("http://" + getIntent().getStringExtra("photo")).into(profileImage);

        textFullname.setText(getIntent().getStringExtra("fullname"));
        textUsername.setText(getIntent().getStringExtra("username"));
        textEmail.setText(getIntent().getStringExtra("email"));
        textAddress.setText(getIntent().getStringExtra("address"));
    }
}
