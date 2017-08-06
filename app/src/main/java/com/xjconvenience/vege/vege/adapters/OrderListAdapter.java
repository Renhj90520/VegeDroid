package com.xjconvenience.vege.vege.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

        void refundOrder(int index);

        void payOrder(int index);

        void completeOrder(int index);

        void goDetail(int index);

        void printOrder(int index);
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
            if ("1".equals(order.getIsPaid())) {
                holder.item_wrapper.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.paid_back));
            } else if ("2".equals(order.getIsPaid())) {
                holder.item_wrapper.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.refund_back));
            } else {
                holder.item_wrapper.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.unpaid_back));
            }
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
                holder.cancel_reason.setText(order.getCancelReason().trim());
                holder.reason_wrapper.setVisibility(View.VISIBLE);
            } else {
                holder.reason_wrapper.setVisibility(View.GONE);
            }
            if (order.getRefundNote() != null && !order.getRefundNote().isEmpty()) {
                holder.refund_note.setText(order.getRefundNote().trim());
                holder.note_wrapper.setVisibility(View.VISIBLE);
            } else {
                holder.note_wrapper.setVisibility(View.GONE);
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
        @BindView(R.id.item_wrapper)
        CardView item_wrapper;
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
        @BindView(R.id.reason_wrapper)
        LinearLayout reason_wrapper;
        @BindView(R.id.note_wrapper)
        LinearLayout note_wrapper;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_dialog, null);
            builder.setView(view);
            final EditText content = (EditText) view.findViewById(R.id.dialog_content);
            builder.setTitle("取消理由");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.cancelOrder(getAdapterPosition(), content.getText().toString());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.create().show();
        }

        @OnClick(R.id.ic_print)
        public void printOrder() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("确认");
            builder.setMessage("确认打印该订单吗");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.printOrder(getAdapterPosition());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }

        @OnClick(R.id.ic_complete)
        public void completeOrder() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("确认");
            builder.setMessage("该订单确认已完成交易？");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.completeOrder(getAdapterPosition());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }

        @OnClick(R.id.ic_delete)
        public void deleteOrder() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("确认删除该订单吗？");
            builder.setTitle("确认");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.deleteOrder(getAdapterPosition());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }

        @OnClick(R.id.ic_pay)
        public void payOrder() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("确认");
            builder.setMessage("确认收到该订单的现金支付吗");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.payOrder(getAdapterPosition());
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }

        @OnClick(R.id.ic_refund)
        public void refundOrder() {
            mListener.refundOrder(getAdapterPosition());
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
