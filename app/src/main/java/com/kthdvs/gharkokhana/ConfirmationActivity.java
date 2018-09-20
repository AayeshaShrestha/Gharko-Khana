package com.kthdvs.gharkokhana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmationActivity extends AppCompatActivity {

    String senderUid;
    String notification_text;

    private DatabaseReference mref;

    private TextView textView;
    private TextView showContact;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        senderUid = getIntent().getExtras().getString("fromId");
        notification_text = getIntent().getExtras().getString("body");

        textView = (TextView)findViewById(R.id.body);
        showContact = (TextView)findViewById(R.id.contactNumber);
        btn = (Button)findViewById(R.id.btnOkay);

        textView.setText(notification_text);

        mref = FirebaseDatabase.getInstance().getReference().child("Users").child(senderUid);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String contact = (String)dataSnapshot.child("Contact").getValue();
                showContact.setText("For further details, contact the chef in " +contact);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ConfirmationActivity.this,MainActivity.class));
            }
        });


    }
}
