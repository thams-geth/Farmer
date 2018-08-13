package com.tts.farmer;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {
    private CollectionReference collectionReference= FirebaseFirestore.getInstance().collection("users");
    private RecyclerView recyclerView;
    private List<Mypost_small> list;
    private MypostListAdapter mypostListAdapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Posts");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mAuth=FirebaseAuth.getInstance();


        list=new ArrayList<>();
        mypostListAdapter=new MypostListAdapter(list);

        recyclerView= (RecyclerView) findViewById(R.id.rv_mypost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
//        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(mypostListAdapter);


        ProgressUtils.showLoadingDialog(this);

        collectionReference.document(mAuth.getUid()).collection("posts").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                    String username=doc.getString("name");
                    String docid=document.getId();
                    Mypost_small product=document.toObject(Mypost_small.class).withid(docid);
                    list.add(product);
                    mypostListAdapter.notifyDataSetChanged();
                }
                ProgressUtils.cancelLoading();
            }
        });


    }

}

