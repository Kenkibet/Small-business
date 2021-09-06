package com.example.smallbusiness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smallbusiness.R;
import com.example.smallbusiness.api.APIConstants;
import com.example.smallbusiness.api.IRestaurantAPI;
import com.example.smallbusiness.api.RetrofitClient;
import com.example.smallbusiness.models.food.Food;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>  {
    Context context;
    LayoutInflater inflater;
    List<Food> foodList;
    CompositeDisposable compositeDisposable;
    IRestaurantAPI myRestaurantAPI;

    public void onStop() {
        this.compositeDisposable.clear();
    }

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.compositeDisposable = new CompositeDisposable();
        this.myRestaurantAPI = RetrofitClient.getInstance(APIConstants.API_ENDPOINT).create(IRestaurantAPI.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(foodList.get(position).getImage()).into(holder.imgFood);
        holder.tvFoodName.setText(foodList.get(position).getName());
        holder.tvFoodPrice.setText(foodList.get(position).getPrice() + " VND");

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_food) ImageView imgFood;
        @BindView(R.id.tv_food_name) TextView tvFoodName;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;
        @BindView(R.id.img_cart) ImageView btnCart;
        @BindView(R.id.img_favorite) ImageView btnFavorite;


        Unbinder unbinder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            btnCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
