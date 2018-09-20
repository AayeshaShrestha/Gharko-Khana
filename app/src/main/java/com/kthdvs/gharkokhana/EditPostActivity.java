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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditPostActivity extends AppCompatActivity {

    EditText name;
    EditText orders;
    EditText delivery;
    EditText description;
    EditText price;
    ImageButton img;
    Button btnUpdate;

    public static final int GALLERY_REQUEST = 1;
    private Uri urimage;
    private StorageReference mstorage;
    String postId;

    private DatabaseReference mref;

    private ProgressDialog mprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        postId = getIntent().getExtras().getString("postId");

        name = (EditText)findViewById(R.id.newDish);
        orders = (EditText)findViewById(R.id.newOrder);
        delivery = (EditText)findViewById(R.id.newDelivery);
        description = (EditText)findViewById(R.id.newDescr);
        price = (EditText)findViewById(R.id.newPrice);
        img =(ImageButton) findViewById(R.id.newImg);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);

        mprogress = new ProgressDialog(this);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);

                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mstorage = FirebaseStorage.getInstance().getReference();
        mref = FirebaseDatabase.getInstance().getReference().child("Food_Posts").child(postId);
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText((String)dataSnapshot.child("DishName").getValue());
                orders.setText((String)dataSnapshot.child("OrdersNo").getValue());
                delivery.setText((String)dataSnapshot.child("Delivery").getValue());
                description.setText((String)dataSnapshot.child("Description").getValue());
                price.setText((String)dataSnapshot.child("Price").getValue());
                final String image = (String) dataSnapshot.child("image").getValue();
                Picasso.with(EditPostActivity.this)
                        .load(image)
                        .into(img);



                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateDatabase();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            urimage = data.getData();

            img.setImageURI(urimage);
        }
    }

    public void updateDatabase(){

        mprogress.setMessage("Updating Post..");
        mprogress.show();

        final String newname = name.getText().toString().trim();
        final String newOrders = orders.getText().toString().trim();
        final String newDel = delivery.getText().toString().trim();
        final String newPrice = price.getText().toString().trim();
        final String newDes = description.getText().toString().trim();


        mref.child("DishName").setValue(newname);
        mref.child("OrdersNo").setValue(newOrders);
        mref.child("Delivery").setValue(newDel);
        mref.child("Description").setValue(newDes);
        mref.child("Price").setValue(newPrice);

        StorageReference filepath = mstorage.child("Food_Images").child(urimage.getLastPathSegment());

        filepath.putFile(urimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();


                mref.child("image").setValue(downloadUrl.toString());


            }
        });

        mprogress.dismiss();
        Intent intent = new Intent(EditPostActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
