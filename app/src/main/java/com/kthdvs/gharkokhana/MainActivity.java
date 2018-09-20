package com.kthdvs.gharkokhana;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String longitude, latitude;
    String currentLocation;

    private DatabaseReference mDatabase;
    private DatabaseReference mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Food_Posts");
        mAuth = FirebaseAuth.getInstance();
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsers.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){

                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }
        };


        mDatabase.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.postLists);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent searchIntent = new Intent(MainActivity.this,CheckActivity.class);
                startActivity(searchIntent);

                /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    buildAlertMessageNoGps();
                }

                else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    getLocation();
                }*/
            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        mUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentLocation = (String)dataSnapshot.child("Location").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<foodPosts, FoodPostsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<foodPosts, FoodPostsViewHolder>(
                        foodPosts.class,
                        R.layout.post_foods,
                        FoodPostsViewHolder.class,
                        mDatabase.orderByChild("Location").equalTo(currentLocation)
                ){


                    @Override
                    protected void populateViewHolder(FoodPostsViewHolder viewHolder, foodPosts model, int position) {

                        final String post_key = getRef(position).getKey();

                        viewHolder.setDishName(model.getDishName());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPrice(model.getPrice());
                        viewHolder.setOrdersNo(model.getOrdersNo());
                        viewHolder.setDelivery(model.getDelivery());
                        viewHolder.setImage(model.getImage());
                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setLocation(model.getLocation());

                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent detailIntent = new Intent(MainActivity.this, PostDetailsActivity.class);
                                detailIntent.putExtra("key",post_key.toString());

                                startActivity(detailIntent);
                            }
                        });
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FoodPostsViewHolder extends RecyclerView.ViewHolder{

        View mview;
        public FoodPostsViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }

        public void setDishName(String name){

            TextView dish = (TextView) mview.findViewById(R.id.newFood);
            dish.setText("Name of Dish: " +name);

        }

        public void setLocation(String location){
            TextView locate = (TextView) mview.findViewById(R.id.newLocation);
            locate.setText("Location: "+location);
        }

        public void setDescription(String desc){

            TextView descr = (TextView)mview.findViewById(R.id.description);
            descr.setText("Description: "+desc);

        }

        public void setOrdersNo(String orders){

            TextView order = (TextView)mview.findViewById(R.id.no_of_orders);
            order.setText("No of orders that can be placed: " +orders);

        }

        public void setPrice(String amount) {

            TextView price =(TextView) mview.findViewById(R.id.food_price);
            price.setText("Price per plate: " +amount);

        }

        public void setDelivery(String deliver){

            TextView delopt = (TextView) mview.findViewById(R.id.delivery_options);
            delopt.setText("Delivery Options: " +deliver);
        }


        public void setImage(String img){

            ImageView imgvw = (ImageView) mview.findViewById(R.id.post_image);

            Picasso.with(itemView.getContext())
                    .load(img)
                    .into(imgvw);
        }

        public void setUsername(String username) {

            TextView usr =(TextView)mview.findViewById(R.id.chef);
            usr.setText("Chef: " +username);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.cook){
            startActivity(new Intent(MainActivity.this,ChefActivity.class));
        }

        if(item.getItemId() == R.id.logout){

            logout();
        }

        if(item.getItemId() == R.id.noti){

            Intent notintent = new Intent(MainActivity.this,AllNotifications.class);
            startActivity(notintent);

        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent profileIntent = new Intent(MainActivity.this,ViewProfileActivity.class);
            profileIntent.putExtra("uid",mAuth.getCurrentUser().getUid());
            startActivity(profileIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();
    }

    public void getLocation(){

        if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }
        else{
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                Double longi = location.getLongitude();
                Double lati = location.getLatitude();
                longitude = String.valueOf(longi);
                latitude = String.valueOf(lati);

                Toast.makeText(this,"Longitude:"+longitude+"\n"+"Latitude:"+latitude,Toast.LENGTH_LONG).show();


            }else{
                Toast.makeText(this,"Unable to trace location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void buildAlertMessageNoGps(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Please turn on your GPS")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        final  AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
