package com.kthdvs.gharkokhana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SendMessageActivity extends AppCompatActivity {

    private String To;
    private String senderName;
    private String chefID;
    private String postId;
    private String notificationrecieverDeviceId;

    private int intOrders;
    private String stringOrders;

    private Button btnOrder;
    private EditText timeTxt;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mref;
    private DatabaseReference notificationData;

    private DatabaseReference usersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        To = getIntent().getExtras().getString("chefName");
        chefID = getIntent().getExtras().getString("chefID");
        postId = getIntent().getExtras().getString("postId");
        stringOrders = getIntent().getExtras().getString("orderRemaining");
        intOrders = Integer.parseInt(stringOrders);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending your request...");
        progressDialog.setMessage("You will be notified about your order by the chef.");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        timeTxt = (EditText)findViewById(R.id.txtTime);

        mref = FirebaseDatabase.getInstance().getReference().child("Food_Posts").child(postId);
        notificationData =FirebaseDatabase.getInstance().getReference().child("notifications");
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(chefID);
        DatabaseReference senderdb = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        senderdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                senderName = (String)dataSnapshot.child("Name").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notificationrecieverDeviceId = (String)dataSnapshot.child("deviceToken").getValue();
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnOrder = (Button)findViewById(R.id.btnSend);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();

                String timeToGetOrder = timeTxt.getText().toString().trim();

                if(TextUtils.isEmpty(timeToGetOrder)){
                    progressDialog.dismiss();
                    Toast.makeText(SendMessageActivity.this,"Please specify the time by which you want your order",Toast.LENGTH_LONG).show();
                }else{
                    HashMap<String , String> notificationDatas = new HashMap<>();
                    notificationDatas.put("From", mCurrentUser.getUid());
                    notificationDatas.put("notificationType", "request");
                    notificationDatas.put("FoodID",postId);
                    notificationDatas.put("To",chefID);
                    notificationDatas.put("recieverDeviceToken", notificationrecieverDeviceId);
                    notificationDatas.put("SenderName", senderName);
                    notificationDatas.put("Time",timeToGetOrder);

                    notificationData.child(chefID).push().setValue(notificationDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            intOrders = intOrders -1;
                            stringOrders = String.valueOf(intOrders);
                            mref.child("OrdersNo").setValue(stringOrders);
                            progressDialog.dismiss();
                            startActivity(new Intent(SendMessageActivity.this,MainActivity.class));
                        }
                    });
                }



            }
        });

    }
}
