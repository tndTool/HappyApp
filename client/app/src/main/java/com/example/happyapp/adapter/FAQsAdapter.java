package com.example.happyapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyapp.R;
import com.example.happyapp.model.FAQs;

import java.util.List;

public class FAQsAdapter extends RecyclerView.Adapter<FAQsAdapter.FAQsVH> {

    List<FAQs> faQsList;

    public FAQsAdapter(List<FAQs> faQsList) {
        this.faQsList = faQsList;
    }

    @NonNull
    @Override
    public FAQsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowfaqs, parent, false);
        return new FAQsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQsVH holder, int position) {

        FAQs faQs = faQsList.get(position);
        holder.titleTxt.setText(faQs.getTitle());
        holder.descriptionTxt.setText(faQs.getDescription());

        boolean isExpandable = faQsList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return faQsList.size();
    }

    public class FAQsVH extends RecyclerView.ViewHolder {
        TextView titleTxt, descriptionTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public FAQsVH(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleText);
            descriptionTxt = itemView.findViewById(R.id.descriptionText);

            linearLayout = itemView.findViewById(R.id.linearLayout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FAQs faQs = faQsList.get(getAdapterPosition());
                    faQs.setExpandable(!faQs.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
