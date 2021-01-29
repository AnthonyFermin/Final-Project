package madelyntav.c4q.nyc.chipchop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import madelyntav.c4q.nyc.chipchop.DBObjects.Address;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.DBObjects.User;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_Checkout;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_Map;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_OrderDetails;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_Orders;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_ProfileSettings;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_SellerProfile;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_ViewCart;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class BuyActivity extends AppCompatActivity {

    private RelativeLayout loadingPanel;
    private Button sellButton;
    private RatingBar ratingBar;
    private Button contactButton;
    private FrameLayout frameLayout;
    private LinearLayout DrawerLinear;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mListTitles;
    private Fragment fragment;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView drawerUserNameTV;
    public ArrayList<Order> orders;

    private DBHelper dbHelper;

    private User user = null;

    private SharedPreferences userInfoSP;

    private DBCallback emptyCallback;

    private String lastFragment;
    private String currentFragment;
    private Seller sellerToView = null;
    private Item itemToCart = null;
    private Order currentOrder;
    private Order orderToView;
    View coordinatorLayoutView;

    public static final String TO_SELL_ACTIVITY = "to_sell";
    public static final String SHOWCASE_ID = "SHOWCASE_ID_4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        FacebookSdk.sdkInitialize(this.getApplicationContext());



        bindViews();
        initializeData();
        setUpDrawer();
        checkIsLogged();
        selectFragmentToLoad(savedInstanceState);
    }

    private void checkIsLogged() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        loadingPanel.setVisibility(View.VISIBLE);

        userInfoSP = getSharedPreferences(Constants.USER_INFO_KEY, MODE_PRIVATE);
        boolean isLoggedIn = userInfoSP.getBoolean(Constants.IS_LOGGED_IN_KEY, false);
        if (isLoggedIn) {
            String email = userInfoSP.getString(Constants.EMAIL_KEY, null);
            String pass = userInfoSP.getString(Constants.PASSWORD_KEY, null);
            Log.d("{ASSWRRR",pass);

            if (email != null && pass != null) {
                Log.d("AUTO LOG-IN", "" + email);
                dbHelper.logInUser(email, pass, new DBCallback() {
                    @Override
                    public void runOnSuccess() {

                        load();
                        mDrawerToggle.setDrawerIndicatorEnabled(true);
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        ratingBar.setVisibility(View.VISIBLE);
                        loadingPanel.setVisibility(View.GONE);
                    }

                    @Override
                    public void runOnFail() {
                        // clears user login info if login authentication failed
                        clearLogin();


                    }
                });
            }
        } else {
            clearLogin();


        }
    }

    private void clearLogin() {
        dbHelper.setSellerCookingStatus(dbHelper.getUserID(),false);
        dbHelper.removeSellersFromActiveSellers(new Seller(dbHelper.getUserID()), emptyCallback);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        loadingPanel.setVisibility(View.GONE);
//        stopService(new Intent(this, ServiceBuyerNotify.class));
        stopService(new Intent(this, ServiceSellerNotify.class));
        userInfoSP.edit().clear().commit();
        mListTitles[3] = "Sign In";
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.navdrawer_list_item, mListTitles));
        dbHelper.signOutUser(emptyCallback);
        drawerUserNameTV.setText("");
        HelperMethods.setUser(null);
        ratingBar.setVisibility(View.GONE);
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isLoggedIn = userInfoSP.getBoolean(Constants.IS_LOGGED_IN_KEY, false);
        if (isLoggedIn) {

        }

        if (dbHelper.userIsLoggedIn()) {
            mListTitles[3] = "Sign Out";
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.navdrawer_list_item, mListTitles));
        }

    }

    private void selectFragmentToLoad(Bundle savedInstanceState) {

        //if items are still in cart go to checkout
        SharedPreferences sPref = getSharedPreferences(Fragment_Buyer_ViewCart.FROM_CART, MODE_PRIVATE);
        boolean fromCart = sPref.getBoolean(Fragment_Buyer_ViewCart.FROM_CART, false);
        Intent intent = getIntent();
        boolean toOrder = false;

        if (intent != null) {
            toOrder = getIntent().getBooleanExtra("To Orders View", false);
        }
        if (toOrder) {
            orderToView = HelperMethods.getCurrentOrder();
            replaceFragment(new Fragment_Buyer_OrderDetails());
        } else if (fromCart
                && getCurrentOrder() != null
                && getCurrentOrder().getItemsOrdered() != null
                && getCurrentOrder().getItemsOrdered().size() != 0) {
            sPref.edit().clear().commit();
            replaceFragment(new Fragment_Buyer_ViewCart());
        } else if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    private void setUpDrawer() {
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(Intent.ACTION_SEND);
                contactIntent.setType("text/html");
                contactIntent.putExtra(Intent.EXTRA_EMAIL, "chipchopcontact@gmail.com");
                startActivity(Intent.createChooser(contactIntent, "Create Email"));
            }
        });

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.navdrawer_list_item, mListTitles));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                new Toolbar(this), R.string.drawer_open,
                R.string.drawer_close) {

            final ImageView myImage = (ImageView)findViewById(R.id.profile_image);
            public void onDrawerClosed(View view) {
                myImage.setVisibility(View.INVISIBLE);
            }

            public void onDrawerOpened(View drawerView) {


                myImage.setVisibility(View.VISIBLE);
                final Animation myRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                myImage.startAnimation(myRotation);

                sellButton = (Button) findViewById(R.id.sellButton);

                Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                drawerUserNameTV.startAnimation(shake);


                new MaterialShowcaseView.Builder(BuyActivity.this)
                        .setTarget(sellButton)
                        .setMaskColour(Color.parseColor("#D51F27"))
                        .setFadeDuration(5)
                        .setDismissOnTouch(true)
                        .setDismissText("GOT IT")
                        .setContentText("Click here to begin selling home-cooked food!")
//                        .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                        .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                        .show();

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(drawerView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                sellButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (dbHelper.userIsLoggedIn()) {
                            if (!user.isHasSellerProfile()) {
                                user.setHasSellerProfile(true);
                            }
                            Intent sellIntent = new Intent(getApplicationContext(), SellActivity.class);
                            startActivity(sellIntent);
                        } else {
                            mDrawerLayout.closeDrawer(DrawerLinear);
                            Snackbar
                                    .make(coordinatorLayoutView, "Must be logged for this feature", Snackbar.LENGTH_SHORT)
                                    .show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent signUpIntent = new Intent(getApplicationContext(), SignUpFirstActivity.class);
                                    signUpIntent.putExtra(TO_SELL_ACTIVITY, true);
                                    startActivity(signUpIntent);
                                    finish();
                                }
                            }, 1000);

                        }
                    }
                });

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.navdrawer);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#D51F27"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

//        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.actionbar));
//        background.setGravity(Gravity.CENTER);
//        getSupportActionBar().setBackgroundDrawable(background);

    }

    private void bindViews() {
        drawerUserNameTV = (TextView) findViewById(R.id.drawer_user_nameTV);



        frameLayout = (FrameLayout) findViewById(R.id.sellerFrameLayout);
        DrawerLinear = (LinearLayout) findViewById(R.id.DrawerLinear);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        coordinatorLayoutView = findViewById(R.id.snackbarPosition);
        contactButton = (Button) findViewById(R.id.contact_button);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);



    }

    private void initializeData() {
        dbHelper = DBHelper.getDbHelper(this);

        mListTitles = getResources().getStringArray(R.array.BUYER_nav_drawer_titles);


        emptyCallback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };

    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments

        if (position == 0) {
            fragment = new Fragment_Buyer_Map();
        } else if (position == 1) {
            if (dbHelper.userIsLoggedIn())
                fragment = new Fragment_Buyer_Orders();
            else {
                clearLogin();
                Snackbar
                        .make(coordinatorLayoutView, "Must log in to view profile", Snackbar.LENGTH_SHORT)
                        .show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BuyActivity.this, SignUpFirstActivity.class));
                    }
                }, 1500);
            }
        } else if (position == 2) {
            if (dbHelper.userIsLoggedIn())
                fragment = new Fragment_Buyer_ProfileSettings();
            else {
                clearLogin();
                Snackbar
                        .make(coordinatorLayoutView, "Must log in to view profile", Snackbar.LENGTH_SHORT)
                        .show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(BuyActivity.this, SignUpFirstActivity.class));
                    }
                }, 1500);
            }
        } else if (position == 3) {
            //SIGN OUT/IN DRAWER ITEM
            boolean isLoggedIn = userInfoSP.getBoolean(Constants.IS_LOGGED_IN_KEY, false);
            if (isLoggedIn) {
                clearLogin();
                mDrawerLayout.closeDrawer(DrawerLinear);
                user = null;
                LoginManager.getInstance().logOut();
                Snackbar
                        .make(coordinatorLayoutView, "Sign out successful", Snackbar.LENGTH_SHORT)
                        .show();
                recreate();

                //if not currently in fragment_buyer_map, replace current fragment with buyer_map fragment
                if (!getCurrentFragment().equals(Fragment_Buyer_Map.TAG)) {
                    replaceFragment(new Fragment_Buyer_Map());
                    clearBackstack();
                }

            } else {
                Intent intent = new Intent(BuyActivity.this, SignUpFirstActivity.class);
                startActivity(intent);
                finish();  // killing the activity for now when switching to another activity
            }


        }



        // Create fragment manager to begin interacting with the fragments and the container
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.sellerFrameLayout, fragment).commit();


        // update selected item and title in nav drawer, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(DrawerLinear);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles

        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean drawerOpen = mDrawerLayout.isDrawerOpen(DrawerLinear);
        return drawerOpen;
    }

    @Override
    public void onBackPressed() {
        switch (getCurrentFragment()) {
            case (Fragment_Buyer_Checkout.TAG):
                replaceFragment(new Fragment_Buyer_ViewCart());
                break;
            case (Fragment_Buyer_Map.TAG):
                super.onBackPressed();
                break;
            case (Fragment_Buyer_Orders.TAG):
                replaceFragment(new Fragment_Buyer_Map());
                break;
            case (Fragment_Buyer_ProfileSettings.TAG):
                replaceFragment(new Fragment_Buyer_Map());
                break;
            case (Fragment_Buyer_SellerProfile.TAG):
                replaceFragment(new Fragment_Buyer_Map());
                break;
            case (Fragment_Buyer_ViewCart.TAG):
                if (getLastFragment() != null && getLastFragment().equals(Fragment_Buyer_ViewCart.TAG)) {
                    replaceFragment(new Fragment_Buyer_Map());
                } else {
                    replaceFragment(new Fragment_Buyer_SellerProfile());
                }
                break;
            case (Fragment_Buyer_OrderDetails.TAG):
                replaceFragment(new Fragment_Buyer_Orders());
                break;
            default:
                super.onBackPressed();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager BuyFragmentManager = getSupportFragmentManager();
        BuyFragmentManager.beginTransaction().replace(R.id.sellerFrameLayout, fragment).commit();

    }

    private void load() {

        String name = userInfoSP.getString(Constants.NAME_KEY, "");
        String email = userInfoSP.getString(Constants.EMAIL_KEY, "");
        String streetAddress = userInfoSP.getString(Constants.ADDRESS_KEY, "");
        String apt = userInfoSP.getString(Constants.APT_KEY, "");
        String city = userInfoSP.getString(Constants.CITY_KEY, "");
        String state = userInfoSP.getString(Constants.STATE_KEY, "");
        String zipcode = userInfoSP.getString(Constants.ZIPCODE_KEY, "");
        String phoneNumber = userInfoSP.getString(Constants.PHONE_NUMBER_KEY, "");
        String photoLink = userInfoSP.getString(Constants.PHOTO_LINK_KEY, "");
        Address address = new Address(streetAddress, apt, city, state, zipcode, dbHelper.getUserID());

        user = new User(dbHelper.getUserID(), email, name, address, photoLink, phoneNumber);
        HelperMethods.setUser(user);
        checkIfLastOrderHasBeenReviewedAndIfNotSetReview();

        drawerUserNameTV.setText(user.getName());

        mListTitles[3] = "Sign Out";
        mDrawerList.setAdapter(new ArrayAdapter<>(BuyActivity.this,
                R.layout.navdrawer_list_item, mListTitles));

    }


    // used for communication between fragments

    public String getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(String currentFragment) {
        setLastFragment(getCurrentFragment());
        this.currentFragment = currentFragment;
    }

    public Seller getSellerToView() {
        return HelperMethods.getSellerToView();
    }

    public void setSellerToView(Seller sellerToView) {
        HelperMethods.setSellerToView(sellerToView);
    }

    public Item getItemToCart() {
        return itemToCart;
    }

    public void setItemToCart(Item itemToCart) {
        this.itemToCart = itemToCart;
    }

    public Order getCurrentOrder() {
        return HelperMethods.getCurrentOrder();
    }

    public void setCurrentOrder(Order currentOrder) {
        HelperMethods.setCurrentOrder(currentOrder);
    }

    public Order getOrderToView() {
        return orderToView;
    }

    public void setOrderToView(Order orderToView) {
        this.orderToView = orderToView;
    }

    public String getLastFragment() {
        return lastFragment;
    }

    private void setLastFragment(String lastFragment) {
        this.lastFragment = lastFragment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void checkIfLastOrderHasBeenReviewedAndIfNotSetReview(){

        orders=new ArrayList<>();
        dbHelper.getAllPreviouslyBoughtOrders(user.getUID(), new DBCallback() {
            @Override
            public void runOnSuccess() {
                orders.addAll(dbHelper.getAllPreviouslyBoughtOrders(dbHelper.getUserID(), emptyCallback));

                Collections.sort(orders, new Comparator<Order>() {
                    @Override
                    public int compare(Order order, Order t1) {
                        if (order.getTimeStamp() > t1.getTimeStamp()) {
                            return -1;
                        } else if (order.getTimeStamp() < t1.getTimeStamp()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                if(orders.size()>0) {
                    Order order = orders.get(0);
                    if (!order.isReviewed()) {
                        HelperMethods.setOrderToReview(order);
                        FragmentManager fm = getSupportFragmentManager();
                        ReviewDialogFragment alertDialog = new ReviewDialogFragment();
                        //try catch needed for when this is called right when switching to another activity
                        try {
                            alertDialog.show(fm, "fragment_alert");
                        }catch(IllegalStateException e){
                            Log.d("FAILED REVIEW","WRONG ACTIVITY");
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void runOnFail() {

            }
        });

    }

    public void clearBackstack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                    0);
            getSupportFragmentManager().popBackStack(entry.getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().executePendingTransactions();
        }
    }
}
