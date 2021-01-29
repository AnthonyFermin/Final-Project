package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.SellActivity;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Seller_OrderDetails;

public class SellerOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Order> orderItems;
    private Context context;
    private int lastPosition = -1;

    public SellerOrdersAdapter(Context context, List<Order> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    private class SellerOrdersViewHolder extends RecyclerView.ViewHolder {

        CardView container;
        TextView nameOfBuyer;
        TextView total;
        TextView orderID;
        TextView deliveryMethod;
        TextView buyerAddress;
        TextView timeStamp;

        public SellerOrdersViewHolder(View itemView) {
            super(itemView);

            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            total = (TextView) itemView.findViewById(R.id.order_cost_tv);
            container = (CardView) itemView.findViewById(R.id.card_view);
            nameOfBuyer = (TextView) itemView.findViewById(R.id.buyer_name_tv);
            orderID = (TextView) itemView.findViewById(R.id.order_id_tv);
            deliveryMethod = (TextView) itemView.findViewById(R.id.delivery_method_tv);
            buyerAddress = (TextView) itemView.findViewById(R.id.buyer_address_tv);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellActivity activity = (SellActivity) context;
                    activity.setOrderToView(orderItems.get(getAdapterPosition()));
                    activity.replaceSellerFragment(new Fragment_Seller_OrderDetails());
                }
            });

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sellerorder_list_item, parent, false);
        return new SellerOrdersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        String deliveryMethod = "";
        Order order = orderItems.get(position);
        SellerOrdersViewHolder vh = (SellerOrdersViewHolder) viewHolder;
        vh.nameOfBuyer.setText("Buyer Id: " + order.getBuyerID());
        vh.orderID.setText("Order Id: " + order.getOrderID());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(order.getTimeStamp());
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String formattedDate = formatter.format(cal.getTime());
        vh.timeStamp.setText("TIME: " + formattedDate);
        vh.total.setText("TOTAL : $" + order.getPrice());

        if (order.isPickup()) {
            deliveryMethod = "PICKUP";
        } else {
            deliveryMethod = "DELIVER";
        }

        vh.deliveryMethod.setText("DELIVERY METHOD: " + deliveryMethod);
        if (order.isToDeliver()) {
            vh.buyerAddress.setText("BUYER ADDRESS: \n" +
                    "\n" + order.getBuyerAddress());
        } else {
            vh.buyerAddress.setVisibility(View.GONE);
        }

        setAnimation(vh.container, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // only animates the view if it was not already displayed on the screen
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom); //can make a custom animation here
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }
}