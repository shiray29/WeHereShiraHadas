package com.example.wehere;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private EditText fullName, cellNum, adress,password;
    private Button  btnClose;
    private Uri imageUri;
    private TextView textViewProfile;
    private ProgressBar progressBar;
    private  boolean flag;
    private Profile profile;
    private String fullname,cellnum,Adress,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        fullName = findViewById(R.id.edit_fullname);
        cellNum = findViewById(R.id.cell_num);
        adress= findViewById(R.id.adress);
        password=findViewById(R.id.password);
        btnClose.findViewById(R.id.btn_close);
        storageReference = FirebaseStorage.getInstance().getReference("users"); // this line defines reference to Firebase Storage in order to store jpg files
        databaseReference= FirebaseDatabase.getInstance().getReference("users"); // this line defines reference to Firebase Database in order to store users data
        progressBar = findViewById(R.id.progress_bar);
        textViewProfile= findViewById(R.id.textview_insertprofile2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){ //this function re-defines the imageUri
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==1)&&(resultCode==RESULT_OK)&&(data!=null)&&(data.getData()!=null)){
            imageUri= data.getData();

        }
    }
    private void upload(){
        if (imageUri!= null){
            String temp= System.currentTimeMillis()+"."+getFileExtention(imageUri); //defines a file name to the uploaded image
            final StorageReference fileReference= storageReference.child(temp); // inserts the new image to database storage
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Handler handler= new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() { // presents a progress bar of the download time
                                    progressBar.setProgress(0);
                                }
                            },500);
                            progressBar.setProgress(0);
                            profile.setUriProfile(uri.toString());



                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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

    private void openFile(){ //this function opens the cellphone's gallery using intents
        Intent intent= new Intent ();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    public void onClick(View v) {
        if(textViewProfile==v){
            openFile();
            upload();
        }
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

            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(profile);
            startActivity(new Intent(getApplicationContext(), profile.isOld() == true ? com.example.wehere.ChooseiconsActivity.class : Search.class)); // starts the suitable activity according to isOld

        }
    }
}
