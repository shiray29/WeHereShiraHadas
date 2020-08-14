package com.example.wehere;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class Search extends FragmentActivity implements OnMapReadyCallback, ExampleDialog.ExampleDialogListener {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Profile> profileList = new ArrayList<Profile>();
    private ArrayList<Marker> markerList = new ArrayList<Marker>();
    private double thisLon, thisLat;
    private Profile thisUser;
    private Button btnSearchedit, accept, decline;
    GoogleMap map;
    private SupportMapFragment mapFragment;
    private int count = 0;
    private int height = 100;
    private int width = 100;
    private double tempLat;
    private double tempLon;
    private GoogleMap.OnMarkerClickListener onMarkerClickListener;
    private boolean dialogResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        thisUser = new Profile((Profile) getIntent().getSerializableExtra("Profile"));
        thisLat = thisUser.getLatitude();
        thisLon = thisUser.getLongitude();
        btnSearchedit = findViewById(R.id.btn_searchedit);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        allMarkers();
        LatLng point = new LatLng(thisLat, thisLon);
        googleMap.addMarker(new MarkerOptions().position(point)); // shows the volunteer's location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(point)); // focus their location
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker mark) {
                openDialog();
                if (dialogResult){
                    LatLng posi = mark.getPosition();
                    String helpType = mark.getTitle();
                    Profile wantedOld = oldUserFromLocation(posi);
                    sendSmsOld(thisUser, wantedOld, helpType);
                }
                return false;
            }
        });
    }

    public void allMarkers(){
        profileList.add(new Profile("Spongebob", "jerusalem", "1234", "05050", "spongeob@gmail.com", "1234", "uri1234"
                , true, false, false, true, false, true, 35.213711, 31.768318));
        profileList.add(new Profile("patrick", "tel aviv", "1000", "05000", "patrick@gmail.com", "1000", "uri1000"
                , true, true, true, false, false, false, 34.780543, 32.082865));
        profileList.add(new Profile("squidward", "eilat", "6666", "05066", "squidward@gmail.com", "6666", "uri6666"
                , true, false, true, false, true, false, 34.947226, 29.550761));
        while (profileList.size() > count) { // shows on map every near by old users (<30km)
            if (profileList.get(count).isOld())
            {
                tempLat = profileList.get(count).getLatitude();
                tempLon = profileList.get(count).getLongitude();
                //if (findDistance(tempLat, tempLon, thisLat, thisLon) <=130)
                //{
                String title;
                if (profileList.get(count).getIsBuild()){
                    title = "סיוע בשיפוץ";
                    showMarker(tempLat, tempLon, R.drawable.isbuild, title);
                }
                if (profileList.get(count).getClean()){
                    title = "סיוע בניקיון";
                    showMarker(tempLat, tempLon, R.drawable.isclean, title);
                }
                if (profileList.get(count).getCompany());
                {
                    title = "אירוח חברה";
                    showMarker(tempLat, tempLon, R.drawable.iscompany, title);
                }
                if (profileList.get(count).getShop());
                {
                    title = "סיוע בקניות";
                    showMarker(tempLat, tempLon, R.drawable.isshop, title);
                }
                if (profileList.get(count).getCall());
                {
                    title = "קשר טלפוני";
                    showMarker(tempLat, tempLon, R.drawable.iscall, title);
                }
                //}
            }
            count++;
        }
    }

    public void openDialog(){
        ExampleDialog exampleDialog = new ExampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
}

    /* public boolean onMarkerClick(final Marker mark) {
        final Dialog dialog = new Dialog(Search.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popupsearch); // shows the pop-up
        accept = dialog.findViewById(R.id.btn_accept);
        decline = dialog.findViewById(R.id.btn_decline);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng posi = mark.getPosition();
                String helpType = mark.getTitle();
                Profile wantedOld = oldUserFromLocation(posi);
                sendSmsOld(thisUser, wantedOld, helpType);
                dialog.cancel();
                dialog.show();
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.show();
            }
        });
        return false;
    } */



    public double findDistance(double lat1, double lon1, double lat2, double lon2) // calculates distance between 2 coordinates
    {
        int R = 6371; // km
        double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
        double y = (lat2 - lat1);
        double distance = Math.sqrt(x * x + y * y) * R;
        return distance;
    }

    public void showMarker(double lat, double lon, int imageID, String title) {
        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(imageID);
        Bitmap b= bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        Marker m1 = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .title(title)); // shows marker on map (specific icon and title)
        markerList.add(m1); // adds it to the marker list
    }

    public void sendSmsOld(Profile currentUser, Profile wantedOldie, String helpType) // send SMS to old user and announces the volunteer
    {
        String phoneNo = wantedOldie.getCellnum();
        String message = "שלום, קוראים לי " + currentUser.getName() + ", הכתובת שלי היא " + currentUser.getAdress() + "ואני רוצה לעזור לך ב"
                + helpType + ". כדי ליצור איתי קשר ולקבל את עזרתי, אנא התקשר למספר הטלפון שלי - " + currentUser.getCellnum();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "ההודעה נשלחה לקשיש",
                Toast.LENGTH_LONG).show();
    }

    public Profile oldUserFromLocation(LatLng location) // finds user's profile using their location
    {
        for (int j = 0; j<profileList.size(); j++){
            Profile tempProf = (Profile) profileList.get(j);
            if ((tempProf.isOld()) && (tempProf.getLatitude() == location.latitude) &&
                    (tempProf.getLongitude() == location.longitude))
            {
                return tempProf;
            }
        }
        return null; // if there's no user in this location
    }

     /* GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker mark) {
                final Dialog dialog = new Dialog(Search.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popupsearch); // shows the pop-up
                accept = dialog.findViewById(R.id.btn_accept);
                decline = dialog.findViewById(R.id.btn_decline);
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng posi = mark.getPosition();
                        String helpType = mark.getTitle();
                        Profile wantedOld = oldUserFromLocation(posi);
                        sendSmsOld(thisUser, wantedOld, helpType);
                        dialog.cancel();
                        dialog.show();
                    }
                });
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        dialog.show();
                    }
                });
                return true;
            }
        }; */

    public void onClick(View V){
        if (V == btnSearchedit) { // links user to edit profile
            Intent intent_login = new Intent(this, com.example.wehere.EditProfile.class);
            intent_login.putExtra("profile to edit" , (Serializable) thisUser);
            startActivity(intent_login);
        }
    }

    @Override
    public void isConfirmed(boolean result) {
        dialogResult = result;
    }
}


