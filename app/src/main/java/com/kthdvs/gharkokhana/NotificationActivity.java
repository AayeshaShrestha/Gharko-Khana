package com.kthdvs.gharkokhana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    private Button btn_confirm;
    private Button btn_delete;

    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private DatabaseReference mref;
    private DatabaseReference notificationData;

    String senderName;
    String fromID;
    String notificationRecieverDeviceId;
    String senderContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        fromID = getIntent().getExtras().getString("fromId");
        notificationData =FirebaseDatabase.getInstance().getReference().child("notifications");

        mref = FirebaseDatabase.getInstance().getReference().child("Users");
        mref.child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderName = (String)dataSnapshot.child("Name").getValue();
                senderContact = (String)dataSnapshot.child("Contact").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mref.child(fromID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notificationRecieverDeviceId = (String)dataSnapshot.child("deviceToken").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> confirmationDatas = new HashMap<>();
                confirmationDatas.put("SenderName", senderName);
                confirmationDatas.put("notificationType","confirm");
                confirmationDatas.put("recieverDeviceToken", notificationRecieverDeviceId);
                confirmationDatas.put("senderContact",senderContact);

                notificationData.child(fromID).push().setValue(confirmationDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        startActivity(new Intent(NotificationActivity.this,MainActivity.class));
                    }
                });


            }
        });
    }
}
