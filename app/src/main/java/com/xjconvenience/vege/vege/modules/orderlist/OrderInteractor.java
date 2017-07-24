package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.models.ItemsResult;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.PatchDoc;
import com.xjconvenience.vege.vege.models.Result;
import com.xjconvenience.vege.vege.webservices.VegeServices;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class OrderInteractor implements IOrderInteractor {
    private VegeServices mVegeServices;

    @Inject
    public OrderInteractor(VegeServices vegeServices) {
        mVegeServices = vegeServices;
    }

    @Override
    public void loadOrders(String index, String perPage, String keyword, String state, String begin, String end, String noshowRemove, final OnLoadFinishListenter listener) {
        Observable<Result<ItemsResult<Order>>> loadResp = mVegeServices.loadOrders(index, perPage, keyword, state, begin, end, noshowRemove);
        loadResp.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<ItemsResult<Order>>>() {
                    @Override
                    public void accept(@NonNull Result<ItemsResult<Order>> itemsResultResult) throws Exception {
                        if (itemsResultResult.getState() == 1) {
                            listener.onLoadFinished(itemsResultResult.getBody().getItems());
                        } else {
                            listener.onLoadError(itemsResultResult.getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        listener.onLoadError(throwable.getMessage());
                    }
                });

    }

    @Override
    public void updateOrder(int id, List<PatchDoc> order, final OnUpdateFinishListener listener) {
        Observable<Result<Boolean>> updateResp = mVegeServices.updateOrder(id, order);
        updateResp.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result<Boolean>>() {
                    @Override
                    public void accept(@NonNull Result<Boolean> booleanResult) throws Exception {
                        if (booleanResult.getState() == 1) {
                            listener.onUpdateSuccess();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        listener.onUpdateError(throwable.getMessage());
                    }
                });
    }
}
