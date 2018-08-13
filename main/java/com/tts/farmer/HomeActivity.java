package com.tts.farmer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import io.grpc.Server;


public class HomeActivity extends AppCompatActivity implements HomeRecyclerViewAdapter.ItemClickListener {


    int logos[] = { R.drawable.h_sack, R.drawable.h_onion, R.drawable.h_strawberry, R.drawable.h_broccoli,
                    R.drawable.h_seeds, R.drawable.h_wheelbarrow2,R.drawable.h_radish,R.drawable.h_plant,R.drawable.h_mooligai,
                    R.drawable.h_malai,R.drawable.h_fertilizer,R.drawable.h_cow,R.drawable.h_grain,R.drawable.h_field,
                    R.drawable.h_equipment,R.drawable.h_animalfood,R.drawable.h_others,R.drawable.h_oil,R.drawable.h_oil_seeds,
                    R.drawable.h_seeeds,R.drawable.h_tree,R.drawable.h_crops};
    String[] name={ "Primary","Vegitables","Fruits","Spinach","Dhaniyam","Payiru","Kilangu","Flower","Mooligai",
                    "Malaithotta Payirgal","Fertilizers","Veterinary nurses","Ithara sagupadigal","Field","Equipment",
                    "Animal food","Others","Oil","Oil seeds","Seeds","Tree","Crops"};
    private HomeRecyclerViewAdapter adapter;
    private RecyclerView recyclerView,rv_boost;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private List<Product_small> list;
    private ProductListAdapterSmall productListAdapterSmall;
    private CardView cardViewFeatured;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        cardViewFeatured=(CardView)findViewById(R.id.cardview_featured);

        recyclerView = (RecyclerView) findViewById(R.id.rv_home);
        int numberOfColumns = 5;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new HomeRecyclerViewAdapter(this, logos,name);
        adapter.setClickListener(this);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(adapter);

        list=new ArrayList<>();
        productListAdapterSmall=new ProductListAdapterSmall(list);

        rv_boost= (RecyclerView) findViewById(R.id.rv_boost);
        rv_boost.setHasFixedSize(true);
        rv_boost.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_boost.setAdapter(productListAdapterSmall);


        featured_database();

        if (mAuth.getCurrentUser()==null){
            Intent intent = new Intent(HomeActivity.this, PhoneAuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }


    }

    private void featured_database() {
        firebaseFirestore.collection("featured" ).orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            cardViewFeatured.setVisibility(View.GONE);

                        } else {
                            list.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                String docid = document.getId();
                                String category = "";
                                Product_small product = document.toObject(Product_small.class).withid(docid, category);
                                list.add(product);
                                productListAdapterSmall.notifyDataSetChanged();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null){
            Intent intent = new Intent(HomeActivity.this, PhoneAuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        if (mAuth.getCurrentUser()!=null) {
            DocumentReference ref = firebaseFirestore.document("users/" + mAuth.getCurrentUser().getUid());
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (!documentSnapshot.exists()) {
                        Intent intent = new Intent(HomeActivity.this, MyProfileEditActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAuth.getCurrentUser()==null){
            Intent intent = new Intent(HomeActivity.this, PhoneAuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        featured_database();
    }

    @Override
    public void onItemClick(View view, int position) {
        String category = null;
//        Log.i("TAG", "You clicked number " + adapter.getItem(position) + ", which is at cell position " + position);
        if (position==0){
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary Production");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==1) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Vegetables");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==2) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Fruits");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==3) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==4) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==5) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==6) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==7) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==8) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==9) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (position==10) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==11) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==12) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==13) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==14) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==15) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==16) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==17) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==18) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==19) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==20) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }
        if (position==21) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "Primary");
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        }

    }
    public void onClick(View view) {
        if (view.getId() == R.id.action_account) {
            Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.action_sell) {
            Intent intent = new Intent(HomeActivity.this, AgreementActivity.class);
            startActivity(intent);
        } if (view.getId() == R.id.tv_viewall) {
            Intent intent = new Intent(HomeActivity.this, ProductList.class);
            intent.putExtra("type", "");
            startActivity(intent);
        }
//        if (view.getId() == R.id.rl_profile) {
//            Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
//            startActivity(intent);
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, PhoneAuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void onImageClick(View view) {
//        if (view.getId() == R.id.iv_primary) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary Production");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_vegitables) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Vegetables");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_fruits) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Fruits");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_spinach) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_dhaniyam) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_payiru) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_kilangu) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_flower) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_mooligai) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_malaithottam) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        }
//        if (view.getId() == R.id.iv_fertilizers) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_vertinarynurces) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_ithara_saagupadigal) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_field) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_equipment) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_animalfood) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_others) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_oil) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_oilseeds) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_seeds) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_tree) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//        if (view.getId() == R.id.iv_crops) {
//            Intent intent = new Intent(HomeActivity.this, ProductList.class);
//            intent.putExtra("type", "Primary");
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//
//        }
//    }
}
