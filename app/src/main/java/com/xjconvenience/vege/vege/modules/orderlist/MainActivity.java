package com.xjconvenience.vege.vege.modules.orderlist;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.VegeApplication;
import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.modules.orderdetail.OrderDetailActivity;
import com.xjconvenience.vege.vege.utils.PackUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView {
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    @BindView(R.id.order_list)
    RecyclerView order_list;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    MainContract.IMainPresenter mPresenter;
    private int lastVisibleIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        order_list.setLayoutManager(new LinearLayoutManager(this));
        DaggerMainComponent.builder().vegeServicesComponent(((VegeApplication) getApplication()).getVegeServicesComponent())
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        mPresenter.loadOrders(lastVisibleIndex);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshOrders();
            }
        });

        order_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public void showProgress() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setItems(OrderListAdapter adapter) {
        order_list.setAdapter(adapter);
    }

    @Override
    public void navigateToDetail(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.ITEM_LIST, order.getProducts());
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dialUser(String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    @Override
    public void callMap(double latitude, double longitude) {
        if (PackUtil.isInstalled(this, "com.baidu.BaiduMap")) {
            Uri uri = Uri.parse("baidumap://map/marker?location=" + latitude + "," + longitude + "&title=用户位置&content=&traffic=on");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            startActivity(intent);
        } else if (PackUtil.isInstalled(this, "com.autonavi.minimap")) {
            Uri uri = Uri.parse("androidamap://viewMap?sourceApplication=Vege&poiname=&lat=" + latitude + "&lon=" + longitude + "&dev=1");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请安装高德地图或百度地图", Toast.LENGTH_LONG).show();
        }
    }
}
