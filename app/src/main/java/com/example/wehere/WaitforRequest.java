package com.example.wehere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitforRequest extends AppCompatActivity implements View.OnClickListener
{
    private Button btnReqChange;
    private TextView textViewExplain;
    private TextView textViewWaitforReq;
    private Button btnEditWait;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitfor_request);
        btnReqChange = findViewById(R.id.btn_reqchange);
        btnEditWait = findViewById(R.id.btn_editwait);
        textViewWaitforReq = findViewById(R.id.textView_waitforreq);
        textViewExplain = findViewById(R.id.textView_explain);
    }

    @Override
    public void onClick(View V)
    {
        if (btnEditWait == V)
        {
            Intent intent_editprofile = new Intent(this, EditProfile.class);
            startActivity(intent_editprofile);
        }

        if (btnReqChange == V)
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // reset all help types to false
                    profile = dataSnapshot.getValue(Profile.class);
                    profile.setIsBuild(false);
                    profile.setCall(false);
                    profile.setClean(true);
                    profile.setCompany(true);
                    profile.setShop(true);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
            Intent intent_waitForRequest = new Intent(this, WaitforRequest.class);
            startActivity(intent_waitForRequest);
        }
    }
}
