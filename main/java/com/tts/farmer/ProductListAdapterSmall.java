package com.tts.farmer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Thamaraiselvan on 15-01-18.
 */

public class ProductListAdapterSmall extends RecyclerView.Adapter<ProductListAdapterSmall.ViewHolder> {

    private List<Product_small> list;

    public ProductListAdapterSmall(List<Product_small> list){
        this.list=list;
    }
    Context  context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_items_product_small,parent,false);
        context=view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tv_name.setText(list.get(position).getProduct());
        holder.tv_price.setText("₹"+list.get(position).getPrice());
        holder.tv_quantity.setText(list.get(position).getQuantity()+"/"+list.get(position).getQuantity_type());

        Picasso.with(context).load(list.get(position).getImage()).into(holder.iv_image);
        holder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("docid",list.get(position).docid);
                intent.putExtra("category",list.get(position).category);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
        View mview;
        TextView tv_name,tv_price,tv_quantity;
        ImageView iv_image;


        public  ViewHolder(View itemView){
            super(itemView);
            mview=itemView;

            tv_name=mview.findViewById(R.id.tv_user_name);
            tv_price=mview.findViewById(R.id.tv_price);
            tv_quantity=mview.findViewById(R.id.tv_quantity);
            iv_image=mview.findViewById(R.id.iv_image);

        }
    }
}
