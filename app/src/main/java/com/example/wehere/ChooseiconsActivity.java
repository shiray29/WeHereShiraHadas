package com.example.wehere;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class ChooseiconsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageViewBuild, imageViewCall, imageViewClean, imageViewCompany, imageViewShop;
    private TextView textViewPick, textViewBuild, textViewCall, textViewClean, textViewCompany, textViewShop;
    private Button btnconfirmicon;
    private Profile profile;
    private int buildCount, companyCount, cleanCount, callCount, shopCount;
    private DatabaseReference  ref;
    private ArrayList<Profile> list;
    private FirebaseUser thisuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseicons);
        imageViewBuild = findViewById(R.id.imageView_build);
         ref = FirebaseDatabase.getInstance().getReference("users");
         list = new ArrayList<Profile>();
         thisuser = FirebaseAuth.getInstance().getCurrentUser();
        imageViewCall = findViewById(R.id.imageView_call);
        imageViewClean = findViewById(R.id.imageView_clean);
        imageViewCompany = findViewById(R.id.imageView_company);
        imageViewShop = findViewById(R.id.imageView_shop);
        textViewBuild = findViewById(R.id.textView_build);
        textViewCall = findViewById(R.id.textView_call);
        textViewClean = findViewById(R.id.textView_clean);
        textViewCompany = findViewById(R.id.textView_company);
        textViewShop = findViewById(R.id.textView_shop);
        textViewPick = findViewById(R.id.textView_pick);
        btnconfirmicon = findViewById(R.id.btn_confirmicon);
        buildCount = 0; // defining count variables for each help type
        callCount = 0;
        cleanCount = 0;
        companyCount = 0;
        shopCount = 0;
        imageViewBuild.setClickable(true);
        imageViewCall.setClickable(true);
        imageViewClean.setClickable(true);
        imageViewCompany.setClickable(true);
        imageViewShop.setClickable(true);
        imageViewBuild.setBackground(null); // cancels previous highlights
        imageViewCall.setBackground(null);
        imageViewClean.setBackground(null);
        imageViewCompany.setBackground(null);
        imageViewShop.setBackground(null);
        btnconfirmicon.setOnClickListener(this);
    }

    public void onClick(View V){

        if (imageViewBuild==V) { // highlights chosen help type, and cancel if clicked twice (for each help type)
            buildCount++;
            if (buildCount % 2 == 1) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);
                imageViewBuild.setBackground(highlight);
            } else {
                imageViewBuild.setBackground(null);
            }
        }
        if (imageViewCall==V)
        {
            callCount++;
            if (callCount % 2 == 1) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);
                imageViewCall.setBackground(highlight);
            } else {
                imageViewCall.setBackground(null);
            }
        }
        if (imageViewClean==V)
        {
            cleanCount++;
            if (cleanCount % 2 == 1) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);
                imageViewClean.setBackground(highlight);
            } else {
                imageViewClean.setBackground(null);
            }
        }
        if (imageViewCompany==V)
        {
            companyCount++;
            if (companyCount % 2 == 1) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);
                imageViewCompany.setBackground(highlight);
            } else {
                imageViewCompany.setBackground(null);
            }
        }
        if (imageViewShop==V)
        {
            shopCount++;
            if (shopCount % 2 == 1) {
                Drawable highlight = getResources().getDrawable(R.drawable.highlight);
                imageViewShop.setBackground(highlight);
            } else {
                imageViewShop.setBackground(null);
            }
        }


        if (btnconfirmicon==V){
            if ((buildCount%2==0) && (callCount%2==0) && (cleanCount%2==0) && (companyCount%2==0) && (shopCount%2==0))
            {
               btnconfirmicon.setError("חייבים לבחור לפחות תחום סיוע אחד"); // notes if non help type was chosen
                return;}

            /* ref.child(thisuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // sets help type according to counters
                    profile = new Profile(dataSnapshot.getValue(Profile.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChooseiconsActivity.this, "CANCELLED", Toast.LENGTH_SHORT).show();
                }
            }); */
            Intent intent = getIntent();
            profile = (Profile) intent.getSerializableExtra("Profile");
            if (buildCount %2 == 1) { profile.setIsBuild(true); }
            else { profile.setIsBuild(false); }
            if (callCount %2 == 1){ profile.setCall(true);
            } else { profile.setCall(false);}
            if (cleanCount %2 == 1){ profile.setClean(true);
            } else { profile.setClean(false);}
            if (companyCount %2 == 1){ profile.setCompany(true);
            } else { profile.setCompany(false);}
            if (shopCount %2 == 1){ profile.setShop(true);
            } else { profile.setShop(false);}
            ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
            Intent intent_waitForRequest = new Intent(ChooseiconsActivity.this, WaitforRequest.class);
            intent_waitForRequest.putExtra("wait for request profile" , (Serializable) profile);
            startActivity(intent_waitForRequest);
            finish();
        }

    };

}