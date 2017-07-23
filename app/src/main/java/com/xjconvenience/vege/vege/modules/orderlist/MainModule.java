package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.webservices.VegeServices;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

@Module
public class MainModule {
    private final MainContract.IMainView mIMainView;

    public MainModule(MainContract.IMainView mainView) {
        this.mIMainView = mainView;
    }

    @Provides
    MainContract.IMainView provideMainView() {
        return mIMainView;
    }

    @Provides
    IOrderInteractor provideOrderInteractor(VegeServices vegeServices) {
        return new OrderInteractor(vegeServices);
    }

    @Provides
    MainContract.IMainPresenter provideMainPresenter(MainContract.IMainView mainView, IOrderInteractor orderInteractor) {
        return new MainPresenter(mainView, orderInteractor);
    }
}
