package com.xjconvenience.vege.vege.modules.share;

import com.xjconvenience.vege.vege.models.Coupon;
import com.xjconvenience.vege.vege.models.Result;
import com.xjconvenience.vege.vege.webservices.VegeServices;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ren Haojie on 2017/8/3.
 */

public class SharePresenter implements ShareContract.ISharePresenter {
    private VegeServices vegeService;
    private ShareContract.IShareView shareView;

    @Inject()
    public SharePresenter(ShareContract.IShareView shareView, VegeServices vegeService) {
        this.vegeService = vegeService;
        this.shareView = shareView;
    }

    @Override
    public void loadCoupon() {
        Observable<Result<Coupon>> getValidityCoupon = vegeService.getValidityCoupon();
        getValidityCoupon
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<Coupon>>() {
                    @Override
                    public void accept(@NonNull Result<Coupon> couponResult) throws Exception {
                        if (couponResult.getState() == 1) {
                            shareView.createPagers(couponResult.getBody().getQR_Path());
                        } else {
                            shareView.showMessage(couponResult.getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        shareView.showMessage(throwable.getMessage());
                    }
                });
    }
}
