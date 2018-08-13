package com.tts.farmer;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tts.farmer.R.drawable.*;

public class MyProfileActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private CircleImageView iv_profile;
    private TextView name,phoneno,address;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        iv_profile=(CircleImageView)findViewById(R.id.iv_user_profile);
        name=(TextView)findViewById(R.id.tv_user_name);
        phoneno=(TextView)findViewById(R.id.tv_phoneno);
        address=(TextView)findViewById(R.id.tv_address);

        ProgressUtils.showLoadingDialog(this);

        DocumentReference ref=firebaseFirestore.document("users/"+mAuth.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name.setText(documentSnapshot.get("name").toString());
                    phoneno.setText(mAuth.getCurrentUser().getPhoneNumber());
                    address.setText(documentSnapshot.get("address").toString());
                    if (!documentSnapshot.get("profilepic").toString().equals("null")){

//                        Picasso.with(MyProfileActivity.this).load(documentSnapshot.get("profilepic").toString()).into(iv_profile);
                        Picasso.with(MyProfileActivity.this).load(documentSnapshot.get("profilepic").toString())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.farmer_logo).into(iv_profile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(MyProfileActivity.this).load(documentSnapshot.get("profilepic").toString())
                                        .placeholder(R.drawable.farmer_logo).into(iv_profile);

                            }
                        });
                    }
                    else {
                        Picasso.with(MyProfileActivity.this).load(farmer_logo).into(iv_profile);

                    }
                }
                ProgressUtils.cancelLoading();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void onClick(View view) {
        if (view.getId() == R.id.fab_chats) {
            Intent intent = new Intent(MyProfileActivity.this, ChatListActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.fab_noti) {
            Intent intent = new Intent(MyProfileActivity.this, NotificationActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.fab_edit) {
            Intent intent = new Intent(MyProfileActivity.this, MyProfileEditActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.fab_mypost) {
            Intent intent = new Intent(MyProfileActivity.this, MyPostsActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.fab_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Farmer");
                String sAux = "\nFarmer is an app. It's helps farmer's to post and buy product's online \n\n" +
                        "Download the app by the given link \n \n";
                sAux = sAux + "http://play.google.com/store/apps/details?id=" + this.getPackageName();
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        }
        if (view.getId() == R.id.fab_postnew) {
            Intent intent = new Intent(MyProfileActivity.this, AgreementActivity.class);
            startActivity(intent);
        }

    }
}
