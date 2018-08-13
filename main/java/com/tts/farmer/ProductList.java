package com.tts.farmer;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Product_small> list;
    private ProductListAdapter productListAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private String category;
    private Toolbar toolbar;
    private TextView cus_tv_lcoation;
    private Query nextQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.cus_pro_list, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        cus_tv_lcoation=(TextView)findViewById(R.id.cus_tv_location);
        cus_tv_lcoation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        category=getIntent().getStringExtra("type");

        list=new ArrayList<>();
        productListAdapter=new ProductListAdapter(list);

        recyclerView= (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
//        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(productListAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if(reachedBottom){
                    if (lastVisible.exists()){

                        if (category.equals("")) {
                            loadMorePostHome();
                        }else {
                            loadMorePost();
                        }
                    }



                }

            }
        });

        ProgressUtils.showLoadingDialog(this);

        if (category.equals("")){
            nextQuery = FirebaseFirestore.getInstance().collection("featured")
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

            nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){
                        return;
                    }
                    if (isFirstPageFirstLoad) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        list.clear();
                    }
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String docid=document.getId();
                        Product_small product=document.toObject(Product_small.class).withid(docid,category);
                        list.add(product);
                        productListAdapter.notifyDataSetChanged();
                    }
                    ProgressUtils.cancelLoading();
                    isFirstPageFirstLoad = false;
                }
            });
        }else {
            nextQuery = FirebaseFirestore.getInstance().collection("product")
                    .document("product").collection(category)
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

            nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()){
                        return;
                    }
                    if (isFirstPageFirstLoad) {
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        list.clear();
                    }
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String docid=document.getId();
                        Product_small product=document.toObject(Product_small.class).withid(docid,category);
                        list.add(product);
                        productListAdapter.notifyDataSetChanged();
                    }
                    ProgressUtils.cancelLoading();
                    isFirstPageFirstLoad=false;
                }
            });
        }

//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//
//
//                        for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
////                    String username=doc.getString("name");
//                            Product product=doc.getDocument().toObject(Product.class).withid("userid",item);
//                            list.add(product);
//                            productListAdapter.notifyDataSetChanged();
//                        }
//                        ProgressUtils.cancelLoading();
//
//                    }
//                });


    }
    public void loadMorePostHome(){

        nextQuery = FirebaseFirestore.getInstance().collection("featured")
                .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);

        nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String docid = document.getId();
                        Product_small product = document.toObject(Product_small.class).withid(docid, category);
                        list.add(product);
                        productListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    } public void loadMorePost(){

        nextQuery = FirebaseFirestore.getInstance().collection("product")
                .document("product").collection(category)
                .orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);

        nextQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String docid = document.getId();
                        Product_small product = document.toObject(Product_small.class).withid(docid, category);
                        list.add(product);
                        productListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }
//    @Override
//    public boolean onCreateOptionsMenu( Menu menu) {
//        getMenuInflater().inflate( R.menu.menu_product_list, menu);
//
//        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
//        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                if( ! searchView.isIconified()) {
//                    searchView.setIconified(true);
//                }
//                myActionMenuItem.collapseActionView();
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });
//        return true;
//    }

    }

