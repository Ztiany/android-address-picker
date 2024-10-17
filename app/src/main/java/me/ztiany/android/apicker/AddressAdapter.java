package me.ztiany.android.apicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

final class AddressAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

    private View.OnClickListener mItemClickListener;

    private final Context mContext;

    private final List<IName> mINames = new ArrayList<>();

    void setItemClickListener(View.OnClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    void replaceAll(List<IName> items) {
        mINames.clear();
        mINames.addAll(items);
        notifyDataSetChanged();
    }

    AddressAdapter(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        mContext = context;
    }

    @NonNull
    @Override
    public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_address_item_select, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder viewHolder, int position) {
        TextView nameView = viewHolder.helper().getView(R.id.tv_province);
        IName item = mINames.get(position);
        IName.AddressToken addressToken = item.getAddressToken();
        nameView.setText(addressToken.getName());
        viewHolder.itemView.setTag(item);
        viewHolder.itemView.setOnClickListener(mItemClickListener);
    }

    @Override
    public int getItemCount() {
        return (mINames == null) ? 0 : mINames.size();
    }

}