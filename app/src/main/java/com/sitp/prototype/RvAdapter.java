package com.sitp.prototype;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.TvHolder>
    implements View.OnClickListener{

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Device> mData;
    private RvListener mListener;

    public RvAdapter(Context context, List<Device> devices) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mData = devices;
    }

    @Override
    public RvAdapter.TvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RvAdapter.TvHolder(mLayoutInflater.inflate(R.layout.recycler_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final RvAdapter.TvHolder holder, int pos) {
        holder.mTextView.setText(mData.get(pos).getName());
        holder.mTextView.setTag(pos);
        holder.mTextView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public interface RvListener {
        public void onItemClicked(int position);
    }

    public void setRvListener(RvAdapter.RvListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mListener.onItemClicked(position);
    }

    class TvHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        TvHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_id);
        }
    }
}
