package com.xjconvenience.vege.vege.modules.orderlist;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.xjconvenience.vege.vege.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
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
}
