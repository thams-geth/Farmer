package com.tts.farmer;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder>{
    List<Notification> list;

    public NotificationListAdapter(List<Notification> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public NotificationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items_ntification,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationListAdapter.ViewHolder holder, int position) {

        holder.title.setText(list.get(position).getTitle());
        holder.content.setText(list.get(position).getContent());
        holder.date.setText(list.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView title,content,date;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;
            title=(TextView)view.findViewById(R.id.tv_title);
            content=(TextView)view.findViewById(R.id.tv_content);
            date=(TextView)view.findViewById(R.id.tv_date);

        }
    }
}
