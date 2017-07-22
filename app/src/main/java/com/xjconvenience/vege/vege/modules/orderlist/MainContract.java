package com.xjconvenience.vege.vege.modules.orderlist;

import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.PatchDoc;

import java.util.List;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public interface MainContract {
    public interface IMainView {
        void showProgress();

        void hideProgress();

        void setItems(OrderListAdapter adapter);

        void navigateToDetail(Order order);

        void showMessage(String message);
    }

    public interface IMainPresenter {
        void loadOrders(int lastVisible);

        void refreshOrders();

        void UpdateOrder(int id, List<PatchDoc> order);
    }
}
