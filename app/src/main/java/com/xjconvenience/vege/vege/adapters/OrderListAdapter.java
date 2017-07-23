package com.xjconvenience.vege.vege.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.models.Order;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ren Haojie on 2017/7/22.
 */

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {
    public interface OperatorListener {
        void dialUser(int index);

        void callMap(int index);

        void sendOrder(int index);

        void cancelOrder(int index, String reason);

        void deleteOrder(int index);

        void refundOrder(int index, String note);

        void goDetail(int index);
    }

    private OperatorListener mListener;

    public void setOperatorListener(OperatorListener listener) {
        mListener = listener;
    }

    private List<Order> mOrderList;
    private Context mContext;

    public OrderListAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = mOrderList.get(position);
        if (order != null) {
            holder.order_id.setText(String.valueOf(order.getId()));
            holder.order_state.setText(convertState(order.getState()));
            holder.create_time.setText(order.getCreateTime());
            holder.name.setText(order.getName());
            holder.phone.setText(order.getPhone());
            holder.address.setText(order.getStreet());
            String total_cost = "￥" + order.getTotalCost();
            if (order.getDeliveryCharge() != 0) {
                total_cost += "(含运费5元)";
            }
            holder.total_cost.setText(total_cost);
            if (order.getCancelReason() != null && !order.getCancelReason().isEmpty()) {
                holder.cancel_reason.setText(order.getCancelReason());
                holder.cancel_reason.setVisibility(View.VISIBLE);
            }
            if (order.getRefundNote() != null && !order.getRefundNote().isEmpty()) {
                holder.refund_note.setText(order.getRefundNote());
                holder.refund_note.setVisibility(View.VISIBLE);
            }
        }
    }

    private String convertState(int state) {
        switch (state) {
            case 0:
                return "未支付";
            case 1:
                return "已支付";
            case 2:
                return "已联系";
            case 3:
                return "派送中";
            case 4:
                return "已取消";
            case 5:
                return "交易完成";
            case 6:
                return "已退款";
            case 7:
                return "已删除";
            default:
                return "";
        }
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_id)
        TextView order_id;
        @BindView(R.id.order_state)
        TextView order_state;
        @BindView(R.id.create_time)
        TextView create_time;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.phone)
        TextView phone;
        @BindView(R.id.address)
        TextView address;
        @BindView(R.id.total_cost)
        TextView total_cost;
        @BindView(R.id.cancel_reason)
        TextView cancel_reason;
        @BindView(R.id.refund_note)
        TextView refund_note;

        @OnClick(R.id.ic_phone)
        public void dialUser() {
            mListener.dialUser(getAdapterPosition());
        }

        @OnClick(R.id.ic_location)
        public void callMap() {
            mListener.callMap(getAdapterPosition());
        }

        @OnClick(R.id.ic_send)
        public void sendOrder() {
            mListener.sendOrder(getAdapterPosition());
        }

        @OnClick(R.id.ic_cancel)
        public void cancelOrder() {

//            mListener.cancelOrder(getAdapterPosition());
        }

        @OnClick(R.id.ic_delete)
        public void deleteOrder() {
            mListener.deleteOrder(getAdapterPosition());
        }

        @OnClick(R.id.ic_refund)
        public void refundOrder() {

//            mListener.refundOrder(getAdapterPosition());
        }

        @OnClick(R.id.ic_detail)
        public void gotoDetail() {
            mListener.goDetail(getAdapterPosition());
        }

        public OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
