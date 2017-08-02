package com.xjconvenience.vege.vege.modules.orderlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.OrderItem;
import com.xjconvenience.vege.vege.models.PatchDoc;
import com.xjconvenience.vege.vege.models.RefundWrapper;
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
            mOrderInteractor.loadOrders(String.valueOf(index), String.valueOf(perPage), getKeyword(), getState(), "", "", "true", new IOrderInteractor.OnLoadFinishListenter() {
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
        mOrderInteractor.loadOrders(String.valueOf(index), String.valueOf(perPage), getKeyword(), getState(), "", "", "true", new IOrderInteractor.OnLoadFinishListenter() {
            @Override
            public void onLoadFinished(List<Order> orders) {
                orderCostCompute(orders);

                mOrderList = orders;
                mAdapter = new OrderListAdapter((MainActivity) mMainView, mOrderList);
                mAdapter.notifyDataSetChanged();
                mMainView.setItems(mAdapter);
                mAdapter.setOperatorListener(MainPresenter.this);
                index++;
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
    public void refundOrder(final int index) {
        final Order order = mOrderList.get(index);
        if (order != null) {
            if ("1".equals(order.getIsPaid())) {
                Context context = (MainActivity) mMainView;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.fragment_dialog, null);
                builder.setView(view);
                final EditText content = (EditText) view.findViewById(R.id.dialog_content);
                content.setHint("请输入退款备注");

                TextView total_cost = (TextView) view.findViewById(R.id.refund_total_cost);
                String total = "订单金额：￥" + order.getTotalCost();
                if (order.getDeliveryCharge() != 0) {
                    total += "(含运费" + order.getDeliveryCharge() + "元)";
                }
                total_cost.setText(total);
                total_cost.setVisibility(View.VISIBLE);

                TextView refund_label = (TextView) view.findViewById(R.id.refund_label);
                refund_label.setVisibility(View.VISIBLE);

                final EditText refund_cost = (EditText) view.findViewById(R.id.refund_cost);
                refund_cost.setText(String.valueOf(order.getTotalCost()));
                refund_cost.setVisibility(View.VISIBLE);

                builder.setTitle("退款备注");

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        double refundCost = Double.parseDouble(refund_cost.getText().toString());
                        double totalCost = order.getTotalCost();
                        if (refundCost > totalCost) {
                            mMainView.showMessage("退款失败：退款金额大于订单金额");
                        } else {
                            final RefundWrapper wrapper = new RefundWrapper();
                            wrapper.setTotalCost((int) (totalCost * 100));
                            wrapper.setRefundCost((int) (refundCost * 100));
                            wrapper.setRefundNote(content.getText().toString());
                            mOrderInteractor.refundOrder(order.getId(), wrapper, new IOrderInteractor.OnUpdateFinishListener() {
                                @Override
                                public void onUpdateSuccess() {
                                    order.setIsPaid("2");
                                    mAdapter.notifyItemChanged(index);
                                    mMainView.showMessage("退款成功");
                                }

                                @Override
                                public void onUpdateError(String message) {
                                    mMainView.showMessage("退款失败：" + message);
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();
            } else {
                mMainView.showMessage("退款失败：该订单未支付或已退款");
            }
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
    public void completeOrder(final int index) {
        final Order order = mOrderList.get(index);
        if (order != null) {
            List<PatchDoc> patchDocs = new ArrayList<>();
            PatchDoc patchDoc = new PatchDoc();
            patchDoc.setPath("State");
            patchDoc.setValue(5);
            patchDocs.add(patchDoc);
            mOrderInteractor.updateOrder(order.getId(), patchDocs, new IOrderInteractor.OnUpdateFinishListener() {
                @Override
                public void onUpdateSuccess() {
                    order.setState(5);
                    mAdapter.notifyItemChanged(index);
                    mMainView.showMessage("交易已完成");
                }

                @Override
                public void onUpdateError(String message) {
                    mMainView.showMessage("交易完成失败：" + message);
                }
            });
        }
    }

    @Override
    public void goDetail(int index) {
        Order order = mOrderList.get(index);
        mMainView.navigateToDetail(order);
    }

    @Override
    public void printOrder(int index) {
        Order order = mOrderList.get(index);
        if (order != null) {
            mMainView.printOrder(order);
        } else {
            mMainView.showMessage("打印的订单不存在！");
        }
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
