package com.kthdvs.gharkokhana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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

public class ChefActivity extends AppCompatActivity {

    private ImageButton mselectImage;
    private EditText mDish;
    private EditText mDesc;
    private EditText mOrders, mPlace;
    private EditText mPrice;
    private Button mbutton;
    private Uri urimage = null;
    private RadioGroup radioGroup;
    private RadioButton btnDeliver;

    private ProgressDialog mprogress;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUser_db;

    private StorageReference mstorage;
    private DatabaseReference mDatabase;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);

        mstorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food_Posts");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUser_db = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mselectImage = (ImageButton)findViewById(R.id.select_img);
        mDish = (EditText)findViewById(R.id.dishName);
        mDesc = (EditText)findViewById(R.id.desc);
        mOrders = (EditText)findViewById(R.id.orders);
        mPrice = (EditText)findViewById(R.id.price);
        mbutton = (Button)findViewById(R.id.submit);
        mPlace = (EditText)findViewById(R.id.place);
        radioGroup = (RadioGroup)findViewById(R.id.Rgroup);

        mprogress = new ProgressDialog(this);

        mselectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);

                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();
            }
        });
    }

    private void startPosting(){
        System.out.println("Posting data");

        mprogress.setMessage("Posting..");
        mprogress.show();

        final String nameOfDish = mDish.getText().toString().trim();
        final String description = mDesc.getText().toString().trim();
        final String orders = mOrders.getText().toString().trim();
        final String price = mPrice.getText().toString().trim();
        final String chefAddress = mPlace.getText().toString().trim();

        int selectedRadio = radioGroup.getCheckedRadioButtonId();

        btnDeliver = (RadioButton)findViewById(selectedRadio);

        final String deliveryOptions = btnDeliver.getText().toString().trim();

        if(!TextUtils.isEmpty(nameOfDish) && !TextUtils.isEmpty(description) &&
                !TextUtils.isEmpty(orders) && !TextUtils.isEmpty(price) &&
                !TextUtils.isEmpty(deliveryOptions) && !TextUtils.isEmpty(chefAddress)){

                StorageReference filepath = mstorage.child("Food_Images").child(urimage.getLastPathSegment());

                filepath.putFile(urimage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        final DatabaseReference newPost = mDatabase.push();

                        mUser_db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                newPost.child("DishName").setValue(nameOfDish);
                                newPost.child("Description").setValue(description);
                                newPost.child("OrdersNo").setValue(orders);
                                newPost.child("Price").setValue(price);
                                newPost.child("Delivery").setValue(deliveryOptions);
                                newPost.child("image").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(mCurrentUser.getUid());
                                newPost.child("username").setValue(dataSnapshot.child("Name").getValue());
                                newPost.child("Location").setValue(chefAddress);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {


                            }
                        });

                        mprogress.dismiss();

                        Intent intent = new Intent(ChefActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            urimage = data.getData();

            mselectImage.setImageURI(urimage);
        }
    }


}
