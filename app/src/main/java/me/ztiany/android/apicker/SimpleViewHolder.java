package me.ztiany.android.apicker;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SimpleViewHolder extends RecyclerView.ViewHolder {

    private final ItemHelper mHelper;

    public ItemHelper helper() {
        return mHelper;
    }

    public SimpleViewHolder(View itemView) {
        super(itemView);
        mHelper = new ItemHelper(itemView);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

}