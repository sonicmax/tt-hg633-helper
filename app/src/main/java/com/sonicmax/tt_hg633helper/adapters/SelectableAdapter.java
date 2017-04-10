package com.sonicmax.tt_hg633helper.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import com.sonicmax.tt_hg633helper.adapters.NetworkDeviceAdapter.NetworkDeviceViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter extends RecyclerView.Adapter<NetworkDeviceViewHolder> {

    private SparseBooleanArray mSelectedItems;

    public SelectableAdapter() {
        mSelectedItems = new SparseBooleanArray();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods for handling item selection
    ///////////////////////////////////////////////////////////////////////////

    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); ++i) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void setSelection(int position) {
        mSelectedItems.put(position, true);
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        mSelectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }
}