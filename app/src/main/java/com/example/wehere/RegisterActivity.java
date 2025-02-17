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
import java.io.Serializable;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

        private EditText edittextFullname, edittextCellnum, edittextIdnum, edittextAdress, edittextEmail, edittextEnterpassword;
        private CheckBox checkboxOldie, checkboxVolunteer;
        private Button btnConfirm, btnUpload;
        private Profile profile;
        // this line defines reference to Firebase Storage in order to store jpg files
        private StorageReference storageReference;
        // this line defines reference to Firebase Database in order to store users data
        private DatabaseReference databaseReference;
        private FirebaseAuth firebaseAuth;
        private ProgressBar progressBar;
        private Uri imageUri;
        private TextView textViewId;
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
                edittextFullname = findViewById(R.id.edittext_fullname);
                edittextCellnum = findViewById(R.id.edittext_cellnum);
                textViewId = findViewById(R.id.textview_insertidphoto);
                edittextIdnum = findViewById(R.id.edittext_idnum);
                edittextAdress = findViewById(R.id.edittext_adress);
                edittextEmail = findViewById(R.id.edittext_email);
                edittextEnterpassword = findViewById(R.id.edittext_enterpassword);
                checkboxOldie = findViewById(R.id.checkbox_oldie);
                checkboxVolunteer = findViewById(R.id.checkbox_volunteer);
                btnConfirm = findViewById(R.id.btn_confirm);
                btnUpload = findViewById(R.id.btn_upload);
                progressBar = findViewById(R.id.progress_bar);
                textViewId.setOnClickListener(this);
                btnConfirm.setOnClickListener(this);
                btnUpload.setOnClickListener(this);
                latitude = 0;
                longitude = 0;
                profile = new Profile();
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                // gets the user's location in order to locate it on the volunteers' maps.
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
                getLocation();
        }

        public void getLocation() {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // if the user doesn't give access to location - we alret them and give another chance
                        Toast.makeText(this,"ללא הפעלת שירותי מיקום האפליקציה לא תוכל למצוא קשישים באזורך/לקבל עזרה ממתנדב. אנא אשר את הגישה למיקום.", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this,"לא תוכל למצוא קשישים באזורך/לקבל עזרה ממתנדב", Toast.LENGTH_SHORT).show();
                                return;
                        }
                }
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                                longitude = location.getLongitude();
                                                latitude = location.getLatitude();
                                        }
                                }
                        });
}

private void openFile(){ //this function opens the cellphone's gallery using intents
        Intent intent= new Intent (Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,1);
        }

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){ //this function re-defines the imageUri
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==1)&&(resultCode==RESULT_OK)){
        imageUri= data.getData();
        }
        }
private void upload(){ // uploads the user's ID photo
        if (imageUri!= null)
        {
        final String temp= System.currentTimeMillis()+"."+getFileExtention(imageUri); //defines a file name to the uploaded image
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
                        String tempo = uri.toString();
                        profile.setUriId(uri.toString());
                        tempo = profile.getUriId();
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
public void onClick(View v)
{
        if (textViewId==v){
        openFile();
        }
        if (btnUpload == v){
                firebaseAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                                upload();
                        }
                });
        }
        if (btnConfirm == v){
        fullname = edittextFullname.getText().toString().trim();
        cellnum = edittextCellnum.getText().toString().trim();
        idnum = edittextIdnum.getText().toString().trim();
        adress = edittextAdress.getText().toString().trim();
        email = edittextEmail.getText().toString().trim();
        password = edittextEnterpassword.getText().toString().trim();

        if ((fullname==null) || (cellnum==null) || (adress == null)
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
        profile.setLatitude(latitude);
        profile.setLongitude(longitude);
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
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
        @Override
        public void onComplete(@NonNull Task<AuthResult> task)
        {
        if (task.isSuccessful()){
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
        Intent intent = new Intent(RegisterActivity.this, profile.isOld()==true?ChooseiconsActivity.class:Search.class);
        intent.putExtra("Profile", (Serializable) profile);
        startActivity(intent);
        }
        if (!task.isSuccessful())
        { Toast.makeText(RegisterActivity.this,task.getException().getMessage() ,Toast.LENGTH_SHORT).show();}
        }
        });
        }}}}