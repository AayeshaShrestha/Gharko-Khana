package com.kthdvs.gharkokhana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewProfileActivity extends AppCompatActivity {

    private String name;
    private String location;

    private TextView userName;
    private TextView userLocation;
    private ImageView userImage;
    private Button btnEdit;

    private String userId;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        userId = getIntent().getExtras().getString("uid");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        userImage = (ImageView)findViewById(R.id.userPic);
        userLocation = (TextView) findViewById(R.id.address);
        userName = (TextView) findViewById(R.id.name);
        btnEdit = (Button)findViewById(R.id.editBtn);

        if((mAuth.getCurrentUser().getUid()).equals(userId)){
            btnEdit.setVisibility(View.VISIBLE);
        }else{
            btnEdit.setVisibility(View.INVISIBLE);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("id",userId.toString());
                startActivity(intent);
            }

        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("Name").getValue();
                location = (String)dataSnapshot.child("Location").getValue();
                String image = (String)dataSnapshot.child("ProfilePic").getValue();

                userName.setText("You are currently viewing profile of " +name);
                userLocation.setText("Location : " +location);
                Picasso.with(ViewProfileActivity.this)
                        .load(image)
                        .into(userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
