package com.kthdvs.gharkokhana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.widget.Toast.LENGTH_LONG;

public class EditProfileActivity extends AppCompatActivity {

    private static EditText name;
    private static EditText address;
    private static EditText contact;
    private static EditText fav1;
    private static EditText fav2;
    private static EditText fav3;
    private static EditText fav4;
    private static EditText fav5;
    private static ImageButton img;
    private static Button btnUpdate;

    private ProgressDialog progressDialog;

    private DatabaseReference mdatabase;
    private String mCurrentUser;
    private StorageReference mstorage;
    private Uri urimage;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = (EditText) findViewById(R.id.newName);
        address = (EditText) findViewById(R.id.newLocation);
        contact = (EditText) findViewById(R.id.newcontact);
        fav1 = (EditText) findViewById(R.id.newFav1);
        fav2 = (EditText) findViewById(R.id.newFav2);
        fav3 = (EditText) findViewById(R.id.newFav3);
        fav4 = (EditText) findViewById(R.id.newFav4);
        fav5 = (EditText) findViewById(R.id.newFav5);
        img = (ImageButton) findViewById(R.id.newPic);
        btnUpdate = (Button) findViewById(R.id.btnSave);
        progressDialog = new ProgressDialog(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);
        mstorage = FirebaseStorage.getInstance().getReference();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);

                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText((String)dataSnapshot.child("Name").getValue());
                address.setText((String)dataSnapshot.child("Location").getValue());
                contact.setText((String)dataSnapshot.child("Contact").getValue());
                fav1.setText((String)dataSnapshot.child("Favourites").child("1").getValue());
                fav2.setText((String)dataSnapshot.child("Favourites").child("2").getValue());
                fav3.setText((String)dataSnapshot.child("Favourites").child("3").getValue());
                fav4.setText((String)dataSnapshot.child("Favourites").child("4").getValue());
                fav5.setText((String)dataSnapshot.child("Favourites").child("5").getValue());
                String userimg = (String)dataSnapshot.child("ProfilePic").getValue();
                Picasso.with(EditProfileActivity.this)
                        .load(userimg)
                        .into(img);

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        updateTheDatabse();




                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateTheDatabse(){

        progressDialog.setMessage("Updating your info..");
        progressDialog.show();

        String newName = name.getText().toString().trim();
        String newadd = address.getText().toString().trim();
        String newCon = contact.getText().toString().trim();
        String new1 = fav1.getText().toString().trim();
        String new2 = fav2.getText().toString().trim();
        String new3 = fav3.getText().toString().trim();
        String new4 = fav4.getText().toString().trim();
        String new5 = fav5.getText().toString().trim();

        mdatabase.child("Name").setValue(newName);
        mdatabase.child("Location").setValue(newadd);
        mdatabase.child("Contact").setValue(newCon);
        mdatabase.child("Favourites").child("1").setValue(new1);
        mdatabase.child("Favourites").child("2").setValue(new2);
        mdatabase.child("Favourites").child("3").setValue(new3);
        mdatabase.child("Favourites").child("4").setValue(new4);
        mdatabase.child("Favourites").child("5").setValue(new5);

        StorageReference filepath = mstorage.child("ProfilePics").child(urimage.getLastPathSegment());

        filepath.putFile(urimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();


                mdatabase.child("ProfilePic").setValue(downloadUrl.toString());


            }
        });

        progressDialog.dismiss();
        Intent intent = new Intent(EditProfileActivity.this,MainActivity.class);
        //intent.putExtra("uid",mCurrentUser.toString());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            urimage = data.getData();

            img.setImageURI(urimage);
        }
    }
}


