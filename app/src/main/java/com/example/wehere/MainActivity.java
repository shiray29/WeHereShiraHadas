package com.example.wehere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnLogin, btnRegister;
    private TextView textViewWeHere, textViewWelcome;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        /* FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:989905895690:android:0a1a507f6d6a540bdebaf3") // Required for Analytics.
                .setProjectId("wehere2-1cc20") // Required for Firebase Installations.
                .setApiKey("AIzaSyDGd03usaNMZXezcz35WY-aOYSDr-Tv6BU") // Required for Auth.
                .build(); */
        // FirebaseApp.initializeApp(this, options, "FIREBASE APP NAME");
        textViewWeHere = findViewById(R.id.textview_wehere);
        textViewWelcome = findViewById(R.id.textview_welcome);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { // this function starts the selected activity according to the clicked button using intents
        if (v==btnLogin) {
            Intent intent_login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent_login);
        }
        if (v==btnRegister) {
            Intent intent_register = new Intent(this, RegisterActivity.class);
            startActivity(intent_register);
        }

    }
}