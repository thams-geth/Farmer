package com.tts.farmer;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    List<ChatMessage> list;
    Context context;
    FirebaseAuth mAuth;

    public ChatAdapter(List<ChatMessage> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_msglist,parent,false);
        context=view.getContext();
        mAuth=FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder holder, final int position) {

        if (list.get(position).getMsgUser().equals(mAuth.getUid())) {

            holder.tv_rghttext.setText(list.get(position).getMsgText());
            holder.tv_rghttext.setVisibility(View.VISIBLE);
            holder.tv_lefttext.setVisibility(View.GONE);
        }else {
            holder.tv_lefttext.setText(list.get(position).getMsgText());
            holder.tv_lefttext.setVisibility(View.VISIBLE);
            holder.tv_rghttext.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView tv_lefttext,tv_rghttext;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            tv_lefttext=(TextView)view.findViewById(R.id.leftText);
            tv_rghttext=(TextView)view.findViewById(R.id.rightText);




        }
    }
}
