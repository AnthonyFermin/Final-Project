package madelyntav.c4q.nyc.chipchop.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBCallback;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.DBObjects.User;
import madelyntav.c4q.nyc.chipchop.HelperMethods;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.adapters.FoodListAdapter;


public class Fragment_Buyer_SellerProfile extends Fragment {

    public static final String TAG = "fragment_buyer_seller_profile";

    ImageButton cartButton;
    private ArrayList<Item> foodItems;
    private RecyclerView foodList;

    RelativeLayout loadingPanel;
    LinearLayout containingView;

    View coordinatorLayoutView;
    CircleImageView storeImage;
    TextView storeName,storeDescription, cookingStatus;
    TextView deliveryTV, pickupTV;
    RatingBar ratingBar;

    private DBHelper dbHelper;
    private User user;
    private Seller seller;
    private BuyActivity activity;
    private DBCallback emptyCallback;
    private Order order;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_buyer_seller_profile, container, false);

        initializeData();
        bindViews(root);
        initializeViews();

        setListeners();

        loadingPanel.setVisibility(root.VISIBLE);
        load();

        return root;
    }

    private void initializeViews() {
        if(seller.getPhotoLink() != null && !seller.getPhotoLink().isEmpty() && seller.getPhotoLink().length() > 200) {
            final String imageLink = seller.getPhotoLink();
            new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... voids) {
                    byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(imageLink.getBytes());
                    BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    storeImage.setImageBitmap(bitmap);
                    storeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }.execute();
        }
        storeName.setText(seller.getStoreName());

        if(seller.isDeliveryAvailable()){
            deliveryTV.setVisibility(View.VISIBLE);
        }

        //only checks if pickup not available since pickup is available by default
        if(!seller.isPickUpAvailable()){
            pickupTV.setVisibility(View.GONE);
            deliveryTV.setVisibility(View.VISIBLE);
        }

        if(seller.getNumOfReviews() == 0){
            ratingBar.setVisibility(View.GONE);
        }else{
            ratingBar.setRating(seller.getNumOfTotalStars());
        }
    }

    private void load() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                int i = 0;
                do{
                    Log.d("Buyer - load seller", "Attempt #" + i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i > 10){
                        Log.d("Buyer - load seller", "DIDN'T LOAD");
                        break;
                    }
                    i++;
                }while(foodItems == null || foodItems.size() == 0);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(foodItems != null) {
                    setAdapter();
                }else{
                    Snackbar
                            .make(coordinatorLayoutView, "Seller food items not found", Snackbar.LENGTH_SHORT)
                            .show();
                    Toast.makeText(activity, "Seller food items not found", Toast.LENGTH_SHORT).show();
                }
                loadingPanel.setVisibility(View.GONE);
                containingView.setVisibility(View.VISIBLE);

            }
        }.execute();
    }

    private void setListeners() {
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BuyActivity activity = (BuyActivity) getActivity();
                activity.replaceFragment(new Fragment_Buyer_ViewCart());

            }
        });
    }

    private void setAdapter() {
        foodList.setLayoutManager(new LinearLayoutManager(getActivity()));
        FoodListAdapter foodListAdapter = new FoodListAdapter(getActivity(),foodItems);
        foodList.setAdapter(foodListAdapter);
    }

    private void bindViews(View root) {
        loadingPanel = (RelativeLayout) root.findViewById(R.id.loadingPanel);
        containingView = (LinearLayout) root.findViewById(R.id.container);
        containingView.setVisibility(View.INVISIBLE);

        cartButton = (ImageButton) root.findViewById(R.id.viewCartButton);
        final Animation myRotation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        cartButton.startAnimation(myRotation2);

        foodList = (RecyclerView) root.findViewById(R.id.seller_items_list);
        storeImage = (CircleImageView) root.findViewById(R.id.profile_image);

//

        cookingStatus =(TextView) root.findViewById(R.id.cooking_status);

        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        cookingStatus.startAnimation(shake);


        storeName = (TextView) root.findViewById(R.id.seller_name);

        storeDescription = (TextView) root.findViewById(R.id.store_description);
        coordinatorLayoutView = root.findViewById(R.id.snackbarPosition);
        ratingBar = (RatingBar) root.findViewById(R.id.rating_bar);

        deliveryTV = (TextView) root.findViewById(R.id.deliver_tv);
        pickupTV = (TextView) root.findViewById(R.id.pickup_tv);
    }

    private void initializeData() {
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

        user = HelperMethods.getUser();
        seller = activity.getSellerToView();
        foodItems = dbHelper.getSellersOnSaleItems(seller.getUID(), emptyCallback);

        order = activity.getCurrentOrder();
        order.setBuyerID(dbHelper.getUserID());
        order.setSellerID(seller.getUID());
        order.setIsActive(true);
        order.setStoreName(seller.getStoreName());
        order.setSellerName(seller.getName());
        order.setSellerAddress(seller.getAddressString());
        activity.setCurrentOrder(order);
    }

}
