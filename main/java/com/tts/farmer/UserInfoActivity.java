package com.tts.farmer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tts.farmer.R.drawable.farmer_logo;

public class UserInfoActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private CircleImageView iv_profile;
    private TextView name,phoneno,address;
    private String userid,s_phoneno;
    private Intent callIntent;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        userid=getIntent().getStringExtra("userid");

        iv_profile=(CircleImageView)findViewById(R.id.iv_user_profile);
        name=(TextView)findViewById(R.id.tv_user_name);
        phoneno=(TextView)findViewById(R.id.tv_phoneno);
        address=(TextView)findViewById(R.id.tv_address);

        ProgressUtils.showLoadingDialog(this);

        DocumentReference ref=firebaseFirestore.document("users/"+userid);
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    name.setText(documentSnapshot.get("name").toString());
                    phoneno.setText(documentSnapshot.get("phoneno").toString());
                    s_phoneno=documentSnapshot.get("phoneno").toString();
                    address.setText(documentSnapshot.get("address").toString());

                    if (!documentSnapshot.get("profilepic").toString().equals("null")){

                        Picasso.with(UserInfoActivity.this).load(documentSnapshot.get("profilepic").toString())
                                .placeholder(farmer_logo).into(iv_profile);
                    }
                    else {
                        Picasso.with(UserInfoActivity.this).load(farmer_logo).into(iv_profile);

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
            Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
            intent.putExtra("sender_uid", userid);
            startActivity(intent);
        }
        if (view.getId() == R.id.fab_call) {
//            String phone = s_phoneno;
//            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
//            startActivity(intent);

            callIntent = new Intent(Intent.ACTION_CALL,Uri.fromParts("tel",s_phoneno,null));
//            callIntent.setData(Uri.parse(s_phoneno));
            if(isPermissionGranted()){
                startActivity(callIntent);
            }
        }

        if (view.getId() == R.id.fab_report) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, "farmerapp@gmail.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Report user");
            intent.putExtra(Intent.EXTRA_TEXT, "The user content is fake.\n" +
                    "User id : "+userid +"\nName : "+name.getText().toString()+"\nPhoneno : "+s_phoneno);

            startActivity(Intent.createChooser(intent, "Send Email"));
        }


    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    startActivity(callIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
