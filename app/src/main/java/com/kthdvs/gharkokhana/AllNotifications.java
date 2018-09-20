package com.kthdvs.gharkokhana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllNotifications extends AppCompatActivity {

    private static ListView list_view;
    private DatabaseReference mref;
    private FirebaseAuth mAuth;
    private String mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notifications);
        //listView();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();

        mref = FirebaseDatabase.getInstance().getReference().child("notifications").child(mCurrentUser);
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectSenderNames((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void collectSenderNames(Map<String, Object> notifications){
        ArrayList<String> sender = new ArrayList<String>();

        for (Map.Entry<String, Object> entry: notifications.entrySet()){
            Map singleUser = (Map) entry.getValue();

            sender.add((String) singleUser.get("SenderName"));
        }

        System.out.println(sender);
    }

    public void listView(){
        list_view = (ListView)findViewById(R.id.lists);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(AllNotifications.this, R.layout.notification_list, sender);

        //list_view.setAdapter(adapter);

    }
}
