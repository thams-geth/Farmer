package com.tts.farmer;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tts.farmer.GetTimeAgo.getTimeAgo;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{
    List<ChatList> list;
    Context context;

    public ChatListAdapter(List<ChatList> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_chat_list,parent,false);
        context=view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListAdapter.ViewHolder holder, final int position) {

        holder.tv_username.setText(list.get(position).getName());
        holder.tv_date.setText(getTimeAgo(Long.parseLong(list.get(position).category),context));
        Picasso.with(context).load(list.get(position).getProfilepicthumb())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.farmer_logo).into(holder.iv_user_profile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {

                Picasso.with(context).load(list.get(position).getProfilepicthumb())
                        .placeholder(R.drawable.farmer_logo).into(holder.iv_user_profile);

            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("sender_uid", list.get(position).docid);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView tv_username,tv_date;
        CircleImageView iv_user_profile;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            tv_username=(TextView)view.findViewById(R.id.tv_user_name);
            tv_date=(TextView)view.findViewById(R.id.tv_date);
            iv_user_profile=(CircleImageView)view.findViewById(R.id.iv_user_profile);


        }
    }
}
