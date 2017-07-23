package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.PatchDoc;

import java.util.List;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public interface IOrderInteractor {

    interface OnLoadFinishListenter {
        void onLoadFinished(List<Order> orders);

        void onLoadError(String message);
    }

    void loadOrders(String index,
                    String perPage,
                    String keyword,
                    String begin,
                    String end,
                    String noshowRemove,
                    OnLoadFinishListenter listener);

    interface OnUpdateFinishListener {
        void onUpdateSuccess();

        void onUpdateError(String message);
    }

    void updateOrder(int id, List<PatchDoc> order, OnUpdateFinishListener listener);
}
