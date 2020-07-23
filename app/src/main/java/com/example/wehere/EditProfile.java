package com.example.wehere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        storageReference = FirebaseStorage.getInstance().getReference("users"); // this line defines reference to Firebase Storage in order to store jpg files
        databaseReference= FirebaseDatabase.getInstance().getReference("users"); // this line defines reference to Firebase Database in order to store users data
        firebaseAuth = FirebaseAuth.getInstance();
        fullName = findViewById(R.id.edit_fullname);
        cellNum = findViewById(R.id.cell_num);
        adress= findViewById(R.id.adress);
        password=findViewById(R.id.password);
        btnClose=findViewById(R.id.btn_close);
        btnSignOut=findViewById(R.id.btn_signout);
    }


    public void onClick(View v) {

        if (btnClose == v) {
            fullname = fullName.getText().toString().trim();
            cellnum = cellNum.getText().toString().trim();
            Adress = adress.getText().toString().trim();
            Password = password.getText().toString().trim();
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    profile = dataSnapshot.getValue(Profile.class);
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
                        profile.getPassword(Password);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(profile);
            startActivity(new Intent(getApplicationContext(), profile.isOld() == true ? com.example.wehere.WaitforRequest.class : Search.class)); // starts the suitable activity according to isOld

        }
    }
}
