package com.kthdvs.gharkokhana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText nameField;
    private EditText locationField;
    private EditText contactField;
    private EditText passwordField;
    private Button registerBtn;

    String name;
    String email;
    String location;
    String password;
    String contact;

    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(this);

        emailField = (EditText) findViewById(R.id.email);
        nameField = (EditText) findViewById(R.id.username);
        locationField = (EditText) findViewById(R.id.location);
        contactField = (EditText) findViewById(R.id.contact);
        passwordField = (EditText) findViewById(R.id.password);
        registerBtn = (Button) findViewById(R.id.btnRegister);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
    }


    private void startRegister(){
        name = nameField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        location = locationField.getText().toString().trim();
        contact = contactField.getText().toString().trim();
        password = passwordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(location) &&
                !TextUtils.isEmpty(contact) && !TextUtils.isEmpty(password)) {

            progressDialog.setMessage("Signing up..");
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendemailVerification();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(RegisterActivity.this,"Please enter all the fields",Toast.LENGTH_SHORT).show();
        }
    }



    private void sendemailVerification() {
        final FirebaseUser user1 = mAuth.getCurrentUser();
        if(user1 != null){
            user1.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Successfully Registered.A verification mail has been sent",Toast.LENGTH_SHORT).show();
                                addTODatabase();
                                mAuth.signOut();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this,"Error 404!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void addTODatabase(){
        String user_id = mAuth.getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        DatabaseReference currentUserDB = mdatabase.child(user_id);
        currentUserDB.child("Name").setValue(name);
        currentUserDB.child("Email").setValue(email);
        currentUserDB.child("Location").setValue(location);
        currentUserDB.child("Contact").setValue(contact);
        currentUserDB.child("deviceToken").setValue(token);
        currentUserDB.child("check").setValue("1");

        progressDialog.dismiss();

    }

}


