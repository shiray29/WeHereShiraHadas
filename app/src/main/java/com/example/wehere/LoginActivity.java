package com.example.wehere;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnLoginConfirm ;
    private EditText editTextLoginEmail, editTextLoginPassword;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ValueEventListener V;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // defines reference to Firebase Database, in order to authenticate users
        btnLoginConfirm = findViewById(R.id.btn_loginconfirm);
        editTextLoginEmail = findViewById(R.id.edittext_loginemail);
        editTextLoginPassword = findViewById(R.id.edittext_logingpassword);
        btnLoginConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (btnLoginConfirm == v)
        {
            String email = editTextLoginEmail.getText().toString().trim();
            String password = editTextLoginPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                editTextLoginEmail.setError("אימייל חובה");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextLoginPassword.setError("סיסמה חובה");
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) { //sends to Firebase Auth the email and password in order to log in
                    if(task.isSuccessful()){
                        V = new ValueEventListener() //this function imports the user's data using the user's Uid
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Profile prof = dataSnapshot.getValue(Profile.class);
                                Intent intent = new Intent(LoginActivity.this, prof.isOld()==true?ChooseiconsActivity.class:Search.class);
                                intent.putExtra("Profile", (Serializable) prof);
                                startActivity(intent); //starts the suitable activity using datasnapshop
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        };
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(V);
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"שגיאה. שם משתמש וסיסמה אינם נכונים.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}