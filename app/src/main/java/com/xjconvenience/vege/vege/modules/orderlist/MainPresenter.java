package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.models.PatchDoc;

import java.util.List;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class MainPresenter implements MainContract.IMainPresenter {
    private MainContract.IMainView mMainView;
    private IOrderInteractor mOrderInteractor;

    public MainPresenter(MainContract.IMainView mainView, IOrderInteractor orderInteractor) {
        this.mMainView = mainView;
        this.mOrderInteractor = orderInteractor;
    }

    @Override
    public void loadOrders(int lastVisible) {

    }

    @Override
    public void refreshOrders() {

    }

    @Override
    public void UpdateOrder(int id, List<PatchDoc> order) {

    }
}
