package com.example.wehere;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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

public class Search extends FragmentActivity implements OnMapReadyCallback, OldDialog.OldDialogListener {
    private ArrayList<Profile> profileList = new ArrayList<Profile>();
    private ArrayList<Profile> moreProfileList = new ArrayList<Profile>();
    private ArrayList<Marker> markerList = new ArrayList<Marker>();
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private double thisLon, thisLat;
    private Profile thisUser, example;
    private Button btnSearchedit, btnSms;
    private OldDialog oldDialog;
    GoogleMap map;
    private SupportMapFragment mapFragment;
    private int count = 0;
    private int height = 100;
    private int width = 100;
    private double tempLat;
    private double tempLon;
    private boolean resultofdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        thisUser = new Profile((Profile) getIntent().getSerializableExtra("Profile"));
        thisLat = thisUser.getLatitude();
        thisLon = thisUser.getLongitude();
        firebaseAuth= FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        btnSearchedit = findViewById(R.id.btn_searchedit);
        btnSms = findViewById(R.id.smsbtn);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // shows map & markers
        Toast.makeText(this,"יש לאשר שליחת SMS על מנת לעזור לקשיש", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(Search.this, new String[]{Manifest.permission.SEND_SMS}, 44);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        allMarkers(); // shows all markers
        LatLng point = new LatLng(thisLat, thisLon);
        googleMap.addMarker(new MarkerOptions().position(point)); // shows the volunteer's location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(point)); // focus on their location
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() { // on click listener for any marker
            @Override
            public boolean onMarkerClick(final Marker mark) {
                popUpNow(mark);
                return false;
            }
        });
    }

    public void allMarkers(){
        // example markers - instead of pulling out from Firebase
        profileList.add(example = new Profile("Spongebob", "jerusalem", "1234", "972525427958",
                "spongeob@gmail.com", "1234", "uri1234", true, false,
                false, true, false, true, 35.213711, 31.768318));
        profileList.add(new Profile("patrick", "tel aviv", "1000", "972525427958",
                "patrick@gmail.com", "1000", "uri1000",true, true,
                true, true, true, true, 34.780543, 32.082865));
        profileList.add(new Profile("squidward", "eilat", "6666", "972525427958",
                "squidward@gmail.com", "6666", "uri6666", true, false,
                true, false, true, false, 34.947226, 29.550761));
        /* databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()){
                    profileList.add((Profile) d.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        while (profileList.size() > count) { // shows on map every near by old users (<100km)
            if (profileList.get(count).isOld())
            {
                tempLat = profileList.get(count).getLatitude();
                tempLon = profileList.get(count).getLongitude();
                if (findDistance(tempLat, tempLon, thisLat, thisLon) <=100)
                {
                String title;
                if (profileList.get(count).getIsBuild()){
                    title = "סיוע בשיפוץ";
                    showMarker(tempLat, tempLon + 0.001, R.drawable.isbuild, title);
                    /* since we change the location of the icons so they won't show up on top of each other,
                    we will add more profiles of the same old users, with a bit different coordinates */
                    Profile addPro = new Profile(profileList.get(count));
                    addPro.setLongitude(profileList.get(count).getLongitude() + 0.001);
                    moreProfileList.add(addPro);
                }
                if (profileList.get(count).getClean()){
                    title = "סיוע בניקיון";
                    showMarker(tempLat, tempLon - 0.001, R.drawable.isclean, title);
                    Profile addPro = new Profile(profileList.get(count));
                    addPro.setLongitude(profileList.get(count).getLongitude() - 0.001);
                    moreProfileList.add(addPro);
                }
                if (profileList.get(count).getCompany());
                {
                    title = "אירוח חברה";
                    showMarker(tempLat + 0.001, tempLon, R.drawable.iscompany, title);
                    Profile addPro = new Profile(profileList.get(count));
                    addPro.setLatitude(profileList.get(count).getLatitude() + 0.001);
                    moreProfileList.add(addPro);
                }
                if (profileList.get(count).getShop());
                {
                    title = "סיוע בקניות";
                    showMarker(tempLat - 0.001, tempLon, R.drawable.isshop, title);
                    Profile addPro = new Profile(profileList.get(count));
                    addPro.setLatitude(profileList.get(count).getLatitude() - 0.001);
                    moreProfileList.add(addPro);
                }
                if (profileList.get(count).getCall());
                {
                    title = "קשר טלפוני";
                    showMarker(tempLat, tempLon, R.drawable.iscall, title);
                }
                }
            }
            count++;
        }
        for (int m = 0; m<moreProfileList.size(); m++) // adds profiles with different coordinates to profile list
        {
            profileList.add(moreProfileList.get(m));
        }
    }

    public double findDistance(double lat1, double lon1, double lat2, double lon2) // calculates distance between 2 coordinates
    {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (R * c); // Distance in km
    }

    public double deg2rad(double deg) // converts from degrees to radians - used in findDistance
    {
        return deg * (Math.PI/180);
    }

    public void showMarker(double lat, double lon, int imageID, String title)
    {
        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(imageID);
        Bitmap b= bitmapDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false); // defines marker size
        Marker m1 = map.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .title(title)); // shows marker on map (specific icon and title)
        markerList.add(m1); // adds marker to list so we can find the old's profile later
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

    public Profile oldUserFromLocation(LatLng location) // finds user's profile using their location & the markers list
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

    public void onClick(View V){
        if (V == btnSearchedit) { //  starts edit profile activity
            Intent intent_login = new Intent(this, com.example.wehere.EditProfile.class);
            intent_login.putExtra("profile to edit" , (Serializable) thisUser);
            startActivity(intent_login);
        }

        /* if (V == btnSms){
            popUpNow(markerList.get(0)); ניסיון
        } */
    }

    @Override
    public void isConfirmed(boolean result) {
        resultofdialog = result;
    }

    public void popUpNow(final Marker mark){
        final String[] options = {"אשמח לעזור! שלחו הודעת SMS לקשיש עם הפרטים האישיים שלי", "לא תודה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("האם תרצה לשלוח הצעת עזרה לקשיש הזה?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("אשמח לעזור! שלחו הודעת SMS לקשיש עם הפרטים האישיים שלי".equals(options[which])){
                    LatLng posi = mark.getPosition();
                    String helpType = mark.getTitle();
                    Profile wantedOld = oldUserFromLocation(posi);// returns the old's profile using the marker
                    sendSmsOld(thisUser, wantedOld, helpType);
                    return;
                }
                if ("לא תודה".equals(options[which])){
                    return;
                }
            }
        });
        builder.show();
    }
}