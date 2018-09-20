package com.kthdvs.gharkokhana;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class GetOrderActivity extends AppCompatActivity {

    String reciever;
    Button btnAccept;
    TextView textView;
    String notification;

    private FirebaseAuth mAuth;
    String currentUser;
    private DatabaseReference mref;
    private String notificationrecieverDeviceId;
    private String senderName;
    private DatabaseReference notificationData;
    private Button deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order);

        reciever = getIntent().getExtras().getString("fromId");
        notification = getIntent().getExtras().getString("body");
        btnAccept = (Button)findViewById(R.id.btnAccept);
        textView = (TextView)findViewById(R.id.notif_text);
        //deleteBtn = (Button)findViewById(R.id.btnDelete);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        notificationData =FirebaseDatabase.getInstance().getReference().child("notifications");

        textView.setText(notification);

        mref = FirebaseDatabase.getInstance().getReference().child("Users");
        mref.child(reciever).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notificationrecieverDeviceId = (String)dataSnapshot.child("deviceToken").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mref.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderName = (String)dataSnapshot.child("Name").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String , String> notificationDatas = new HashMap<>();
                notificationDatas.put("From", currentUser);
                notificationDatas.put("notificationType", "confirm");
                notificationDatas.put("To",reciever);
                notificationDatas.put("recieverDeviceToken", notificationrecieverDeviceId);
                notificationDatas.put("SenderName", senderName);

                notificationData.child(reciever).push().setValue(notificationDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        startActivity(new Intent(GetOrderActivity.this,MainActivity.class));
                    }
                });
            }
        });

        /*deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String>deleteDatas = new HashMap<>();
                deleteDatas.put("From", currentUser);
                deleteDatas.put("notificationType","delete");
                deleteDatas.put("To", reciever);
                deleteDatas.put("recieverDeviceToken", notificationrecieverDeviceId);
                deleteDatas.put("SenderName", senderName);

                notificationData.child(reciever).push().setValue(deleteDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(GetOrderActivity.this, MainActivity.class));
                    }
                });
            }
        });*/
    }
}
