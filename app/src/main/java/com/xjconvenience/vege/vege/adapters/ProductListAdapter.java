package com.xjconvenience.vege.vege.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.models.OrderItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ren Haojie on 2017/7/24.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductHolder> {
    List<OrderItem> product_list;

    public ProductListAdapter(List<OrderItem> list) {
        product_list = list;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        OrderItem product = product_list.get(position);
        if (product != null) {
            holder.product_name.setText(product.getName());
            holder.price_count.setText("￥" + product.getPrice() + " * " + product.getCount() + " " + product.getUnitName());
            holder.cost.setText("小计：￥" + product.getCost());
        }
    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.product_name)
        TextView product_name;
        @BindView(R.id.price_count)
        TextView price_count;
        @BindView(R.id.cost)
        TextView cost;
        @BindView(R.id.product_check)
        CheckBox product_check;

        public ProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            product_check.setChecked(!product_check.isChecked());
        }
    }
}
