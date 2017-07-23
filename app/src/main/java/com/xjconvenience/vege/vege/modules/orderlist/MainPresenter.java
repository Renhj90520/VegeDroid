package com.xjconvenience.vege.vege.modules.orderlist;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.PatchDoc;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
            mOrderInteractor.loadOrders(String.valueOf(index), String.valueOf(perPage), "", "", "", "", new IOrderInteractor.OnLoadFinishListenter() {
                @Override
                public void onLoadFinished(List<Order> orders) {
                    int currentSize = mOrderList.size();
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
        mOrderInteractor.loadOrders(String.valueOf(index), String.valueOf(perPage), "", "", "", "", new IOrderInteractor.OnLoadFinishListenter() {
            @Override
            public void onLoadFinished(List<Order> orders) {
                mOrderList = orders;
                mAdapter = new OrderListAdapter((MainActivity) mMainView, mOrderList);
                mAdapter.notifyDataSetChanged();
                mMainView.hideProgress();
            }

            @Override
            public void onLoadError(String message) {
                mMainView.showMessage(message);
            }
        });
    }

    @Override
    public void UpdateOrder(int id, List<PatchDoc> order) {
        mOrderInteractor.updateOrder(id, order, new IOrderInteractor.OnUpdateFinishListener() {
            @Override
            public void onUpdateSuccess() {
                mMainView.showMessage("操作成功");
            }

            @Override
            public void onUpdateError(String message) {
                mMainView.showMessage(message);
            }
        });
    }

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
    public void sendOrder(int index) {
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
            }

            @Override
            public void onUpdateError(String message) {
                mMainView.showMessage("派送失败");
            }
        });
    }

    @Override
    public void cancelOrder(int index, final String reason) {
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
        mOrderInteractor.updateOrder(order.getId(), patchDocs, new IOrderInteractor.OnUpdateFinishListener() {
            @Override
            public void onUpdateSuccess() {
                mMainView.showMessage("取消成功");
                order.setState(4);
                order.setCancelReason(reason);
            }

            @Override
            public void onUpdateError(String message) {
                mMainView.showMessage("取消失败");
            }
        });
    }

    @Override
    public void deleteOrder(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) mMainView);
        builder.setMessage("确认删除该订单吗？");
        builder.setTitle("确认");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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
                    }

                    @Override
                    public void onUpdateError(String message) {
                        mMainView.showMessage("删除失败");
                    }
                });
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public void refundOrder(int index, String note) {

    }

    @Override
    public void goDetail(int index) {
        Order order = mOrderList.get(index);
        mMainView.navigateToDetail(order);
    }
}
