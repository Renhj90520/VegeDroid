package com.xjconvenience.vege.vege.modules.share;

import com.xjconvenience.vege.vege.webservices.VegeServices;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

@Module()
public class ShareModule {

    private ShareContract.IShareView shareView;

    public ShareModule(ShareContract.IShareView shareView) {
        this.shareView = shareView;
    }

    @Provides
    ShareContract.IShareView provideShareView() {
        return this.shareView;
    }

    @Provides
    ShareContract.ISharePresenter provideSharePresenter(ShareContract.IShareView shareView, VegeServices services) {
        return new SharePresenter(shareView, services);
    }
}
