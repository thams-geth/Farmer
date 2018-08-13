package com.tts.farmer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.tts.farmer.R.drawable.farmer_logo;

public class MyProfileEditActivity extends AppCompatActivity {

    private CircleImageView iv_profile;
    private TextInputLayout tl_name;
    private TextInputEditText te_name;
    private TextView tv_address;
    private TextView phoneno;
    private boolean valid=true;
    private int image_code=1;
    private Uri image_uri=null;
    private String name,address;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private Map<String,Object> map=new HashMap<>();
    private ProgressDialog progressDialog;
    private File compressedImageBitmap;
    private static final int PLACE_PICKER_REQUEST = 2;
    private File actualImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile_edit);

        iv_profile=(CircleImageView)findViewById(R.id.iv_user_profile);
        tl_name=(TextInputLayout)findViewById(R.id.tl_name);
        te_name=(TextInputEditText)findViewById(R.id.te_name);
        tv_address=(TextView)findViewById(R.id.tv_address);
        phoneno=(TextView)findViewById(R.id.tv_phoneno);


        mAuth=FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving...");
        progressDialog.setCancelable(false);

        ProgressUtils.showLoadingDialog(this);

        DocumentReference ref=firebaseFirestore.document("users/"+mAuth.getUid());
        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                        te_name.setText(documentSnapshot.get("name").toString());
                        tv_address.setText(documentSnapshot.get("address").toString());
                        address=documentSnapshot.get("address").toString();


                    if (!documentSnapshot.get("profilepic").toString().equals("null")) {

                        Picasso.with(MyProfileEditActivity.this).load(documentSnapshot.get("profilepic").toString())
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.farmer_logo).into(iv_profile, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                                Picasso.with(MyProfileEditActivity.this).load(documentSnapshot.get("profilepic").toString())
                                        .placeholder(R.drawable.farmer_logo).into(iv_profile);

                            }
                        });

                        map.put("profilepic", documentSnapshot.get("profilepic").toString());


                    }else {
                        Picasso.with(MyProfileEditActivity.this).load(farmer_logo).into(iv_profile);
                        map.put("profilepic", "null");

                    }
                }else {
                    map.put("profilepic", "null");
                }
                phoneno.setText(mAuth.getCurrentUser().getPhoneNumber());
                ProgressUtils.cancelLoading();

            }

        });
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        firebaseFirestore.setFirestoreSettings(settings);


    }

    public void onClick(View view) {
        if(view.getId()==R.id.fa_edit){
            Intent im=new Intent(Intent.ACTION_GET_CONTENT);
            im.setType("image/*");
            startActivityForResult(im,image_code);
        }
        if (view.getId()==R.id.b_save){

            if(valiate()){
                if (image_uri!=null){
                    uploadStorage();
                }else {
                    uploadDatabase();
                }

            }

        }
        if (view.getId()==R.id.tv_address){

                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().
                        setTypeFilter(Place.TYPE_COUNTRY).setCountry("IN").build();
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(typeFilter)
                        .build(this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadStorage() {
        showDialog();
        final StorageReference sref=firebaseStorage.getReference("users/"+mAuth.getUid()+"prfilepic.jpg");
        final StorageReference srefthumb=firebaseStorage.getReference("users/thumb/"+mAuth.getUid()+"prfilepic.jpg");

        try {
            compressedImageBitmap = new Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(70)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .compressToFile(actualImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UploadTask uploadTask=firebaseStorage.getReference("users/"+mAuth.getUid()+"prfilepic.jpg")
                .putFile(Uri.fromFile(compressedImageBitmap));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        map.put("profilepic",uri.toString());
                        try {
                            compressedImageBitmap = new Compressor(MyProfileEditActivity.this)
                                    .setMaxWidth(50)
                                    .setMaxHeight(50)
                                    .setQuality(50)
                                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                                    .compressToFile(actualImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UploadTask uploadTask=firebaseStorage.getReference("users/thumb/"+mAuth.getUid()+"prfilepic.jpg")
                                .putFile(Uri.fromFile(compressedImageBitmap));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                srefthumb.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        map.put("profilepicthumb",uri.toString());
                                        uploadDatabase();

                                    }
                                });



                            }
                        });

                    }
                });



            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int currentprogress = (int) progress;
                progressDialog.setMessage(String.valueOf(currentprogress)+"% done.");

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        });


    }
    @Override
    public void onBackPressed()
    {
//        super.onBackPressed();  // optional depending on your needs

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure to cancel?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void uploadDatabase() {

        showDialog();
        map.put("name",name);
        map.put("address",address);
        map.put("phoneno",mAuth.getCurrentUser().getPhoneNumber());

        DocumentReference dref=firebaseFirestore.document("users/"+mAuth.getUid());
        dref.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyProfileEditActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                hideDialog();
                Intent intent = new Intent(MyProfileEditActivity.this, MyProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            }
        });

    }
    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private boolean valiate() {
        name=te_name.getText().toString();
        valid=true;

        if (!TextUtils.isEmpty(name)){
            tl_name.setErrorEnabled(false);

        }else {
            valid=false;
            tl_name.setError("Please enter name");
        }
        if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "Please select your address", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==image_code&& resultCode==RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "photo not seleted", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                actualImage = FileUtil.from(this, data.getData());
                iv_profile.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
            } catch (IOException e) {
                Toast.makeText(this, "Failed to read picture data!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            image_uri=data.getData();
            iv_profile.setImageURI(image_uri);
        }
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            Place place = PlaceAutocomplete.getPlace(this, data);
            address=place.getName()+"\n"+place.getAddress();
            tv_address.setText(address);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


}
