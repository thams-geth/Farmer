package com.tts.farmer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SellActivity extends AppCompatActivity {
    public static final String PRICE = "price";
    public static final String QUANTITY = "quantity";
    public static final String QUANTITY_TYPE = "quantity_type";
    public static final String DETAILS = "details";
    public static final String CATEGORY = "category";
    public static final String PRODUCT = "product";
    private ProgressDialog progressDialog;
    private Spinner sp_category,sp_product,sp_quantity;
    private boolean valid=true;
    private int image_code=1;
    private Uri image_uri=null;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private int i_price,i_quantity;
    private String s_category,s_product,s_quantity,s_details,s_location_name="",
            s_location_address="",s_location_lat="",s_location_lng="";

    private TextInputLayout tl_price,tl_quantity,tl_details;
    private TextInputEditText te_price,te_quantity,te_details;
    private ImageView iv_image;
    private File compressedImageBitmap;

    private static final int PLACE_PICKER_REQUEST = 2;
    private TextView tv_location;
    private Button b_changeimage;
    private File actualImage;
    private List<String> list_category = new ArrayList<String>();
    private List<String> list_product = new ArrayList<String>();
    private int checkSpinnerInitialization = 0;
    private Map<String,Object> map;
    private Intent image_intent;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        mAuth=FirebaseAuth.getInstance();
        iv_image=(ImageView)findViewById(R.id.iv_image);
        sp_category=(Spinner)findViewById(R.id.sp_category);
        sp_product=(Spinner)findViewById(R.id.sp_product);
        sp_quantity=(Spinner)findViewById(R.id.sp_quantity);
        tl_price=(TextInputLayout) findViewById(R.id.tl_price);
        tl_quantity=(TextInputLayout) findViewById(R.id.tl_quantity);
        tl_details=(TextInputLayout) findViewById(R.id.tl_details);
        te_price=(TextInputEditText)findViewById(R.id.te_price);
        te_quantity=(TextInputEditText)findViewById(R.id.te_quantity);
        te_details=(TextInputEditText)findViewById(R.id.te_details);
        tv_location=(TextView)findViewById(R.id.tv_location);
        b_changeimage=(Button)findViewById(R.id.b_changephoto);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


//        Intent im=new Intent(Intent.ACTION_GET_CONTENT);
//        im.setType("image/*");
//        startActivityForResult(im,image_code);



        firebaseFirestore.collection("sell")
        .document("sell")
        .collection("category")
        .get()
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc :queryDocumentSnapshots){
                    String s=doc.getString("name");
                    list_category.add(s);
                }
                ArrayAdapter<String> adapter_category = new ArrayAdapter<String>(SellActivity.this,
                        android.R.layout.simple_spinner_item, list_category);
                adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_category.setAdapter(adapter_category);
            }
        });



//        ArrayAdapter adapter1 = ArrayAdapter.class(this, R.array.categories, android.R.layout.simple_spinner_item);
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        sp_category.setAdapter(adapter1);


        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this, R.array.Quantity, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_quantity.setAdapter(adapter3);

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(++checkSpinnerInitialization > 1) {

                    Object item = parent.getItemAtPosition(pos);
                    list_product.clear();

                    s_category = item.toString();
                    firebaseFirestore.collection("sell")
                            .document("sell")
                            .collection(item.toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                        String s = doc.getString("name");
                                        list_product.add(s);
                                    }
                                    ArrayAdapter<String> adapter_product = new ArrayAdapter<String>(SellActivity.this,
                                            android.R.layout.simple_spinner_item, list_product);
                                    adapter_product.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp_product.setAdapter(adapter_product);
                                }
                            });
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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
                iv_image.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
            } catch (IOException e) {
                Toast.makeText(this, "Failed to read picture data!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            image_uri=data.getData();
            iv_image.setImageURI(image_uri);
            b_changeimage.setText("Change photo");

        }
        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            Place place = PlaceAutocomplete.getPlace(this, data);

            tv_location.setText(place.getName()+"\n"+place.getAddress());
            s_location_name=place.getName().toString();
            s_location_address=place.getAddress().toString();
            s_location_lat= String.valueOf(place.getLatLng().latitude);
            s_location_lng= String.valueOf(place.getLatLng().longitude);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }
    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    public void onClick(View view) {
        if(view.getId()==R.id.b_post){

            if(validate()) {
                progressDialog.setTitle("Uploading...");
                showDialog();
                Posting();

            }

        }
        if (view.getId()==R.id.tv_location){
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().
                    setTypeFilter(Place.TYPE_COUNTRY).setCountry("IN").build();
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(typeFilter)
                        .build(this);
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        if (view.getId()==R.id.b_changephoto){

            image_intent=new Intent(Intent.ACTION_GET_CONTENT);
            image_intent.setType("image/*");
            if (isPermissionGranted()){
                startActivityForResult(image_intent,image_code);
            }
        }
    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                    startActivityForResult(image_intent,image_code);
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void Posting() {
        map=new HashMap<>();
        map.put(PRICE,i_price);
        map.put(QUANTITY,i_quantity);
        map.put(QUANTITY_TYPE,s_quantity);
        map.put(DETAILS,s_details);
        map.put(PRODUCT,s_product);
        map.put(CATEGORY,s_category);
        map.put("userid",mAuth.getUid());
        map.put("location_name",s_location_name);
        map.put("location_address",s_location_address);
        map.put("location_lat",s_location_lat);
        map.put("location_lng",s_location_lng);
        map.put("boost","null");


        if (image_uri!=null){
            UploadStorage();
        }
        else {
            map.put("image", "");
        }


    }

    private void UploadStorage() {
        final StorageReference ref = firebaseStorage.
                getReference("product").child(s_category).child(s_product).child(i_price+i_quantity+ ".jpg");


        //file from stored directory
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

        UploadTask uploadTask=firebaseStorage.getReference("product").child(s_category).child(s_product)
                .child(i_price+i_quantity+ ".jpg").putFile(Uri.fromFile(compressedImageBitmap));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imagethumb = uri.toString();
                        map.put("image", imagethumb);
                        UploadDatabase();
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

    private void UploadDatabase() {
        map.put("timestamp",""+ System.currentTimeMillis());
        final CollectionReference collectionReference=firebaseFirestore.collection("product/product/"+s_category);
        final CollectionReference usercollectionReference=firebaseFirestore
                .collection("users/"+mAuth.getUid()+"/posts");
        collectionReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                usercollectionReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SellActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        hideDialog();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SellActivity.this, " Firestore Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                });

            }
        });
    }

    private boolean validate() {
        valid=true;
        s_product=sp_product.getSelectedItem().toString();
        s_quantity=sp_quantity.getSelectedItem().toString();


        if (!TextUtils.isEmpty(te_price.getText().toString())){
            i_price=Integer.parseInt(te_price.getText().toString());
            tl_price.setErrorEnabled(false);
        }
        else{
            tl_price.setError("Please Enter price");
            valid=false;
        }

        if (!TextUtils.isEmpty(te_quantity.getText().toString())){
            i_quantity=Integer.parseInt(te_quantity.getText().toString());
            tl_quantity.setErrorEnabled(false);
        }
        else{
            tl_quantity.setError("Please Enter quantity");
            valid=false;
        }
        if (!TextUtils.isEmpty(te_details.getText().toString())){
            s_details=te_details.getText().toString();
            tl_details.setErrorEnabled(false);
        }
        else{
            tl_details.setError("Please Enter details");
            valid=false;
        }
        if (TextUtils.isEmpty(s_location_address)){
            valid=false;
            Toast.makeText(this, "Please select your location", Toast.LENGTH_SHORT).show();
        }

        return  valid;


    }
}
