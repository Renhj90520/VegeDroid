package com.xjconvenience.vege.vege.modules.orderdetail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.adapters.ProductListAdapter;
import com.xjconvenience.vege.vege.models.OrderItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ren Haojie on 2017/7/23.
 */

public class OrderDetailActivity extends AppCompatActivity {
    public static final String ITEM_LIST = "ITEM_LIST";

    @BindView(R.id.products_list)
    RecyclerView products_list;
    @BindView(R.id.detailtoolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("商品列表");

        List<OrderItem> products = getIntent().getParcelableArrayListExtra(ITEM_LIST);
        ProductListAdapter adapter = new ProductListAdapter(products);
        products_list.setAdapter(adapter);
        products_list.setLayoutManager(new LinearLayoutManager(this));
    }
}
