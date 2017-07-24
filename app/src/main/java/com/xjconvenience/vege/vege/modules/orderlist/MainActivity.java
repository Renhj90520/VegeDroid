package com.xjconvenience.vege.vege.modules.orderlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.xjconvenience.vege.vege.Constants;
import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.VegeApplication;
import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.modules.login.LoginActivity;
import com.xjconvenience.vege.vege.modules.orderdetail.OrderDetailActivity;
import com.xjconvenience.vege.vege.modules.share.ShareActivity;
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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("生鲜派送");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_search, null);
                EditText keyword = (EditText) view.findViewById(R.id.keyword);
                ((MainPresenter) mPresenter).setKeyword(keyword.getText().toString());
                Spinner state = (Spinner) view.findViewById(R.id.state);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.state_list, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                state.setAdapter(adapter);

                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((MainPresenter) mPresenter).setState(String.valueOf(i - 1));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                builder.setView(view);
                builder.setTitle("查询");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.refreshOrders();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return true;
            case R.id.menu_exit:

                SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.TOKEN_KEY, "");
                editor.commit();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_share:
                Intent shareIntent = new Intent(this, ShareActivity.class);
                startActivity(shareIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
