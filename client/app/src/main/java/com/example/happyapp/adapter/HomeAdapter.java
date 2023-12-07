package com.example.happyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyapp.R;
import com.example.happyapp.model.History;
import com.example.happyapp.model.HistoryDiffCallback;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<History> historyList;

    public HomeAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_home_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.bind(history);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<History> newHistoryList) {
        HistoryDiffCallback diffCallback = new HistoryDiffCallback(historyList, newHistoryList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        historyList.clear();
        historyList.addAll(newHistoryList);

        diffResult.dispatchUpdatesTo(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView titleImage;
        private TextView tvBehavior, tvCreateAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleImage = itemView.findViewById(R.id.title_image);
            tvBehavior = itemView.findViewById(R.id.tvBehavior);
            tvCreateAt = itemView.findViewById(R.id.tvCreateAt);
        }

        public void bind(History history) {
            tvBehavior.setText(history.getBehavior());

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(history.getCreateAt());
            tvCreateAt.setText(formattedDate);

            Picasso.get()
                    .load(history.getTitleImage())
                    .fit()
                    .centerCrop()
                    .into(titleImage);
        }
    }
}
