package com.tts.farmer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private List<ChatList> list;
    private ChatListAdapter chatListAdapter;
    private TextView tv_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My chats");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mAuth=FirebaseAuth.getInstance();

        recyclerView=(RecyclerView)findViewById(R.id.rv_chatlist);
        tv_error=(TextView)findViewById(R.id.tv_error);

        mAuth=FirebaseAuth.getInstance();
        list=new ArrayList<>();
        chatListAdapter=new ChatListAdapter(list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatListActivity.this));
        recyclerView.setAdapter(chatListAdapter);



    }

    private void chatlist_database() {
        ProgressUtils.showLoadingDialog(this);
        list.clear();

        firebaseFirestore.collection("users/"+mAuth.getUid()+"/chats").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()){
                            tv_error.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            tv_error.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            list.clear();

                            for (final QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                firebaseFirestore.document("users/"+document.getId()).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                ChatList chatList=documentSnapshot.toObject(ChatList.class).
                                                        withid(document.getId(),document.get("timestamp").toString());
                                                list.add(chatList);
                                                chatListAdapter.notifyDataSetChanged();
                                            }
                                        });


                            }
                        }
                    }
                });

        ProgressUtils.cancelLoading();

    }

    @Override
    protected void onStart() {
        super.onStart();

        chatlist_database();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
