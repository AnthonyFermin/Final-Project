package madelyntav.c4q.nyc.chipchop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.Payments.PaymentsActivity;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_Orders;

public class PaymentDialogFragment extends android.support.v4.app.DialogFragment {

    View coordinatorLayoutView;
    private ImageView confirmImage;
    private Button cardButton, cashButton;
    private BuyActivity activity;
    private Order order;
    private DBHelper dbHelper;
    private int total;
    private ArrayList<Item> cartItems;
    public static final String TAG = "fragment_buyer_checkout";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View root = inflater.inflate(R.layout.fragment_dialog_payment, container, false);

        initializeData();

        confirmImage = (ImageView) activity.findViewById(R.id.confirm_image);
        cardButton = (Button) root.findViewById(R.id.cardButton);
        cardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                long millis = date.getTime();
                order.setTimeStamp(millis);
                order.setPrice(total);
                Log.d("Order Info", "Seller ID: " + order.getSellerID());
                Log.d("Order Info", "Buyer ID: " + order.getBuyerID());
                Log.d("Order Info", "Total Price: $" + order.getPrice());
                Log.d("Order Info", "Store Name: " + order.getStoreName());
                Log.d("Order Info", "Time Bought: " + order.getTimeStamp());
                Intent paymentIntent = new Intent(activity.getApplicationContext(), PaymentsActivity.class);
                activity.startActivity(paymentIntent);

                //TODO: Check if Signed in, else go into signup activity - Sign in should just be a pop up dialog
            }
        });


        cashButton = (Button) root.findViewById(R.id.cashButton);
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                long millis = date.getTime();
                order.setTimeStamp(millis);
                order.setPrice(total);
                Log.d("Order Info", "Seller ID: " + order.getSellerID());
                Log.d("Order Info", "Buyer ID: " + order.getBuyerID());
                Log.d("Order Info","Total Price: $" + order.getPrice());
                Log.d("Order Info", "Store Name: " + order.getStoreName());
                Log.d("Order Info", "Time Bought: " + order.getTimeStamp());
                dbHelper.addCurrentOrderToSellerDB(order, new DBCallback() {
                    @Override
                    public void runOnSuccess() {
                        confirmImage.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                Intent intent = new Intent(activity,ServiceBuyerNotify.class)
//                                        .putExtra(ServiceBuyerNotify.BUYER_ID,dbHelper.getUserID())
//                                        .putExtra(ServiceBuyerNotify.SELLER_ID,order.getSellerID())
//                                        .putExtra(ServiceBuyerNotify.ORDER_ID,order.getOrderID());
//                                activity.startService(intent);
                                confirmImage.setVisibility(View.GONE);
                                activity.replaceFragment(new Fragment_Buyer_Orders());

                            }
                        }, 1000);
                        getDialog().dismiss();
                    }

                    @Override
                    public void runOnFail() {
                        Toast.makeText(activity, "Items are no longer available", Toast.LENGTH_SHORT).show();
                        Snackbar
                                .make(coordinatorLayoutView, "Items are no longer available", Snackbar.LENGTH_SHORT)
                                .show();
                        getDialog().dismiss();
                    }
                });
                //TODO: Check if Signed in, else go into signup activity - Sign in should just be a pop up dialog
            }
        });

        return root;
    }

    private void initializeData() {
        activity = (BuyActivity) getActivity();
        dbHelper = DBHelper.getDbHelper(activity);
        activity.setCurrentFragment(TAG);

        order = activity.getCurrentOrder();
        order.setBuyerID(dbHelper.getUserID());
        cartItems = order.getItemsOrdered();
        //debug
        for(Item item: cartItems){
            Log.d("Checkout Item","Buyer ID: " + item.getBuyerID());
            Log.d("Checkout Item","Seller ID: " + item.getSellerID());
            Log.d("Checkout Item","Item ID: " + item.getItemID());
            Log.d("Checkout Item","Item Name: " + item.getNameOfItem());
            Log.d("Checkout Item","Quantity Wanted: " + item.getQuantityWanted());
            Log.d("Checkout Item","          ------");
        }

        total = 1;
        for(Item item: cartItems){
            item.setBuyerID(dbHelper.getUserID());
            total = total + (item.getPrice() * item.getQuantityWanted());
        }
    }
}
