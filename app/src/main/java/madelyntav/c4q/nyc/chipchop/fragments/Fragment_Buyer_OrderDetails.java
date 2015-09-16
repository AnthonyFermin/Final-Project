package madelyntav.c4q.nyc.chipchop.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBCallback;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.adapters.CheckoutListAdapter;

/**
 * Created by alvin2 on 8/26/15.
 */
public class Fragment_Buyer_OrderDetails extends Fragment {

    private ArrayList<Item> foodItems;
    private RecyclerView foodList;

    public static final String TAG = "fragment_buyer_order_details";

    TextView totalPrice, sellerName, buyDateTime, deliveryMethod, sellerAddress;
    private BuyActivity activity;
    private DBHelper dbHelper;
    private DBCallback emptyCallback;

    private Order orderToView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_buyer_orderdetail, container, false);

        sellerName = (TextView) root.findViewById(R.id.seller_name_tv);
        buyDateTime = (TextView) root.findViewById(R.id.order_timestamp_tv);
        deliveryMethod = (TextView) root.findViewById(R.id.delivery_method_tv);
        sellerAddress = (TextView) root.findViewById(R.id.seller_address_tv);
        totalPrice = (TextView) root.findViewById(R.id.total_price_tv);
        activity = (BuyActivity) getActivity();
        dbHelper = DBHelper.getDbHelper(activity);
        activity.setCurrentFragment(TAG);

        emptyCallback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };


        orderToView = activity.getOrderToView();
        Log.d("Order to View", "Order ID: " + orderToView.getOrderID());
        Log.d("Order to View", "TimeStamp: " + orderToView.getTimeStamp());
        Log.d("Order to View", "Price: " + orderToView.getPrice());
        Log.d("Order to View", "Buyer ID: " + orderToView.getBuyerID());
        foodItems = dbHelper.getReceiptForSpecificOrderForBuyer(orderToView.getOrderID(), orderToView.getBuyerID(), emptyCallback);
        loadItems();

        foodList = (RecyclerView) root.findViewById(R.id.checkout_items_list);



//        // NOTIFICATION CODE
//        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(getActivity().getApplicationContext())
//                        .setSmallIcon(R.drawable.chipchop_small)
//                        .setContentTitle("ChipChop")
//                        .setContentText("Your order is ready!");
//
//        mBuilder.setAutoCancel(true);
//        notification = mBuilder.build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        sellerName.setText("SELLER: " + orderToView.getSellerName());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(orderToView.getTimeStamp());
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String formattedDate = formatter.format(cal.getTime());
        buyDateTime.setText("TIME: " + formattedDate);
        String deliverMethod = "";

        if (orderToView.isPickup()) {
            deliverMethod = "PICKUP";
        } else {
            deliverMethod = "DELIVER";
        }

        deliveryMethod.setText("DELIVERY METHOD: " + deliverMethod);
        sellerAddress.setText("SELLER ADDRESS: \n\n" + orderToView.getSellerAddress().replace("null","").replace(", ,",","));


        return root;

    }

    private void loadItems() {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {

                    int i = 0;
                    do {
                        Log.d("LOAD ORDER DETAILS", "Attempt #" + i);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i > 10) {
                            Log.d("LOAD ORDER DETAILS", "DIDN'T LOAD");
                            break;
                        }
                        i++;
                    } while (foodItems.size() == 0);

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    setAdapter();

                    int total = orderToView.getPrice();

                    totalPrice.setText("$ " + String.valueOf(total) + ".00");
                }
            }.execute();

            }

    private void setAdapter() {
        foodList.setLayoutManager(new LinearLayoutManager(activity));

        CheckoutListAdapter checkoutListAdapter = new CheckoutListAdapter(activity,foodItems);
        foodList.setAdapter(checkoutListAdapter);
    }

}
