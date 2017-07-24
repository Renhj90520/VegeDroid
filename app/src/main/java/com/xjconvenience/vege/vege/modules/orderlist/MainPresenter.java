package com.xjconvenience.vege.vege.modules.orderlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.OrderItem;
import com.xjconvenience.vege.vege.models.PatchDoc;
import com.xjconvenience.vege.vege.models.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class MainPresenter implements MainContract.IMainPresenter, OrderListAdapter.OperatorListener {
    private MainContract.IMainView mMainView;
    private IOrderInteractor mOrderInteractor;
    private List<Order> mOrderList = new ArrayList<>();
    private OrderListAdapter mAdapter;
    private int index = 1;
    private int perPage = 10;

    private String keyword;
    private String state;

    @Inject
    public MainPresenter(MainContract.IMainView mainView, IOrderInteractor orderInteractor) {
        this.mMainView = mainView;
        this.mOrderInteractor = orderInteractor;
        mAdapter = new OrderListAdapter((MainActivity) mMainView, mOrderList);
        mAdapter.setOperatorListener(this);
    }

    @Override
    public void loadOrders(int lastVisible) {
        if (lastVisible >= mOrderList.size() - 1) {
            mMainView.showProgress();
            mOrderInteractor.loadOrders(String.valueOf(index), String.valueOf(perPage), getKeyword(), getState(), "", "", "", new IOrderInteractor.OnLoadFinishListenter() {
                @Override
                public void onLoadFinished(List<Order> orders) {
                    int currentSize = mOrderList.size();
                    orderCostCompute(orders);
                    mOrderList.addAll(orders);
                    mAdapter.notifyItemInserted(currentSize);
                    if (index == 1) {
                        mMainView.setItems(mAdapter);
                    }
                    index++;
                    mMainView.hideProgress();
                }

                @Override
                public void onLoadError(String message) {
                    mMainView.showMessage(message);
                }
            });
        }
    }

    @Override
    public void refreshOrders() {
        mMainView.showProgress();
        index = 1;
        mOrderInteractor.loadOrders(String.valueOf(index), String.valueOf(perPage), getKeyword(), getState(), "", "", "", new IOrderInteractor.OnLoadFinishListenter() {
            @Override
            public void onLoadFinished(List<Order> orders) {
                orderCostCompute(orders);

                mOrderList = orders;
                mAdapter = new OrderListAdapter((MainActivity) mMainView, mOrderList);
                mAdapter.notifyDataSetChanged();
                mMainView.setItems(mAdapter);
                mMainView.hideProgress();
            }

            @Override
            public void onLoadError(String message) {
                mMainView.showMessage(message);
            }
        });
    }

    private void orderCostCompute(List<Order> orders) {
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            List<OrderItem> products = order.getProducts();
            double sum = 0;
            for (int j = 0; j < products.size(); j++) {
                OrderItem product = products.get(j);
                double cost = product.getPrice() * product.getCount();
                product.setCost(cost);
                sum += cost;
            }
            order.setTotalCost(sum + order.getDeliveryCharge());
        }
    }

//    @Override
//    public void UpdateOrder(int id, List<PatchDoc> order) {
//        mOrderInteractor.updateOrder(id, order, new IOrderInteractor.OnUpdateFinishListener() {
//            @Override
//            public void onUpdateSuccess() {
//                mMainView.showMessage("操作成功");
//            }
//
//            @Override
//            public void onUpdateError(String message) {
//                mMainView.showMessage(message);
//            }
//        });
//    }

    @Override
    public void dialUser(int index) {
        Order order = mOrderList.get(index);
        mMainView.dialUser(order.getPhone());
    }

    @Override
    public void callMap(int index) {
        Order order = mOrderList.get(index);
        if (order.getLatitude() != 0 || order.getLongitude() != 0) {
            mMainView.callMap(order.getLatitude(), order.getLongitude());
        } else {
            mMainView.showMessage("该订单无用户定位信息");
        }
    }

    @Override
    public void sendOrder(final int index) {
        final Order order = mOrderList.get(index);
        List<PatchDoc> patchDocs = new ArrayList<>();
        PatchDoc patch = new PatchDoc();
        patch.setValue(3);
        patch.setPath("State");
        patchDocs.add(patch);
        mOrderInteractor.updateOrder(order.getId(), patchDocs, new IOrderInteractor.OnUpdateFinishListener() {
            @Override
            public void onUpdateSuccess() {
                mMainView.showMessage("派送成功");
                order.setState(3);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onUpdateError(String message) {
                mMainView.showMessage("派送失败");
            }
        });
    }

    @Override
    public void cancelOrder(final int index, final String reason) {
        final Order order = mOrderList.get(index);
        List<PatchDoc> patchDocs = new ArrayList<>();
        PatchDoc patch = new PatchDoc();
        patch.setValue(4);
        patch.setPath("State");
        patchDocs.add(patch);
        PatchDoc reasonPatch = new PatchDoc();
        reasonPatch.setPath("CancelReason");
        reasonPatch.setValue(reason);
        patchDocs.add(reasonPatch);
        PatchDoc cancelTime = new PatchDoc();
        cancelTime.setPath("CancelTime");
        cancelTime.setValue(new Date());
        patchDocs.add(cancelTime);
        mOrderInteractor.updateOrder(order.getId(), patchDocs, new IOrderInteractor.OnUpdateFinishListener() {
            @Override
            public void onUpdateSuccess() {
                mMainView.showMessage("取消成功");
                order.setState(4);
                order.setCancelReason(reason);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onUpdateError(String message) {
                mMainView.showMessage("取消失败");
            }
        });
    }

    @Override
    public void deleteOrder(final int index) {
        final Order order = mOrderList.get(index);
        List<PatchDoc> patchDocs = new ArrayList<>();
        PatchDoc patch = new PatchDoc();
        patch.setValue(7);
        patch.setPath("State");
        patchDocs.add(patch);
        mOrderInteractor.updateOrder(order.getId(), patchDocs, new IOrderInteractor.OnUpdateFinishListener() {
            @Override
            public void onUpdateSuccess() {
                mMainView.showMessage("删除成功");
                order.setState(7);
                mOrderList.remove(index);
                mAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onUpdateError(String message) {
                mMainView.showMessage("删除失败");
            }
        });
    }

    @Override
    public void refundOrder(int index, String note) {
        Order order = mOrderList.get(index);
        if (order != null) {
            //TODO
        }
    }

    @Override
    public void payOrder(final int index) {
        final Order order = mOrderList.get(index);
        if (order != null) {
            List<PatchDoc> patchDocs = new ArrayList<>();
            PatchDoc ispaid = new PatchDoc();
            ispaid.setPath("IsPaid");
            ispaid.setValue("1");
            patchDocs.add(ispaid);
            if (order.getState() == 0) {
                PatchDoc state = new PatchDoc();
                state.setPath("State");
                state.setValue(1);
                patchDocs.add(state);
            }
            mOrderInteractor.updateOrder(order.getId(), patchDocs, new IOrderInteractor.OnUpdateFinishListener() {
                @Override
                public void onUpdateSuccess() {
                    if (order.getState() == 0) {
                        order.setState(1);
                    }
                    order.setIsPaid("1");
                    mAdapter.notifyItemChanged(index);
                    mMainView.showMessage("现金支付成功");
                }

                @Override
                public void onUpdateError(String message) {
                    mMainView.showMessage("现金支付失败：" + message);
                }
            });
        }
    }

    @Override
    public void goDetail(int index) {
        Order order = mOrderList.get(index);
        mMainView.navigateToDetail(order);
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getState() {
        return "-1".equals(state) ? "" : state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
