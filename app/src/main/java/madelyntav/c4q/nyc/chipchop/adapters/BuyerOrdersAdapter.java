package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_OrderDetails;

public class BuyerOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Order> orderItems;
    private Context context;
    private int lastPosition = -1;

    public BuyerOrdersAdapter(Context context, List<Order> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    private class BuyerOrdersViewHolder extends RecyclerView.ViewHolder {

        CardView container;
        TextView timeStamp;
        TextView total;
        TextView nameOfSeller;
        TextView deliveryMethod;
        TextView sellerAddress;


        public BuyerOrdersViewHolder(View itemView) {
            super(itemView);

            total = (TextView) itemView.findViewById(R.id.order_cost_tv);
            container = (CardView) itemView.findViewById(R.id.card_view);
            nameOfSeller = (TextView) itemView.findViewById(R.id.seller_name_tv);
            timeStamp = (TextView) itemView.findViewById(R.id.order_timestamp_tv);
            deliveryMethod = (TextView) itemView.findViewById(R.id.delivery_method_tv);
            sellerAddress = (TextView) itemView.findViewById(R.id.seller_address_tv);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BuyActivity activity = (BuyActivity) context;
                    activity.setOrderToView(orderItems.get(getAdapterPosition()));
                    activity.replaceFragment(new Fragment_Buyer_OrderDetails());
                }
            });

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyerorder_list_item, parent, false);
        return new BuyerOrdersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        String deliveryMethod = "";
        Order order = orderItems.get(position);
        BuyerOrdersViewHolder vh = (BuyerOrdersViewHolder) viewHolder;
        vh.nameOfSeller.setText("SELLER NAME: " + order.getStoreName());

        Log.d("BuyerOrderAdapter", "Date: " + order.getTimeStamp());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(order.getTimeStamp());
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String formattedDate = formatter.format(cal.getTime());
        vh.timeStamp.setText("TIME: " + formattedDate);
        vh.total.setText("TOTAL: $" + order.getPrice());

        if (order.isPickup()) {
            deliveryMethod = "PICKUP";
        } else {
            deliveryMethod = "DELIVER";
        }

        vh.deliveryMethod.setText("DELIVERY METHOD: " + deliveryMethod);
        if (order.isPickup()) {
            vh.sellerAddress.setText("SELLER ADDRESS: \n\n" + order.getSellerAddress().replace("null","").replace(", ,",","));
        } else {
            vh.sellerAddress.setVisibility(View.GONE);
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