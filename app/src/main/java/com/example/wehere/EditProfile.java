package com.example.wehere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private EditText fullName, cellNum, adress,password;
    private Button btnClose, btnSignOut;
    private Profile profile;
    private String fullname,cellnum,Adress,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        storageReference = FirebaseStorage.getInstance().getReference("users");
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        fullName = findViewById(R.id.edit_fullname);
        cellNum = findViewById(R.id.cell_num);
        adress= findViewById(R.id.adress);
        password=findViewById(R.id.password);
        btnClose=findViewById(R.id.btn_close);
        btnSignOut=findViewById(R.id.btn_signout);
        profile = (Profile) getIntent().getSerializableExtra("profile to edit");
    }

    public void onClick(View v) {
        if (btnClose == v)
        { // updates data and goes back to previous activity
            fullname = fullName.getText().toString().trim();
            cellnum = cellNum.getText().toString().trim();
            Adress = adress.getText().toString().trim();
            Password = password.getText().toString().trim();
            if (!(fullname.isEmpty())) {
                profile.setName(fullname);
            }
            if (!(Adress.isEmpty())) {
                profile.setAdress(Adress);
            }
            if (!(cellnum.isEmpty())) {
                profile.setCellnum(cellnum);
            }
            if (!(Password.isEmpty())) {
                profile.setPassword(Password);
            }
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
            startActivity(new Intent(getApplicationContext(), profile.isOld() == true ? com.example.wehere.WaitforRequest.class : Search.class));
        }
        if (btnSignOut == v)
        {
            firebaseAuth.signOut();
        }
        }
    }
