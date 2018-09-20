package com.kthdvs.gharkokhana;

import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FavouritesActivity extends AppCompatActivity {

    private EditText no1;
    private EditText no2;
    private EditText no3;
    private EditText no4;
    private EditText no5;
    private Button btnSubmit;

    String userEmail;
    String userId;

    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        userEmail = currentUser.getEmail();
        userId = currentUser.getUid();

        no1 = (EditText)findViewById(R.id.fav1);
        no2 = (EditText)findViewById(R.id.fav2);
        no3 = (EditText)findViewById(R.id.fav3);
        no4 = (EditText)findViewById(R.id.fav4);
        no5 = (EditText)findViewById(R.id.fav5);
        btnSubmit = (Button)findViewById(R.id.btnToPic);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    addInfos();
            }
        });


    }

    private void addInfos() {

        final String user_id = mAuth.getCurrentUser().getUid();

        final String fav1 = no1.getText().toString().trim();
        final String fav2 = no2.getText().toString().trim();
        final String fav3 = no3.getText().toString().trim();
        final String fav4 = no4.getText().toString().trim();
        final String fav5 = no5.getText().toString().trim();


        DatabaseReference favRef = mdatabase.child(userId);

        favRef.child("Email").setValue(userEmail);
        favRef.child("Favourites").child("1").setValue(fav1);
        favRef.child("Favourites").child("2").setValue(fav2);
        favRef.child("Favourites").child("3").setValue(fav3);
        favRef.child("Favourites").child("4").setValue(fav4);
        favRef.child("Favourites").child("5").setValue(fav5);
        favRef.child("check").setValue("2");

        favRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String num = (String)dataSnapshot.child("check").getValue();

                if(num.equals("2")){
                    Intent intent = new Intent(FavouritesActivity.this,CitizencpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else if(num.equals("3")){
                    Intent intent = new Intent(FavouritesActivity.this,ProfilePicActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else
                {
                    Intent intent = new Intent(FavouritesActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
