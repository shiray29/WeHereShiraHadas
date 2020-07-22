package com.example.wehere;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

private EditText edittextFullname, edittextCellnum, edittextIdnum, edittextAdress, edittextEmail, edittextEnterpassword;
private CheckBox checkboxOldie, checkboxVolunteer;
private Button btnConfirm;
private Profile profile;
// this line defines reference to Firebase Storage in order to store jpg files
private StorageReference storageReference;
// this line defines reference to Firebase Database in order to store users data
private DatabaseReference databaseReference;
private FirebaseAuth firebaseAuth;
private ProgressBar progressBar;
private Uri imageUri;
private TextView textViewProfile, textViewId;
private  boolean flag;
private FusedLocationProviderClient fusedLocationProviderClient;
private double longitude, latitude;
private String fullname, cellnum, idnum, adress, email, password;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = getInstance().getReference("users");
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null)
        {
            firebaseAuth.signOut();
        }
        edittextFullname = findViewById(R.id.edittext_fullname);
        edittextCellnum = findViewById(R.id.edittext_cellnum);
        textViewProfile = findViewById(R.id.textview_insertprofile);
        textViewId = findViewById(R.id.textview_insertidphoto);
        edittextIdnum = findViewById(R.id.edittext_idnum);
        edittextAdress = findViewById(R.id.edittext_adress);
        edittextEmail = findViewById(R.id.edittext_email);
        edittextEnterpassword = findViewById(R.id.edittext_enterpassword);
        checkboxOldie = findViewById(R.id.checkbox_oldie);
        checkboxVolunteer = findViewById(R.id.checkbox_volunteer);
        btnConfirm = findViewById(R.id.btn_confirm);
        progressBar = findViewById(R.id.progress_bar);
        textViewProfile.setOnClickListener(this);
        textViewId.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        profile = new Profile();
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
        {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                 }
                }
        });
        }

private void openFile(){ //this function opens the cellphone's gallery using intents
        Intent intent= new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
        }

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){ //this function re-defines the imageUri
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==1)&&(resultCode==RESULT_OK)&&(data!=null)&&(data.getData()!=null)){
        imageUri= data.getData();
        }
        }
private void upload(){
        if (imageUri!= null)
        {
        String temp= System.currentTimeMillis()+"."+getFileExtention(imageUri); //defines a file name to the uploaded image
        final StorageReference fileReference= storageReference.child(temp); // inserts the new image to database storage
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                public void onSuccess(Uri uri) {
                        progressBar.setEnabled(true);
                        Handler handler= new Handler();
                        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() { // presents a progress bar of the download time
                                progressBar.setProgress(0);
                                }
                        },500);
                        progressBar.setProgress(0);
                        if(flag){ // inserts the information to the suitable variable using the flag (flag is true if it's ID image)
                        profile.setUriId(uri.toString());
                        }
                else {
                profile.setUriProfile(uri.toString());
                }
        }
        });
        }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() { // להעלות את זה לתוך האון סקסס
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress=(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int )progress);
                }
                });
                }
        }
private String getFileExtention(Uri uri){
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
        }


@Override
public void onClick(View v) {

        if (textViewProfile==v) {
        flag= false;
        openFile();
        upload();
        }
        if (textViewId==v){
        flag=true;
        openFile();
        upload();
        }
        if (btnConfirm == v){
        fullname = edittextFullname.getText().toString().trim();
        cellnum = edittextCellnum.getText().toString().trim();
        idnum = edittextIdnum.getText().toString().trim();
        adress = edittextAdress.getText().toString().trim();
        email = edittextEmail.getText().toString().trim();
        password = edittextEnterpassword.getText().toString().trim();

        if ((fullname==null) || (cellnum==null) || (idnum == null) || (adress == null)
        || (email ==null) || (password == null))
        {
        Toast.makeText(this,"אנא מלא את כל הפרטים הנדרשים", Toast.LENGTH_SHORT).show();
        }
        else {
        profile.setName(fullname);
        profile.setCellnum(cellnum);
        profile.setId(idnum);
        profile.setAdress(adress);
        profile.setEmail(email);
        profile.setPassword(password);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED)){
        getLocation();
        }
        else {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        if ((checkboxOldie.isChecked() && checkboxVolunteer.isChecked()) || (!checkboxOldie.isChecked() && !checkboxVolunteer.isChecked())) // checks whether the user chose 2 or 0 options
        {
        Toast.makeText(this,"יש לסמן רק אחת מהאפשרויות - מתנדב או קשיש", Toast.LENGTH_SHORT).show();
        checkboxOldie.setChecked(false);
        checkboxVolunteer.setChecked(false);
        }

        if (checkboxOldie.isChecked()){
        profile.setOld(true);
        }
        if (checkboxVolunteer.isChecked()){
        profile.setOld(false);
        }


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task)
        {
        if (task.isSuccessful()){
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
        startActivity(new Intent(getApplicationContext(), profile.isOld()==true?ChooseiconsActivity.class:Search.class));
        finish();
        }
        if (!task.isSuccessful())
        { Toast.makeText(RegisterActivity.this,"ההרשמה נכשלה",Toast.LENGTH_SHORT).show();}
        }
        });
        }}}}