package com.xjconvenience.vege.vege.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.models.Order;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    private List<Order> mOrderList;
    private Context mContext;

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
