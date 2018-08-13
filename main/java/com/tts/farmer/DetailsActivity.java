package com.tts.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tts.farmer.R.drawable.farmer_logo;

public class DetailsActivity extends AppCompatActivity {
    private ImageView iv_image;
    private TextView tv_product,tv_price,tv_quantity,tv_detail,tv_location,tv_user_name,tv_user_location;
    private LinearLayout ll_chat,ll_profile;
    private CircleImageView iv_user_profile;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private String category,docid,userid=null;
    private CardView cv_userinfo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);




//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        mAuth=FirebaseAuth.getInstance();

        category=getIntent().getStringExtra("category");
        docid=getIntent().getStringExtra("docid");

        iv_image=(ImageView)findViewById(R.id.iv_image);
        iv_user_profile=(CircleImageView)findViewById(R.id.iv_user_profile);
        tv_product=(TextView)findViewById(R.id.tv_product);
        tv_price=(TextView)findViewById(R.id.tv_price);
        tv_quantity=(TextView)findViewById(R.id.tv_quantity);
        tv_detail=(TextView)findViewById(R.id.tv_detail);
        tv_location=(TextView)findViewById(R.id.tv_location);
        tv_user_name=(TextView)findViewById(R.id.tv_user_name);
        tv_user_location=(TextView)findViewById(R.id.tv_user_location);
        ll_chat=(LinearLayout)findViewById(R.id.ll_chat);
        ll_profile=(LinearLayout)findViewById(R.id.ll_profile);
        cv_userinfo=(CardView)findViewById(R.id.cv_userinfo);

        if (category.equals("")){
            fetch_product_data_inhome();
        }
        else {
            fetch_product_data();

        }


    }

    private void fetch_product_data_inhome() {
        DocumentReference ref=firebaseFirestore.document("featured/"+docid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product=documentSnapshot.toObject(Product.class);
                Picasso.with(DetailsActivity.this).load(product.getImage()).placeholder(R.drawable.farmer_logo).into(iv_image);
                tv_product.setText(product.getProduct());
                tv_price.setText("₹"+product.getPrice());
                tv_quantity.setText(product.getQuantity()+"/"+product.getQuantity_type());
                tv_detail.setText(product.getDetails());
                tv_location.setText(product.getLocation_name()+"\n"+product.getLocation_address());
                if (mAuth.getUid().equals(product.getUserid())){
                    cv_userinfo.setVisibility(View.GONE);
                    Toast.makeText(DetailsActivity.this, "Product was posted by you.", Toast.LENGTH_SHORT).show();
                }else {
                    fetch_user_data(product.getUserid());
                    userid=product.getUserid();
                }



            }
        });
    }

    private void fetch_product_data() {
        DocumentReference ref=firebaseFirestore.document("product/product/"+category+"/"+docid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product=documentSnapshot.toObject(Product.class);
                Picasso.with(DetailsActivity.this).load(product.getImage()).into(iv_image);
                tv_product.setText(product.getProduct());
                tv_price.setText("₹"+product.getPrice());
                tv_quantity.setText(product.getQuantity()+"/"+product.getQuantity_type());
                tv_detail.setText(product.getDetails());
                tv_location.setText(product.getLocation_name()+"\n"+product.getLocation_address());
                if (mAuth.getUid().equals(product.getUserid())){
                    cv_userinfo.setVisibility(View.GONE);
                    Toast.makeText(DetailsActivity.this, "Product was posted by you.", Toast.LENGTH_SHORT).show();
                }else {
                    fetch_user_data(product.getUserid());
                    userid=product.getUserid();
                }

            }
        });

    }
    private void fetch_user_data(String userid) {

        DocumentReference ref=firebaseFirestore.document("users/"+userid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Picasso.with(DetailsActivity.this).load(documentSnapshot.getString("profilepic"))
                        .placeholder(farmer_logo).into(iv_user_profile);
                tv_user_name.setText(documentSnapshot.getString("name"));
                tv_user_location.setText(documentSnapshot.getString("address"));


            }
        });

    }
    public void onClick(View view) {
        if (view.getId()==R.id.iv_image){
            Toast.makeText(this, "Image Clicked.", Toast.LENGTH_SHORT).show();

        }
        if (view.getId()==R.id.ll_chat){

            Intent intent = new Intent(DetailsActivity.this, ChatActivity.class);
            intent.putExtra("sender_uid", userid);
            startActivity(intent);
        }
        if (view.getId()==R.id.ll_profile){
            if (userid!=null) {
                Intent intent = new Intent(DetailsActivity.this, UserInfoActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        }

    }
}
