package com.example.smallbusiness.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smallbusiness.R;
import com.example.smallbusiness.models.food.Food;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    LayoutInflater inflater;
    List<Food> cartItemList;

    public CartAdapter(Context context, List<Food> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("AAA loi", cartItemList.get(position).toString());
        Picasso.get().load(cartItemList.get(position).getImage()).into(holder.imgFood);
        holder.tvFoodName.setText(cartItemList.get(position).getName());
        holder.tvFoodPrice.setText(cartItemList.get(position).getPrice()+"");
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food) ImageView imgFood;
        @BindView(R.id.tv_food_name) TextView tvFoodName;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;
        @BindView(R.id.tv_quantity) TextView tvQuantity;
        @BindView(R.id.img_decrease) ImageView imgDecrease;
        @BindView(R.id.img_increase) ImageView imgIncrease;
        @BindView(R.id.tv_food_price_new) TextView tvNewPrice;
        @BindView(R.id.tv_extra_price) TextView tvExtraPrice;
        @BindView(R.id.img_delete_food) ImageView imgDeleteFood;

        Unbinder unbinder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            imgDecrease.setOnClickListener(this);
            imgIncrease.setOnClickListener(this);
            imgDeleteFood.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
