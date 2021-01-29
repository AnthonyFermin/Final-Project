package madelyntav.c4q.nyc.chipchop.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.DeliveryDialogFragment;
import madelyntav.c4q.nyc.chipchop.HelperMethods;
import madelyntav.c4q.nyc.chipchop.PaymentDialogFragment;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.adapters.CheckoutListAdapter;


public class Fragment_Buyer_Checkout extends Fragment {

    ImageView confirmImage;
    Button confirmOrder;
    private ArrayList<Item> cartItems;
    private RecyclerView cartRView;
    private TextView totalPriceTV;
    private Seller seller;

    private BuyActivity activity;
    private Order order;
    private DBHelper dbHelper;
    private int total;

    public static final String TAG = "fragment_buyer_checkout";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_buyer_checkout, container, false);

        initializeData();
        bindViews(root);
        initializeViews();
        setListAdapter();
        setListeners();

        return root;

    }

    private void setListeners() {
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(  !(seller.isDeliveryAvailable() && seller.isPickUpAvailable())  ){
                    if(seller.isDeliveryAvailable()){
                        HelperMethods.getCurrentOrder().setToDeliver(true);
                        HelperMethods.getCurrentOrder().setIsPickup(false);
                    }else{
                        HelperMethods.getCurrentOrder().setIsPickup(true);
                        HelperMethods.getCurrentOrder().setToDeliver(false);
                    }
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    PaymentDialogFragment alertDialog = new PaymentDialogFragment();
                    alertDialog.show(fm, "fragment_alert");
                }else {

                    FragmentManager fm = activity.getSupportFragmentManager();
                    DeliveryDialogFragment alertDialog = new DeliveryDialogFragment();
                    alertDialog.show(fm, "fragment_alert");
                }

            }
        });
    }

    private void setListAdapter() {
        cartRView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CheckoutListAdapter checkoutListAdapter = new CheckoutListAdapter(getActivity(), cartItems);
        cartRView.setAdapter(checkoutListAdapter);

    }

    private void initializeViews() {
        totalPriceTV.setText("$ " + total + "");

    }

    private void bindViews(View root) {
        cartRView = (RecyclerView) root.findViewById(R.id.checkout_items_list);
        totalPriceTV = (TextView) root.findViewById(R.id.total_price_tv);
        confirmOrder = (Button) root.findViewById(R.id.confirmOrderButton);
        confirmImage = (ImageView) root.findViewById(R.id.confirm_image);
    }

    private void initializeData() {
        activity = (BuyActivity) getActivity();
        dbHelper = DBHelper.getDbHelper(activity);
        activity.setCurrentFragment(TAG);

        seller = HelperMethods.getSellerToView();

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

        for(Item item: cartItems){
            item.setBuyerID(dbHelper.getUserID());
            total = (item.getPrice() * item.getQuantityWanted());
        }
    }

}
