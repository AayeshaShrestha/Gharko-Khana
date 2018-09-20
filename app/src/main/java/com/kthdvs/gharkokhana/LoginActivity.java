package com.kthdvs.gharkokhana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.sql.StatementEvent;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passField;
    private Button Login_btn;
    private Button Signup_btn;

    private FirebaseAuth mAuth;
    private DatabaseReference db_user;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db_user = FirebaseDatabase.getInstance().getReference().child("Users");

        db_user.keepSynced(true);

        progressDialog = new ProgressDialog(this);

        emailField = (EditText) findViewById(R.id.email);
        passField = (EditText) findViewById(R.id.password);
        Login_btn = (Button) findViewById(R.id.btnLogin);
        Signup_btn = (Button) findViewById(R.id.btnSignup);

        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();
            }
        });

        Signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup_intent = new Intent(LoginActivity.this,RegisterActivity.class);
                signup_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signup_intent);
            }
        });
    }

    private void checkLogin(){

        String email = emailField.getText().toString().trim();
        String password = passField.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            progressDialog.setMessage("Checking in..");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        progressDialog.dismiss();
                        checkUserVerification();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Login Failed.Try Again",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    private void checkUserVerification(){
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser.isEmailVerified()){
            finish();
            startActivity(new Intent(LoginActivity.this,MainActivity.class));

        }else{
            Toast.makeText(this,"Please verify the email..",Toast.LENGTH_LONG).show();
            mAuth.signOut();
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}
