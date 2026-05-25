package com.datn.smarttray.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.smarttray.R;
import com.datn.smarttray.model.Food;

import java.util.List;

public class FoodAdapter
        extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private List<Food> foodList;
    private OnFoodClickListener listener;
    public interface OnFoodClickListener{

        void onFoodClick(Food food);

    }

    public FoodAdapter(List<Food> foodList,OnFoodClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_food,
                        parent,
                        false
                );

        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FoodViewHolder holder,
            int position
    ) {

        Food food = foodList.get(position);

        holder.txtFoodName.setText(
                food.getNameViet()
        );

        holder.txtFoodPrice.setText(
                food.getPrice() + " VNĐ"
        );
        String imageUrl = food.getImageUrl();

        if (imageUrl == null
                || imageUrl.isEmpty()
                || imageUrl.equals("img")) {

            holder.imgFood.setImageResource(
                    R.drawable.ic_baseline_fastfood_24
            );

        } else {

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_baseline_fastfood_24)
                    .error(R.drawable.ic_baseline_fastfood_24)
                    .into(holder.imgFood);
        }
        holder.itemView.setOnClickListener(v->{
            listener.onFoodClick(food);
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    static class FoodViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgFood;

        TextView txtFoodName;

        TextView txtFoodPrice;

        public FoodViewHolder(@NonNull View itemView) {

            super(itemView);

            imgFood =
                    itemView.findViewById(R.id.imgFood);

            txtFoodName =
                    itemView.findViewById(R.id.txtFoodName);

            txtFoodPrice =
                    itemView.findViewById(R.id.txtFoodPrice);


        }
    }
}
