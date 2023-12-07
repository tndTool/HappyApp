package com.example.happyapp.model;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class HistoryDiffCallback extends DiffUtil.Callback {
    private List<History> oldList;
    private List<History> newList;

    public HistoryDiffCallback(List<History> oldList, List<History> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        History oldHistory = oldList.get(oldItemPosition);
        History newHistory = newList.get(newItemPosition);
        return oldHistory.getBehavior().equals(newHistory.getBehavior()) &&
                oldHistory.getCreateAt().equals(newHistory.getCreateAt());
    }
}
