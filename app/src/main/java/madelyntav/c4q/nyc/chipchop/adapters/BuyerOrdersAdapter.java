package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

public class BuyerOrdersAdapter extends RecyclerView.Adapter<BuyerOrdersAdapter.BuyerOrdersViewHolder> {

    private List<Order> orderItems;
    private Context context;
    private int lastPosition = -1;

    public BuyerOrdersAdapter(Context context, List<Order> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    @Override
    public BuyerOrdersViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.buyerorder_list_item, parent, false);
        return new BuyerOrdersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BuyerOrdersViewHolder viewHolder, int position) {

        String deliveryMethod = "";
        Order order = orderItems.get(position);
        BuyerOrdersViewHolder vh = (BuyerOrdersViewHolder) viewHolder;
        String nameOfSellerString = "SELLER NAME: " + order.getStoreName();
        Spannable spannable = new SpannableString(nameOfSellerString);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#D51F27")), nameOfSellerString.indexOf(order.getStoreName()), nameOfSellerString.indexOf(order.getStoreName()) + order.getStoreName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        vh.nameOfSeller.setText(spannable);

        Log.d("BuyerOrderAdapter", "Date: " + order.getTimeStamp());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(order.getTimeStamp());
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy -- HH:mm a", Locale.US);
        String formattedDate = formatter.format(cal.getTime());
        String timeStampString = "TIME: " + formattedDate;
        Spannable spannableTime = new SpannableString(timeStampString);
        spannableTime.setSpan(new ForegroundColorSpan(Color.parseColor("#D51F27")), timeStampString.indexOf(formattedDate), timeStampString.indexOf(formattedDate) + formattedDate.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        vh.timeStamp.setText(spannableTime);
        String totalString = "TOTAL: $" + order.getPrice();
        Spannable spannableTotal = new SpannableString(totalString);
        spannableTotal.setSpan(new ForegroundColorSpan(Color.parseColor("#D51F27")), totalString.indexOf("$"), totalString.indexOf("$") + String.valueOf(order.getPrice()).length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        vh.total.setText(spannableTotal);

        if (order.isPickup()) {
            deliveryMethod = "PICKUP";
        } else {
            deliveryMethod = "DELIVER";
        }

        String deliveryString = "DELIVERY METHOD: " + deliveryMethod;
        Spannable spannableDelivery = new SpannableString(deliveryString);
        spannableDelivery.setSpan(new ForegroundColorSpan(Color.parseColor("#D51F27")), deliveryString.indexOf(deliveryMethod), deliveryString.indexOf(deliveryMethod) + deliveryMethod.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        vh.deliveryMethod.setText(spannableDelivery);
        if (order.isPickup()) {
            String addressString = "SELLER ADDRESS: " + order.getSellerAddress().replace("null","").replace(", ,",",");
            Spannable spannableAddress = new SpannableString(addressString);
            spannableAddress.setSpan(new ForegroundColorSpan(Color.parseColor("#D51F27")), addressString.indexOf(order.getSellerAddress().replace("null","").replace(", ,",",")), addressString.indexOf(order.getSellerAddress().replace("null","").replace(", ,",",")) + order.getSellerAddress().replace("null","").replace(", ,",",").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.sellerAddress.setText(spannableAddress);
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

    public class BuyerOrdersViewHolder extends RecyclerView.ViewHolder {

        private CardView container;
        private TextView timeStamp;
        private TextView total;
        private TextView nameOfSeller;
        private TextView deliveryMethod;
        private TextView sellerAddress;

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
}