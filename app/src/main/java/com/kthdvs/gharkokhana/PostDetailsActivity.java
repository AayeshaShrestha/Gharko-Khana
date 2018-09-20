package com.kthdvs.gharkokhana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PostDetailsActivity extends AppCompatActivity {

    TextView nameofchef;
    TextView dishDes;
    TextView dishPrice;
    TextView NoOfOrders, chefLocation;
    TextView Chef;
    TextView DelOptions;
    ImageView imgFood;
    Button btnOrder;
    Button btnEdit;

    private int intOrders;
    String orders;
    private String delivery;
    private String deliveryCheck;
    String chef_id;
    String chef;
    String postId;

    private DatabaseReference mref;
    private String mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        postId = getIntent().getExtras().getString("key");

        nameofchef = (TextView)findViewById(R.id.NameOfDish);
        dishDes = (TextView)findViewById(R.id.DishDescription);
        dishPrice = (TextView)findViewById(R.id.DishPrice);
        NoOfOrders = (TextView)findViewById(R.id.DishOrders);
        Chef = (TextView)findViewById(R.id.NameOfChef);
        DelOptions = (TextView)findViewById(R.id.DishDelivery);
        imgFood = (ImageView)findViewById(R.id.FoodImage);
        btnOrder = (Button)findViewById(R.id.btnOrder);
        btnEdit = (Button)findViewById(R.id.btnEdit);
        chefLocation = (TextView) findViewById(R.id.chefLocation);

        mref = FirebaseDatabase.getInstance().getReference().child("Food_Posts").child(postId);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = (String) dataSnapshot.child("DishName").getValue();
                String description = (String) dataSnapshot.child("Description").getValue();
                orders = (String) dataSnapshot.child("OrdersNo").getValue();
                String price = (String)dataSnapshot.child("Price").getValue();
                chef = (String)dataSnapshot.child("username").getValue();
                delivery = (String)dataSnapshot.child("Delivery").getValue();
                String image = (String)dataSnapshot.child("image").getValue();
                chef_id = (String) dataSnapshot.child("uid").getValue();
                String chefAddress = (String)dataSnapshot.child("place").getValue();


                if (mCurrentUser.equals(chef_id)){
                    btnEdit.setVisibility(View.VISIBLE);
                    btnOrder.setVisibility(View.INVISIBLE);
                }else{
                    btnOrder.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.INVISIBLE);
                }

                intOrders = Integer.parseInt(orders);
                deliveryCheck = delivery.toUpperCase();

                nameofchef.setText("Name of Dish: " +name);
                dishDes.setText("Description: " +description);
                dishPrice.setText("Price: " +price);
                NoOfOrders.setText("No of orders remaining: " +orders);
                Chef.setText("Chef: " +chef);
                DelOptions.setText("Delivery Options: " +delivery);
                Picasso.with(PostDetailsActivity.this)
                        .load(image)
                        .into(imgFood);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Chef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chefIntent = new Intent(PostDetailsActivity.this, ViewProfileActivity.class);
                chefIntent.putExtra("uid",chef_id.toString());
                startActivity(chefIntent);
            }
        });
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (intOrders != 0) {
                        Intent intent = new Intent(PostDetailsActivity.this,SendMessageActivity.class);
                        intent.putExtra("chefID",chef_id.toString());
                        intent.putExtra("orderRemaining",orders);
                        intent.putExtra("chefName",chef.toString());
                        intent.putExtra("postId",postId.toString());
                        startActivity(intent);

                    } else {
                        Toast.makeText(PostDetailsActivity.this, "Limit to place order has reached. Sorry!!", Toast.LENGTH_SHORT).show();
                    }

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(PostDetailsActivity.this,EditPostActivity.class);
                newIntent.putExtra("postId",postId.toString());
                startActivity(newIntent);
            }
        });


    }
}
