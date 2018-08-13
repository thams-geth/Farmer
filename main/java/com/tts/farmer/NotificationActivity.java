package com.tts.farmer;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private List<Notification> list;
    private NotificationListAdapter notificationListAdapter;
    private TextView tv_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Notifications");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        recyclerView=(RecyclerView)findViewById(R.id.rv_noti);
        tv_error=(TextView)findViewById(R.id.tv_error);


        mAuth=FirebaseAuth.getInstance();
        list=new ArrayList<>();
        notificationListAdapter=new NotificationListAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        recyclerView.setAdapter(notificationListAdapter);

        firebaseFirestore.collection("users/"+mAuth.getUid()+"/notification").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()){
                    tv_error.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    tv_error.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        Notification message = document.toObject(Notification.class);
                        list.add(message);
                        notificationListAdapter.notifyDataSetChanged();


                    }
                }
            }
        });


    }
}
