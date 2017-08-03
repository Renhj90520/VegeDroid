package com.xjconvenience.vege.vege.modules.share;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.VegeApplication;
import com.xjconvenience.vege.vege.adapters.SharePagerAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ren Haojie on 2017/7/24.
 */

public class ShareActivity extends AppCompatActivity implements ShareContract.IShareView {
    @BindView(R.id.vPager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject()
    ShareContract.ISharePresenter mSharePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("关注领券");
        DaggerShareComponent.builder()
                .shareModule(new ShareModule(this))
                .vegeServicesComponent(((VegeApplication) getApplication()).getVegeServicesComponent())
                .build()
                .Inject(this);
        mSharePresenter.loadCoupon();
    }

    @Override
    public void createPagers(String src) {
        SharePagerAdapter adapter = new SharePagerAdapter(getSupportFragmentManager());
        adapter.setSrc(src);
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
