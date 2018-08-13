package com.tts.farmer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SellActivity_Old extends AppCompatActivity {
    public static final String PRICE = "price";
    public static final String QUANTITY = "quantity";
    public static final String QUANTITY_TYPE = "quantity_type";
    public static final String DETAILS = "details";
    public static final String CATEGORY = "category";
    public static final String PRODUCT = "product";
    private ProgressDialog progressDialog;;
    private ConstraintLayout constraintLayout;
    private NestedScrollView nestedScrollView;
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
    private Bitmap compressedImageBitmap;
    private File file;

    private Boolean mLocationPermissionGranted=false;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=21;
    private static final int PLACE_PICKER_REQUEST = 2;
    private TextView mName;
    private TextView mAddress;
    private TextView mAttributions;
    private TextView tv_location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        mAuth=FirebaseAuth.getInstance();

        constraintLayout=(ConstraintLayout)findViewById(R.id.constraint);
        nestedScrollView=(NestedScrollView)findViewById(R.id.nestedscrollview);
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

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Intent im=new Intent(Intent.ACTION_GET_CONTENT);
        im.setType("image/*");
        startActivityForResult(im,image_code);




        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        sp_category.setAdapter(adapter1);

//        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.Payiru_vagaigal, R.layout.spinner_item);
//        sp_product.setAdapter(adapter2);
        sp_product.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this, R.array.Quantity, R.layout.spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_quantity.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        sp_quantity.setAdapter(adapter3);
        sp_quantity.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);



        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                Toast.makeText(SellActivity_Old.this, "item :" +item +"pos :"+pos, Toast.LENGTH_SHORT).show();

                s_category=item.toString();

                if(sp_category.getSelectedItem().toString().equals("Primary Production")) {
                    ArrayAdapter adapter = ArrayAdapter.createFromResource(SellActivity_Old.this,
                            R.array.Primary_Production,
                            R.layout.spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_product.setAdapter(adapter);

                }
                if(pos==1) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SellActivity_Old.this,
                            R.array.Vegetables,
                            R.layout.spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_product.setAdapter(adapter);

                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
//    private class MyLocationListener implements LocationListener {
//        @Override
//        public void onLocationChanged(Location loc) {
//
//            editLocation.setText("");
//            pb.setVisibility(View.INVISIBLE);
//            Toast.makeText(
//                    getBaseContext(),
//                    "Location changed : Lat: " + loc.getLatitude() + " Lng: "
//                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
//            String longitude = "Longitude: " + loc.getLongitude();
//            Log.v(TAG, longitude);
//            String latitude = "Latitude: " + loc.getLatitude();
//            Log.v(TAG, latitude);
//
//            /*----------to get City-Name from coordinates ------------- */
//            String cityName = null;
//            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(loc.getLatitude(),
//                        loc.getLongitude(), 1);
//                if (addresses.size() > 0)
//                    System.out.println(addresses.get(0).getLocality());
//                cityName = addresses.get(0).getLocality();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String s = longitude + "\n" + latitude + "\n\nMy Currrent City is: "
//                    + cityName;
//            editLocation.setText(s);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            // TODO Auto-generated method stub
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            // TODO Auto-generated method stub
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            // TODO Auto-generated method stub
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==image_code&& resultCode==RESULT_OK) {
            image_uri=data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                String dirName = FieldValue.serverTimestamp().toString()+".jpg";
                createDirectoryAndSaveFile(bitmap,dirName);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        checkImgUrl();

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

    private void checkImgUrl() {
        if(image_uri==null){
            finish();
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Farmer");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/Farmer");
            wallpaperDirectory.mkdirs();
        }

        file = new File(new File(Environment.getExternalStorageDirectory() + "/Farmer"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(file));
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            nestedScrollView.setBackground(bitmapDrawable);
        } catch (Exception e) {
            e.printStackTrace();
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

            if(validate()){
                progressDialog.setTitle("Uploading...");
                showDialog();

                final Map<String,Object> map=new HashMap<>();
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


                final CollectionReference collectionReference=firebaseFirestore.collection("product/product/"+s_category);
                final CollectionReference usercollectionReference=firebaseFirestore
                        .collection("users/"+mAuth.getUid()+"/posts");

                final StorageReference ref = firebaseStorage.getReference("product").child(s_category).child(s_product).child(i_price+i_quantity+ ".jpg");


//                        File actualImageFile=new File(image_uri.getPath());
                                //file from stored directory
                                try {
                                    compressedImageBitmap = new Compressor(SellActivity_Old.this)
                                            .setMaxHeight(150)
                                            .setMaxWidth(150)
                                            .setQuality(10)
                                            .compressToBitmap(file);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                                compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,60,baos);
                                byte[] tumbdata=baos.toByteArray();


                                UploadTask uploadTask=firebaseStorage.getReference("product").child(s_category).child(s_product).child(i_price+i_quantity+ ".jpg").putBytes(tumbdata);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String imagethumb = uri.toString();
                                                map.put("image", imagethumb);
                                                map.put("timestamp",""+ System.currentTimeMillis());

                                                collectionReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        usercollectionReference.add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Toast.makeText(SellActivity_Old.this, "Success", Toast.LENGTH_SHORT).show();
                                                                hideDialog();
                                                                finish();

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(SellActivity_Old.this, " Firestore Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                hideDialog();
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

        }
        if (view.getId()==R.id.tv_location){
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
        if (view.getId()==R.id.b_changephoto){

            Intent im=new Intent(Intent.ACTION_GET_CONTENT);
            im.setType("image/*");
            startActivityForResult(im,image_code);
        }
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
