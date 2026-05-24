package com.datn.smarttray.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.smarttray.R;

import com.datn.smarttray.model.History;

import java.io.File;
import java.util.List;


public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    private final OnHistoryClickListener listener;

    public interface OnHistoryClickListener {

        void onHistoryClick(History history);

        void onDeleteClick(History history);

    }

    public HistoryAdapter(List<History> historyList,OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_history,
                        parent,
                        false
                );

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull HistoryViewHolder holder,
            int position
    ) {

        History history = historyList.get(position);

        holder.txtSumPrice.setText(
                history.getSumPrice() + " VNĐ"
        );

        holder.txtQuanlityFood.setText(
                history.getQuanlityFood() + " Món ăn"
        );
        holder.txtTime.setText(
                history.getLocalTime()
        );
        String imagePath = history.getImagePredict();

        if(imagePath == null || imagePath.isEmpty()){

            holder.imgPredict.setImageResource(
                    R.drawable.ic_baseline_fastfood_24
            );

        }else{

            Glide.with(holder.itemView.getContext())
                    .load(new File(imagePath))
                    .placeholder(R.drawable.ic_baseline_fastfood_24)
                    .error(R.drawable.ic_baseline_fastfood_24)
                    .into(holder.imgPredict);

        }
        holder.itemView.setOnClickListener(v -> {

            listener.onHistoryClick(history);

        });

        holder.btnDelete.setOnClickListener(v -> {

            listener.onDeleteClick(history);

        });

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgPredict;

        TextView txtSumPrice;

        TextView txtQuanlityFood;

        TextView txtTime;

        ImageButton btnDelete;

        public HistoryViewHolder(@NonNull View itemView) {

            super(itemView);

            imgPredict =
                    itemView.findViewById(R.id.imgPredict);

            txtSumPrice =
                    itemView.findViewById(R.id.txtSumprice);

            txtQuanlityFood =
                    itemView.findViewById(R.id.txtQuanlityFood);

            txtTime =
                    itemView.findViewById(R.id.txtTime);
            btnDelete = itemView.findViewById(R.id.imageDeleteBtn);
        }
    }

}
