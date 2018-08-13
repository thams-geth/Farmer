package com.tts.farmer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<ChatMessage> list;
    private ChatAdapter chatListAdapter;
    private String sender_uid;
    private EditText et_msg;
    private RelativeLayout rl_sendmsg;
    private TextView tv_error;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        sender_uid = getIntent().getStringExtra("sender_uid");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.cus_chat, null);

        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----
        final TextView tv_user_name=(TextView)findViewById(R.id.tv_user_name);
        final ImageView  iv_user_image=(ImageView)findViewById(R.id.iv_user_image);

        firebaseFirestore.collection("users").document(sender_uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_user_name.setText(documentSnapshot.getString("name"));
                Picasso.with(ChatActivity.this).load(documentSnapshot.getString("profilepicthumb"))
                        .into(iv_user_image);


            }
        });




        recyclerView = (RecyclerView) findViewById(R.id.rv_chat);
        et_msg = (EditText) findViewById(R.id.et_msg);
        rl_sendmsg = (RelativeLayout) findViewById(R.id.rl_sendmsg);
        tv_error=(TextView)findViewById(R.id.tv_error);


        mAuth = FirebaseAuth.getInstance();
        list = new ArrayList<>();
        chatListAdapter = new ChatAdapter(list);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        firebaseFirestore.collection("users/" + mAuth.getUid() + "/chats/" + sender_uid + "/" + sender_uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (queryDocumentSnapshots.isEmpty()){
                            tv_error.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else {
                            tv_error.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                                ChatMessage chatMessage = doc.getDocument().toObject(ChatMessage.class);
                                list.add(chatMessage);
                                chatListAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(chatListAdapter.getItemCount() - 1);
                            }
                        }




                    }
                });


        recyclerView.setAdapter(chatListAdapter);
        rl_sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_msg.getText().toString().trim();

                if (!message.equals("")) {

                    ChatMessage chatMessage = new ChatMessage(message, mAuth.getUid());

                    HashMap<String, Object> mapcurrentuser = new HashMap();
                    mapcurrentuser.put("timestamp", "" + System.currentTimeMillis());

                    HashMap<String, Object> mapsender = new HashMap();
                    mapsender.put("timestamp", "" + System.currentTimeMillis());

//                    Map<String,Object> map = snapshot.getData();
//                    Date date = (Date) map.get("timestamp");
//                    Log.d(TAG, "date=" + date);
//                    Log.d(TAG, "time=" + date.getTime());


                    //message
                    //mine
                    firebaseFirestore.collection("users/"
                            + mAuth.getUid() + "/chats/" + sender_uid + "/" + sender_uid).add(chatMessage);
                    //other
                    firebaseFirestore.collection("users/"
                            + sender_uid + "/chats/" + mAuth.getUid() + "/" + mAuth.getUid()).add(chatMessage);
                    //mine
                    firebaseFirestore.document("users/"
                            + mAuth.getUid() + "/chats/" + sender_uid).set(mapcurrentuser);
                    //other
                    firebaseFirestore.document("users/"
                            + sender_uid + "/chats/" + mAuth.getUid()).set(mapsender);


                }
                et_msg.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}
