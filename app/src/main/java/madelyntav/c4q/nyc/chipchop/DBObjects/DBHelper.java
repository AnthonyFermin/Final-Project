package madelyntav.c4q.nyc.chipchop.DBObjects;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import madelyntav.c4q.nyc.chipchop.Constants;
import madelyntav.c4q.nyc.chipchop.DBCallback;
import madelyntav.c4q.nyc.chipchop.SignUpFirstActivity;
import madelyntav.c4q.nyc.chipchop.SignUpSecondActivity;

public class DBHelper extends Firebase {
    static DBHelper fireBaseRef;
    private static final String URL = "https://chipchop.firebaseio.com/";
    public static Context mContext;
    public static Item item;
    public static Item item2;
    String UID;
    public static ArrayList<User> allUsers;
    public static ArrayList<Item> items;
    private static String userID = "buyerID";
    private static final String sStreet = "streetAddress";
    private static final String sApartment = "apartment";
    private static final String sCity = "city";
    private static final String sState = "state";
    private static final String sZipCode = "zipCode";
    private static final String nameOfItem = "nameOfItem";
    private static final String descriptionOfItem = "descriptionOfItem";
    private static final String quantity = "quantity";
    private static String itemID = "itemID";
    private static final String imageLink = "imageLink";
    private static final String sName = "name";
    private static final String sEmailAddress = "eMail";
    private static final String sPhoneNumber = "phoneNumber";
    private static final String sAddress = "addressString";
    private static final String sPhotoLink = "photoLink";
    private static final String sUID = "UID";
    private static final String sCardNumber = "cardNumber";
    private static final String sCardExpirationMonth = "cardExpirationMonth";
    private static final String sCardExpirationYear = "cardExpirationYear";
    private static final String sCardCVC = "cardCVC";
    private static final String sIsCooking = "isCooking";
    public static User user;
    public static final String sLatitude = "latitude";
    public static final String sLongitude = "longitude";
    public boolean mSuccess = false;
    public static Address address;
    long sizeofAddDBList;
    public static ArrayList<LatLng> latLngList;
    public static ArrayList<Address> addressList;
    public static ArrayList<User> userList;
    public static ArrayList<Item> arrayListOfSellerItems;
    public static Order returnOrder;
    public ArrayList<Item> listOfItemsSellingForSeller;
    public String sellerId;
    public String orderID;
    public static DBCallback callback;
    public static Seller seller;
    public static ArrayList<Seller> allSellersInDB;
    public static ArrayList<Review> reviewArrayList;
    public static ArrayList<Seller> allActiveSellers;
    public static ArrayList<Review> sellersReviewArrayList;
    public static ArrayList<Order> previouslyBought;
    public static ArrayList<Order> previouslySold;
    public static ArrayList<Item> receiptForSpecificOrder;
    public static ArrayList<Item> itemsInSpecificOrder;
    public static ArrayList<Item> inactiveItems;


    public DBHelper() {
        super(URL);
    }

    public Firebase getFirebaseRef() {
        return fireBaseRef;
    }

    public static DBHelper getDbHelper(Context context) {
        mContext = context;
        if (fireBaseRef == null) {
            Firebase.setAndroidContext(context.getApplicationContext());
            fireBaseRef = new DBHelper();
        }
        userList = new ArrayList<>();
        items = new ArrayList<>();
        sellersReviewArrayList = new ArrayList<>();
        previouslyBought = new ArrayList<>();
        previouslySold = new ArrayList<>();
        allUsers = new ArrayList<>();
        user = new User();
        latLngList = new ArrayList<>();
        addressList = new ArrayList<>();
        inactiveItems = new ArrayList<>();
        userList = new ArrayList<>();
        arrayListOfSellerItems = new ArrayList<>();
        returnOrder = new Order();
        arrayListOfSellerItems = new ArrayList<>();
        reviewArrayList = new ArrayList<>();
        allActiveSellers = new ArrayList<>();
        seller = new Seller();
        item = new Item();
        item2 = new Item();
        allSellersInDB = new ArrayList<>();
        receiptForSpecificOrder = new ArrayList<>();
        itemsInSpecificOrder = new ArrayList<>();
        callback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };
        return fireBaseRef;
    }

    //checks if user is logged in
    public boolean userIsLoggedIn() {

        if (UID == null) {

            return false;
        } else {
            return true;
        }
    }

    public String getUserID() {
        return UID;
    }

    public String getSellerID() {
        return sellerId;
    }

    public Seller getCurrentSeller() {
        return seller;
    }

    public User getCurrentUser() {
        return user;
    }

    public boolean createUser(final String email, final String password) {
        UID = "";

        fireBaseRef.createUser(email, password, new ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                Toast.makeText(mContext, "Account Created", Toast.LENGTH_SHORT).show();
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Must log in to view profile", Snackbar.LENGTH_SHORT)
                        .show();
                mSuccess = true;

                UID = String.valueOf(stringObjectMap.get("uid"));

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("UID", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", UID);
                editor.putString("eMail", email);
                editor.putString("password", password);
                editor.apply();

                user = new User(UID, email);

                Firebase fRef = new Firebase(URL + "UserProfiles");
                fRef.child(UID);
                fRef.child(UID).child(sEmailAddress).setValue(email);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Account Creation Failed", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Account Creation Failed", Toast.LENGTH_SHORT).show();
                Log.d("Firebase", firebaseError.toString());
                mSuccess = false;
            }
        });
        return mSuccess;
    }

    public Boolean createUserAndLaunchIntent(final String email, final String password, final Intent intent) {
        UID = "";

        fireBaseRef.createUser(email, password, new ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Account Created", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Account Created", Toast.LENGTH_SHORT).show();
                mSuccess = true;

                UID = String.valueOf(stringObjectMap.get("uid"));

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("New User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", UID);
                editor.putString("eMail", email);
                editor.putString("password", password);
                editor.apply();

                user = new User(UID, email);

                Firebase fRef = new Firebase(URL + "UserProfiles");
                fRef.child(UID);
                fRef.child(UID).child(sEmailAddress).setValue(email);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mContext.startActivity(intent);
                    }
                }, 2000);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d("Firebase", firebaseError.toString());
                mSuccess = false;
            }
        });

        return mSuccess;
    }

    public Boolean createUserAndCallback(final String email, final String password, final DBCallback callback) {
        UID = "";
        fireBaseRef.createUser(email, password, new ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Account Created", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Account Created", Toast.LENGTH_SHORT).show();
                mSuccess = true;

                UID = String.valueOf(stringObjectMap.get("uid"));

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("New User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", UID);
                editor.putString("eMail", email);
                editor.putString("password", password);
                editor.apply();

                user = new User(UID, email);

                Firebase fRef = new Firebase(URL + "UserProfiles");
                fRef.child(UID);
                fRef.child(UID).child(sEmailAddress).setValue(email);

                callback.runOnSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Please set Email and Password", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Please set Email and Password", Toast.LENGTH_SHORT).show();
                Log.d("Firebase", firebaseError.toString());
                mSuccess = false;
                callback.runOnFail();
            }
        });

        return mSuccess;
    }

    //logsInUser without launching an intent
    public boolean logInUser(String email, String password) {
        UID = "";
        mSuccess = false;
        fireBaseRef.authWithPassword(email, password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) { /* ... */
                        mSuccess = true;
                        UID = authData.getUid();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        switch (error.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                Snackbar
                                        .make(SignUpFirstActivity.coordinatorLayoutView, "E-mail or Password Invalid", Snackbar.LENGTH_SHORT)
                                        .show();
                                Toast.makeText(mContext, "E-mail or Password Invalid", Toast.LENGTH_LONG).show();
                                mSuccess = false;
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                Snackbar
                                        .make(SignUpFirstActivity.coordinatorLayoutView, "Invalid Password", Snackbar.LENGTH_SHORT)
                                        .show();
                                Toast.makeText(mContext, "Invalid Password", Toast.LENGTH_LONG).show();
                                mSuccess = false;
                                break;
                            default:
                                // handle other errors
                                break;
                        }
                    }
                });
        return mSuccess;
    }

    //logs in user and launches an intent
    public boolean logInUser(String email, String password, final Intent intent) {
        UID = "";
        mSuccess = false;
        fireBaseRef.authWithPassword(email, password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) { /* ... */
                        mSuccess = true;
                        UID = authData.getUid();


                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        switch (error.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                Snackbar
                                        .make(SignUpFirstActivity.coordinatorLayoutView, "E-mail or Password Invalid", Snackbar.LENGTH_SHORT)
                                        .show();
                                Toast.makeText(mContext, "E-mail or Password Invalid", Toast.LENGTH_LONG).show();
                                mSuccess = false;
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                Snackbar
                                        .make(SignUpFirstActivity.coordinatorLayoutView, "Invalid Password", Snackbar.LENGTH_SHORT)
                                        .show();
                                Toast.makeText(mContext, "Invalid Password", Toast.LENGTH_LONG).show();
                                mSuccess = false;
                                break;
                            default:
                                // handle other errors
                                break;
                        }
                    }
                });
        return mSuccess;
    }

    public boolean logInUser(String email, String password, final DBCallback callback) {
        UID = "";
        mSuccess = false;
        fireBaseRef.authWithPassword(email, password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) { /* ... */
                        mSuccess = true;
                        UID = authData.getUid();


                        callback.runOnSuccess();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        switch (error.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
//                                Snackbar
//                                        .make(SignUpFirstActivity.coordinatorLayoutView, "E-mail or Password Invalid", Snackbar.LENGTH_SHORT)
//                                        .show();
                                Toast.makeText(mContext, "E-mail or Password Invalid", Toast.LENGTH_LONG).show();
                                mSuccess = false;
                                break;
                            case FirebaseError.INVALID_PASSWORD:

                                Toast.makeText(mContext, "Invalid Password", Toast.LENGTH_LONG).show();
                                mSuccess = false;
                                break;
                            default:
                                // handle other errors
                                break;
                        }
                        callback.runOnFail();
                    }
                });
        return mSuccess;
    }

    public Boolean changeUserEmail(String oldEmail, String newEmail, String password, ResultHandler resultHandler) {

        fireBaseRef.changeUserEmail(oldEmail, newEmail, password,
                new ResultHandler() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {

                    }
                });
        return mSuccess;
    }

    public void onFacebookAccessTokenChange(final AccessToken token, final DBCallback dbCallback) {
        Firebase ref = new Firebase(URL);
        Log.d("Tok In DB preAuth", "Token");

        Log.d("Token Is:", token.toString());

        if (token != null) {

            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // The Facebook user is now authenticated with your Firebase app
                    Log.d("Tok In DB after Auth", "Token");
                    //Create user Profile with user email
                    String token2= token.getToken();
                    String password ="";
                    for (int i=0;i<7;i++){
                        password+=token2.charAt(i);
                    }
                    Log.d("passwordt",password);
                    createUserFromFBAuthLogin(String.valueOf(authData.getProviderData().get("email")), password, dbCallback);

                    Log.d("email", authData.getProviderData().get("email").toString());
                    UID = "";
                    UID = authData.getUid();

                    Log.d("UID", UID + "");

                    Firebase fRef = new Firebase(URL + "UserProfiles/");
                    fRef.child(UID);
                    fRef.child(UID).child(sEmailAddress).setValue(authData.getProviderData().get("email"));
                    fRef.child(UID).child(imageLink).setValue(authData.getProviderData().get("profileImageURL"));
                    fRef.child(UID).child(sName).setValue(authData.getProviderData().get("displayName"));

//                    Intent intent = new Intent(mContext, SignUpSecondActivity.class);
//                    intent.putExtra("email", String.valueOf(authData.getProviderData().get("email")));
//                    intent.putExtra("name", String.valueOf(authData.getProviderData().get("displayName")));
//                    intent.putExtra("UID", UID);
//                    mContext.startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                }
            });
        } else {
        /* Logged out of Facebook so do a logout from the Firebase app */
            ref.unauth();
        }
    }

    private void createUserFromFBAuthLogin(final String email, final String password, final DBCallback callback) {
        Log.d("emailz",email);
        Log.d("passwordz",password);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", UID);
        editor.putString("eMail", email);
        editor.putString("password", password);
        editor.apply();

        fireBaseRef.createUser(email, password, new ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                //mSuccess = true;

                user = new User(UID, email);

                Firebase fRef = new Firebase(URL + "UserProfiles/");
                fRef.child(UID);
                fRef.child(UID).child(sEmailAddress).setValue(email);
                Log.d("b4CB", "B4CB");

                callback.runOnSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                loginUserThroughFacebookAuth(email, password, callback);

            }
        });
    }

    private void loginUserThroughFacebookAuth(String email, String password, final DBCallback dbCallback) {
        Log.d("createUserLoginFailed", "lk");
        fireBaseRef.authWithPassword(email, password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) { /* ... */
                        mSuccess = true;
                        //UID = authData.getUid();
                        dbCallback.runOnSuccess();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        switch (error.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                Snackbar
                                        .make(SignUpFirstActivity.coordinatorLayoutView, "E-mail or Password Invalid", Snackbar.LENGTH_SHORT)
                                        .show();
                                Toast.makeText(mContext, "E-mail or Password Invalid", Toast.LENGTH_LONG).show();
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                break;
                            default:
                                // handle other errors
                                break;
                        }
                    }
                });
    }

    public void onGmailAccessTokenChange(final String token) {
        Firebase ref = new Firebase(URL);
        Log.d("Tok In DB preAuth", "Token");

        if (token != null) {
            ref.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // The Facebook user is now authenticated with your Firebase app
                    Log.d("Tok In DB after Auth", "Token");
                    //Create user Profile with user email
                    Log.d("HEREFORG,", "HERE");
                    createUserFromGmailAuthLogin(String.valueOf(authData.getProviderData().get("email")), String.valueOf(authData.getProviderData().get("displayName")), AccessToken.getCurrentAccessToken().getToken());
                    Log.d("email", authData.getProviderData().get("email").toString());
                    UID = "";
                    UID = authData.getUid();

                    Log.d("UID", UID + "");
                    Firebase fRef = new Firebase(URL + "UserProfiles/");
                    fRef.child(UID);
                    fRef.child(UID).child(sEmailAddress).setValue(authData.getProviderData().get("email"));
                    fRef.child(UID).child(imageLink).setValue(authData.getProviderData().get("profileImageURL"));
                    fRef.child(UID).child(sName).setValue(authData.getProviderData().get("displayName"));
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                    Log.d("err", firebaseError.toString());
                }
            });
        } else {
        /* Logged out of google so do a logout from the Firebase app */
            ref.unauth();
        }
    }


    private void createUserFromGmailAuthLogin(final String email, final String name, final String password) {
        fireBaseRef.createUser(email, password, new ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {

                mSuccess = true;

                SharedPreferences sharedPreferences = mContext.getSharedPreferences("New User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id", UID);
                editor.putString("eMail", email);
                editor.putString("password", password);
                editor.apply();
                Log.d("HEREFOrIntent,", "HERE");


                Intent intent = new Intent(mContext, SignUpSecondActivity.class);
                intent.putExtra("email", String.valueOf(email));
                intent.putExtra("name", String.valueOf(name));
                intent.putExtra("UID", UID);
                mContext.startActivity(intent);

                user = new User(UID, email);

                Firebase fRef = new Firebase(URL + "UserProfiles");
                fRef.child(UID);
                fRef.child(UID).child(sEmailAddress).setValue(email);

                callback.runOnSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                loginUserThroughGmailAuth(email, password);

            }
        });
    }

    private void loginUserThroughGmailAuth(String email, String password) {
        fireBaseRef.authWithPassword(email, password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) { /* ... */
                        mSuccess = true;
                        UID = authData.getUid();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        switch (error.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                Snackbar
                                        .make(SignUpFirstActivity.coordinatorLayoutView, "E-mail or Password Invalid", Snackbar.LENGTH_SHORT)
                                        .show();
                                Toast.makeText(mContext, "E-mail or Password Invalid", Toast.LENGTH_LONG).show();
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                break;
                            default:
                                // handle other errors
                                break;
                        }
                    }
                });
    }

    public void addUserProfileInfoToDB(User user) {
        Firebase fRef = new Firebase(URL + "UserProfiles/");

        UID = user.getUID();

        fRef.child(UID);
        fRef.child(UID).child(sName).setValue(user.getName());
        fRef.child(UID).child(sEmailAddress).setValue(user.geteMail());
        fRef.child(UID).child(sPhoneNumber).setValue(user.getPhoneNumber());
        fRef.child(UID).child(sPhotoLink).setValue(user.getPhotoLink());
        fRef.child(UID).child(sAddress).setValue(user.getAddress().toString());
        fRef.child(UID).child("hasSellerProfile").setValue(user.isHasSellerProfile());

        if (user.isHasSellerProfile()) {
            seller = new Seller();
            seller.setUID(user.getUID());
            seller.setNumOfReviews(seller.numOfReviews);
            seller.setName(user.getName());
            seller.seteMail(user.geteMail());
            seller.setPhoneNumber(user.getPhoneNumber());
            seller.setAddress(user.getAddress());
            addSellerProfileInfoToDB(seller);
        }
    }

    public void addUserProfileInfoToDBAndLaunchIntent(User user, final DBCallback callback) {
        Firebase fRef = new Firebase(URL + "UserProfiles/");
        UID = user.getUID();

        fRef.child(UID).push();
        fRef.child(UID).child(sName).setValue(user.getName());
        fRef.child(UID).child(sEmailAddress).setValue(user.geteMail());
        fRef.child(UID).child(sCardNumber).setValue(user.getCardNumber());
        fRef.child(UID).child(sCardExpirationMonth).setValue(user.getCardExpirationMonth());
        fRef.child(UID).child(sCardExpirationYear).setValue(user.getCardExpirationYear());
        fRef.child(UID).child(sCardCVC).setValue(user.getCardCVC());
        fRef.child(UID).child(sPhoneNumber).setValue(user.getPhoneNumber());
        fRef.child(UID).child(sPhotoLink).setValue(user.getPhotoLink());
        fRef.child(UID).child(sAddress).setValue(user.getAddress().toString());

        callback.runOnSuccess();
    }

    public void changeUserEmail(String oldeMail, String neweMail, String passWord, final DBCallback dbCallback) {
        Firebase ref = new Firebase(URL);
        ref.changeEmail(oldeMail, neweMail, passWord, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "E-mail Successfully Changed", Toast.LENGTH_SHORT).show();
                dbCallback.runOnSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(mContext, "Error Changing E-mail, Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeUserPassword(String email, String oldPassword, String newPassowrd, final DBCallback dbCallback) {
        Firebase ref = new Firebase(URL);
        ref.changePassword(email, oldPassword, newPassowrd, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "Password Successfully Changed", Toast.LENGTH_SHORT).show();
                dbCallback.runOnSuccess();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(mContext, "Error Changing Password, Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void addSellerProfileInfoToDB(Seller seller) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/");
        sellerId = seller.getUID();

        fRef.child(sellerId).child("UID").setValue(UID);
        fRef.child(sellerId).child(sName).setValue(seller.getName());
        fRef.child(sellerId).child("storeName").setValue(seller.getStoreName());
        fRef.child(sellerId).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(sellerId).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(sellerId).child("pickUpAvailable").setValue(seller.isPickUpAvailable());
        fRef.child(sellerId).child("deliveryAvailable").setValue(seller.isDeliveryAvailable());
        fRef.child(sellerId).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(sellerId).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(sellerId).child(sEmailAddress).setValue(seller.geteMail());
        fRef.child(sellerId).child(sPhoneNumber).setValue(seller.getPhoneNumber());
        fRef.child(sellerId).child(sPhotoLink).setValue(seller.getPhotoLink());
        fRef.child(sellerId).child("routingNumber").setValue(seller.getRoutingNumber());
        fRef.child(sellerId).child("accountNumber").setValue(seller.getAccountNumber());
        fRef.child(sellerId).child(sAddress).setValue(seller.getAddress().toString());
        fRef.child(sellerId).child(sLatitude).setValue(seller.getAddress().getLatitude());
        fRef.child(sellerId).child(sLongitude).setValue(seller.getAddress().getLongitude());
    }

    public void setSellerCookingStatus(boolean isCooking) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/");

        fRef.child(UID).child("isCooking").setValue(isCooking);
    }

    public void setSellerCookingStatus(String userID, boolean isCooking) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + userID);

        fRef.child("isCooking").setValue(isCooking);
    }

    public void addSellerProfileInfoToDB(Seller seller, final DBCallback callback) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/");
        UID = seller.getUID();

        fRef.child(UID).push();
        fRef.child(UID).child(sName).setValue(seller.getName());
        fRef.child(UID).child("storeName").setValue(seller.getStoreName());
        fRef.child(UID).child(sEmailAddress).setValue(seller.geteMail());
        fRef.child(UID).child(sPhoneNumber).setValue(seller.getPhoneNumber());
        fRef.child(UID).child(sPhotoLink).setValue(seller.getPhotoLink());
        fRef.child(UID).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(UID).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(UID).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(UID).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(UID).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(UID).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(UID).child(sAddress).setValue(seller.getAddress().toString());
        fRef.child(UID).child(sLatitude).setValue(seller.getAddress().getLatitude());
        fRef.child(UID).child(sLongitude).setValue(seller.getAddress().getLongitude());
        fRef.child(UID).child("routingNumber").setValue(seller.getRoutingNumber());
        fRef.child(UID).child("accountNumber").setValue(seller.getAccountNumber());

        callback.runOnSuccess();
    }

    public Seller getSellerFromDB(final String sellerID, final DBCallback callback) {
        sellerId = sellerID;
        seller = new Seller();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId);
        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                Seller seller1 = dataSnapshot.getValue(Seller.class);
                if (seller1 == null) {
                    callback.runOnFail();
                } else {
                    seller.setName(seller1.name);
                    seller.setStoreName(seller1.storeName);
                    seller.setAddress(seller1.address);
                    seller.setNumOfReviews(seller1.numOfReviews);
                    seller.setNumOfTotalStars(seller1.numOfTotalStars);
                    seller.setDescription(seller1.description);
                    seller.setPickUpAvailable(seller1.pickUpAvailable);
                    seller.setDeliveryAvailable(seller1.deliveryAvailable);
                    seller.seteMail(seller1.eMail);
                    seller.setUID(dataSnapshot.getKey());
                    seller.setPhoneNumber(seller1.phoneNumber);
                    seller.setItems(seller1.items);
                    seller.setLongitude(seller1.longitude);
                    seller.setLatitude(seller1.latitude);
                    seller.setIsCooking(seller1.isCooking);
                    seller.setAccountNumber(seller1.getAccountNumber());
                    seller.setRoutingNumber(seller1.routingNumber);
                    seller.setPhotoLink(seller1.getPhotoLink());

                    Log.d("Seller", seller.name + "");
                    callback.runOnSuccess();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mContext, "Error: Please Try Again", Toast.LENGTH_SHORT).show();
                seller = null;
            }
        });
        return seller;
    }

    public User getUserFromDB(final String userID) {
        this.UID = userID;
        Firebase fRef = new Firebase(URL + "UserProfiles/" + userID);

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                User user1 = dataSnapshot.getValue(User.class);

                user.setName(user1.name);
                user.setAddressString(user1.addressString);
                user.seteMail(user1.eMail);
                user.setUID(user1.UID);
                user.setCardNumber(user1.cardNumber);
                user.setCardExpirationYear(user1.cardExpirationYear);
                user.setCardExpirationMonth(user1.cardExpirationMonth);
                user.setCardCVC(user1.cardCVC);
                user.setPhoneNumber(user1.phoneNumber);
                user.setUserItems(user1.userItems);
                user.setLongitude(user1.longitude);
                user.setLatitude(user1.latitude);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
                user = null;
            }
        });

        return user;
    }

    public User getUserFromDBForFBAuth(final String userID, final DBCallback dbCallback) {
        this.UID = userID;
        Firebase fRef = new Firebase(URL + "UserProfiles/" + userID);
        Log.d("frefz32", fRef.toString());


        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                User user1 = dataSnapshot.getValue(User.class);

                user.setName(user1.name);
                user.setAddressString(user1.addressString);
                user.seteMail(user1.eMail);
                user.setUID(dataSnapshot.getKey());
                user.setCardNumber(user1.cardNumber);
                user.setCardExpirationYear(user1.cardExpirationYear);
                user.setCardExpirationMonth(user1.cardExpirationMonth);
                user.setCardCVC(user1.cardCVC);
                user.setPhoneNumber(user1.phoneNumber);
                user.setUserItems(user1.userItems);
                user.setLongitude(user1.longitude);
                user.setLatitude(user1.latitude);
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dbCallback.runOnFail();
                user = null;
            }
        });

        return user;
    }

    public void addActiveSellerToTable(Seller seller) {
        Firebase fRef = new Firebase(URL + "ActiveSellers/");
        sellerId = seller.getUID();
        Log.d("ADD TO ACTIVE", sellerId + "");

        fRef.child(sellerId).child("UID").setValue(sellerId);
        fRef.child(sellerId).child(sIsCooking).setValue(seller.getIsCooking());
        fRef.child(sellerId).child("storeName").setValue(seller.getStoreName());
        fRef.child(sellerId).child(sName).setValue(seller.getName());
        fRef.child(sellerId).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(sellerId).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(sellerId).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(sellerId).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(sellerId).child("pickUpAvailable").setValue(seller.isPickUpAvailable());
        fRef.child(sellerId).child("deliveryAvailable").setValue(seller.isDeliveryAvailable());
        fRef.child(sellerId).child(sEmailAddress).setValue(seller.geteMail());
        fRef.child(sellerId).child(sPhoneNumber).setValue(seller.getPhoneNumber());
        fRef.child(sellerId).child(sPhotoLink).setValue(seller.getPhotoLink());
        fRef.child(sellerId).child(sAddress).setValue(seller.getAddressString());
        fRef.child(sellerId).child(sLatitude).setValue(seller.getLatitude());
        fRef.child(sellerId).child(sLongitude).setValue(seller.getLatitude());
        fRef.child(sellerId).child("routingNumber").setValue(seller.getRoutingNumber());
        fRef.child(sellerId).child("accountNumber").setValue(seller.getAccountNumber());
    }

    public ArrayList<Seller> getAllActiveSellers(DBCallback dbCallback) {

        Firebase fRef = new Firebase(URL + "ActiveSellers/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ACTIVE SELLER SIZE", dataSnapshot.getChildrenCount() + "");

                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Seller seller1 = dataSnapshot1.getValue(Seller.class);

                    Seller seller = new Seller();

                    seller.setName(seller1.name);
                    seller.setUID(dataSnapshot1.getKey());
                    seller.setIsCooking(seller1.isCooking);
                    seller.setPickUpAvailable(seller1.pickUpAvailable);
                    seller.setDeliveryAvailable(seller1.deliveryAvailable);
                    seller.setLatitude(seller1.latitude);
                    seller.setLongitude(seller1.longitude);
                    seller.setPhoneNumber(seller1.phoneNumber);
                    seller.setAddressString(seller1.addressString);
                    seller.seteMail(seller1.eMail);
                    seller.setNumOfTotalStars(seller.numOfTotalStars);
                    seller.setDescription(seller1.description);
                    seller.setPhotoLink(seller1.photoLink);
                    seller.setStoreName(seller1.storeName);
                    seller.setAccountNumber(seller1.accountNumber);
                    seller.setRoutingNumber(seller1.routingNumber);
                    seller.setNumOfReviews(seller.numOfReviews);


                    if (allActiveSellers.size() < sizeofAddDBList) {

                        allActiveSellers.add(seller);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
                user = null;
            }
        });

        if (allActiveSellers.size() == sizeofAddDBList) {
            updateAllActiveSellers(dbCallback);
        }

        return allActiveSellers;
    }

    //Returns list of all users
    public ArrayList<Seller> updateAllActiveSellers(DBCallback dbCallback) {

        if (allActiveSellers.size() < sizeofAddDBList) {
            getAllActiveSellers(dbCallback);
        }
        return allActiveSellers;
    }


    public Seller getSpecificActiveSeller(final String sellerID, final DBCallback callback) {
        Firebase fRef = new Firebase(URL + "ActiveSellers/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Seller seller1 = dataSnapshot1.getValue(Seller.class);

                    if (dataSnapshot1.getKey().equals(sellerID)) {
                        seller.setName(seller1.name);
                        seller.setIsCooking(seller1.isCooking);
                        seller.setAddress(seller1.address);
                        seller.setDescription(seller1.description);
                        seller.seteMail(seller1.eMail);
                        seller.setUID(seller1.UID);
                        seller.setNumOfTotalStars(seller1.numOfTotalStars);
                        seller.setStoreName(seller1.storeName);
                        seller.setPhoneNumber(seller1.phoneNumber);
                        seller.setItems(seller1.items);
                        seller.setLongitude(seller1.latitude);
                        seller.setRoutingNumber(seller1.routingNumber);
                        seller.setAccountNumber(seller1.accountNumber);
                        seller.setLatitude(seller1.longitude);
                        seller.setNumOfReviews(seller1.numOfReviews);

                    }
                    Log.d("SellerOne", seller.name + "");
                }
                callback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Seller not found", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Error: Seller not found", Toast.LENGTH_SHORT).show();
                seller = null;
                callback.runOnFail();
            }
        });

        return seller;
    }

    public Seller getSpecificSeller(final String sellerID, final DBCallback callback) {
        Firebase fRef = new Firebase(URL + "ActiveSellers/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Seller seller1 = dataSnapshot1.getValue(Seller.class);

                    if (dataSnapshot1.getKey().equals(sellerID)) {
                        seller.setName(seller1.name);
                        seller.setIsCooking(seller1.isCooking);
                        seller.setAddressString(seller1.addressString);
                        seller.setDescription(seller1.description);
                        seller.seteMail(seller1.eMail);
                        seller.setNumOfTotalStars(seller1.numOfTotalStars);
                        seller.setUID(seller1.UID);
                        seller.setCardCVC(seller1.cardCVC);
                        seller.setCardExpirationMonth(seller1.cardExpirationMonth);
                        seller.setCardExpirationYear(seller1.cardExpirationYear);
                        seller.setCardNumber(seller1.cardNumber);
                        seller.setStoreName(seller1.storeName);
                        seller.setPhoneNumber(seller1.phoneNumber);
                        seller.setItems(seller1.items);
                        seller.setLongitude(seller1.latitude);
                        seller.setLatitude(seller1.longitude);
                        seller.setRoutingNumber(seller1.routingNumber);
                        seller.setAccountNumber(seller1.accountNumber);
                        seller.setNumOfReviews(seller1.numOfReviews);

                    }
                    Log.d("SellerOne", seller.name + "");
                }
                callback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Seller not found", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Error: Seller not found", Toast.LENGTH_SHORT).show();
                seller = null;
                callback.runOnFail();
            }
        });

        return seller;
    }

    //Method which adds a seller to the ActiveSellers table and updates their items for sale,
    //checks if quantity is greater than 0 for each item
    public void sendSellerToActiveSellerTable(Seller seller) {
        this.sellerId = seller.getUID();
        Firebase fRef = new Firebase(URL + "ActiveSellers/");
        //Seller sellerToSend = getSpecificActiveSeller(sellerId, callback);

        fRef.child(sellerId).child(sUID).setValue(sellerId);
        fRef.child(sellerId).child(sName).setValue(seller.getName());
        fRef.child(sellerId).child(descriptionOfItem).setValue(seller.getDescription());
        fRef.child(sellerId).child("numOfTotalStars").setValue(seller.getNumOfTotalStars());
        fRef.child(sellerId).child("numOfReviews").setValue(seller.getNumOfReviews());
        fRef.child(sellerId).child("storeName").setValue(seller.getStoreName());
        fRef.child(sellerId).child("routingNumber").setValue(seller.getRoutingNumber());
        fRef.child(sellerId).child("accountNumber").setValue(seller.getAccountNumber());
        fRef.child(sellerId).child("pickUpAvailable").setValue(seller.isPickUpAvailable());
        fRef.child(sellerId).child("deliveryAvailable").setValue(seller.isDeliveryAvailable());
        fRef.child(sellerId).child(sIsCooking).setValue(seller.getIsCooking());
        fRef.child(sellerId).child("addressString").setValue(seller.getAddressString());
        fRef.child(sellerId).child(sEmailAddress).setValue(seller.geteMail());
        fRef.child(sellerId).child(sPhoneNumber).setValue(seller.getPhoneNumber());
        fRef.child(sellerId).child(sPhotoLink).setValue(seller.getPhotoLink());
        fRef.child(sellerId).child(sLatitude).setValue(seller.getLatitude());
        fRef.child(sellerId).child(sLongitude).setValue(seller.getLongitude());

        ArrayList<Item> itemsSellerHas = seller.getItems();

        if (itemsSellerHas != null) {

            for (Item item : itemsSellerHas) {
                if (item.quantity > 0) {

                    Firebase fRef1 = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/" + item.getItemID());

                    fRef1.child("itemID").setValue(item.getItemID());
                    fRef1.child("nameOfItem").setValue(item.getNameOfItem());
                    fRef1.child("descriptionOfItem").setValue(item.getDescriptionOfItem());
                    fRef1.child(quantity).setValue(item.getQuantity());
                    fRef1.child("price").setValue(item.getPrice());
                    fRef1.child("sellerID").setValue(item.getSellerID());
                    fRef1.child("imageLink").setValue(item.getImageLink());
                    fRef1.child("containsPeanuts").setValue(item.isContainsPeanuts());
                    fRef1.child("glutenFree").setValue(item.isGlutenFree());
                    fRef1.child("isVegetarian").setValue(item.isVegetarian());
                    fRef1.child("containsEggs").setValue(item.isContainsEggs());
                    fRef1.child("containsShellfish").setValue(item.isContainsShellfish());
                    fRef1.child("containsDairy").setValue(item.isContainsDairy());
                }
            }
        }
    }

    //Method that adds one item to the sellers onSale Items
    public void addItemToActiveSellerTable(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");
        Firebase fire1 = fRef.child("itemsForSale").push();

        String itemID = fire1.getKey();

        item.setItemID(itemID);

        fRef.child(itemID);
        fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(itemID).child(quantity).setValue(item.getQuantity());
        fRef.child(itemID).child("price").setValue(item.getPrice());
        fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
        fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        fRef.child(itemID).child("sellerID").setValue(item.getSellerID());
        dbCallback.runOnSuccess();

    }

    public void updateItemInSellerTable(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();
        String itemID = item.getItemID();

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");
        fRef.child(itemID).push();
        fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(itemID).child(quantity).setValue(item.getQuantity());
        fRef.child(itemID).child("price").setValue(item.getPrice());
        fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
        fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        fRef.child(itemID).child("sellerID").setValue(item.getSellerID());
        dbCallback.runOnSuccess();

        Log.d("ItemID", itemID);
    }


    public void addItemToSellerProfileDB(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/itemsForSale/");
        Firebase fire1 = fRef.child("itemsForSale").push();

        String itemID = fire1.getKey();
        //item.setItemID(itemID);

        fRef.child(itemID);
        fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(itemID).child(quantity).setValue(item.getQuantity());
        fRef.child(itemID).child("price").setValue(item.getPrice());
        fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
        fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        fRef.child(itemID).child("sellerID").setValue(item.getSellerID());
        dbCallback.runOnSuccess();
        Log.d("ItemID", itemID);
    }

    public void addInactiveItemToSellerProfileDB(Item item) {
        sellerId = item.getSellerID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/inactiveItems/");
        Firebase fire1 = fRef.child("itemsForSale").push();

        String itemID = fire1.getKey();
        //item.setItemID(itemID);
        fRef.child(itemID);
        fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(itemID).child(quantity).setValue(item.getQuantity());
        fRef.child(itemID).child("price").setValue(item.getPrice());
        fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
        fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        fRef.child(itemID).child("sellerID").setValue(item.getSellerID());
        Log.d("ItemID", itemID);
    }

    public ArrayList<Item> getInactiveItems(String sellerId, final DBCallback dbCallback) {
        this.sellerId = sellerId;
        final Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/inactiveItems/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item = dataSnapshot1.getValue(Item.class);

                    item.setItemID(dataSnapshot1.getKey());
                    item.setSellerID(item.sellerID);
                    item.setBuyerID(item.buyerID);
                    item.setItemID(dataSnapshot1.getKey());
                    item.setNameOfItem(item.nameOfItem);
                    item.setPrice(item.price);
                    item.setSellerPhoneNumber(item.sellerPhoneNumber);
                    item.setBuyerPhoneNumber(item.buyerPhoneNumber);
                    item.setQuantity(item.quantity);
                    item.setIsVegetarian(item.isVegetarian);
                    item.setImageLink(item.imageLink);
                    item.setContainsDairy(item.containsDairy);
                    item.setContainsEggs(item.containsEggs);
                    item.setContainsPeanuts(item.containsPeanuts);
                    item.setContainsShellfish(item.containsShellfish);
                    item.setGlutenFree(item.glutenFree);

                    if (inactiveItems.size() < sizeofAddDBList) {
                        inactiveItems.add(item);
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "No Inactive Items Available", Snackbar.LENGTH_SHORT)
                        .show();
                dbCallback.runOnFail();
            }
        });

        return inactiveItems;
    }

    //Method that sends a list of items to the sellersProfile... if num>0 display on listView
    public ArrayList<Item> sendArrayListOfItemsToItemsForSale(ArrayList<Item> itemsforSale, String sellerID) {
        sellerId = sellerID;

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");

        for (Item item1 : itemsforSale) {

            Firebase fire1 = fRef.child("itemsForSale").push();
            String itemID = fire1.getKey();
            Item item2 = new Item();
            item2.setItemID(itemID);
            item2.setNameOfItem(item1.getNameOfItem());
            item2.setIsVegetarian(item1.isVegetarian());
            item2.setImageLink(item1.imageLink);
            item2.setPrice(item1.price);
            item2.setSellerID(item1.sellerID);
            item2.setBuyerID(item1.buyerID);
            item2.setDescriptionOfItem(item1.descriptionOfItem);
            item2.setContainsPeanuts(item1.containsPeanuts);
            item2.setPrice(item1.price);
            item2.setQuantity(item2.quantity);
            item2.setGlutenFree(item2.glutenFree);
            items.add(item2);

            fRef.child(itemID);
            fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(itemID).child(quantity).setValue(item.getQuantity());
            fRef.child(itemID).child("sellerID").setValue(item.getSellerID());
            fRef.child(itemID).child("price").setValue(item.getPrice());
            fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        }

        return items;
    }

    public void removeSellersFromActiveSellers(Seller seller1, final DBCallback dbCallback) {
        final Firebase fRef = new Firebase(URL + "ActiveSellers");
        final String sellerID = seller1.getUID();
        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Seller seller1 = dataSnapshot1.getValue(Seller.class);

                    if (dataSnapshot1.getKey().equals(sellerID)) {
                        Log.d("SellerOne", seller.name + "");

                        fRef.child(dataSnapshot1.getKey()).removeValue();
                    }

                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Seller not found", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Error: Seller not found", Toast.LENGTH_SHORT).show();
                seller = null;
                dbCallback.runOnFail();
            }
        });

    }

    public Item removeItemFromSale(final Item item, final DBCallback dbCallback) {
        sellerId = item.getSellerID();
        final String itemid1 = item.getItemID();

        final Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Item item1 = dataSnapshot1.getValue(Item.class);

                    if (dataSnapshot1.getKey().equals(itemid1)) {
                        item.setItemID(dataSnapshot1.getKey());
                        item.setContainsPeanuts(item1.containsPeanuts);
                        item.setSellerID(item1.sellerID);
                        item.setDescriptionOfItem(item1.descriptionOfItem);
                        item.setGlutenFree(item1.glutenFree);
                        item.setPrice(item.price);
                        item.setImageLink(item1.imageLink);
                        item.setNameOfItem(item1.nameOfItem);
                        item.setIsVegetarian(item1.isVegetarian);
                        item.setQuantity(item1.quantity);

                        if (item.getBuyerID() != null) {
                            moveItemToPreviouslyBoughtItems(item, dbCallback);
                        }
                        addItemToSellerProfileDB(item, callback);

                        fRef.child(dataSnapshot1.getKey()).removeValue();
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Seller not found", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Error: Seller not found", Toast.LENGTH_SHORT).show();
                seller = null;
                dbCallback.runOnFail();
            }
        });
        return item;
    }

    public void removeItemFromSellerProfile(final Item item, final DBCallback dbCallback) {

        sellerId = item.getSellerID();
        final String itemid1 = item.getItemID();

        final Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/itemsForSale/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Item item1 = dataSnapshot1.getValue(Item.class);

                    if (dataSnapshot1.getKey().equals(itemid1)) {
                        item.setItemID(dataSnapshot1.getKey());
                        item.setQuantity(item1.quantity);
                        item.setSellerID(item1.sellerID);
                        item.setItemID(dataSnapshot1.getKey());
                        item.setNameOfItem(item1.nameOfItem);
                        item.setPrice(item1.price);
                        item.setQuantity(item1.quantity);
                        item.setIsVegetarian(item1.isVegetarian);
                        item.setImageLink(item1.imageLink);
                        item.setContainsDairy(item1.containsDairy);
                        item.setContainsEggs(item1.containsEggs);
                        item.setContainsPeanuts(item1.containsPeanuts);
                        item.setContainsShellfish(item1.containsShellfish);
                        item.setGlutenFree(item1.glutenFree);

                        if (item.getBuyerID() != null) {
                            moveItemToPreviouslyBoughtItems(item, dbCallback);
                        }
                        fRef.child(dataSnapshot1.getKey()).removeValue();
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Seller not found", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Error: Seller not found", Toast.LENGTH_SHORT).show();
                seller = null;
                dbCallback.runOnFail();
            }
        });
    }

    public Item getItemFromSellerProfile(final Item item12, final DBCallback dbCallback) {
        final String itemid1 = item12.getItemID();

        final Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/itemsForSale/");
        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Item item1 = dataSnapshot1.getValue(Item.class);

                    if (dataSnapshot1.getKey().equals(itemid1)) {
                        item.setItemID(dataSnapshot1.getKey());
                        item.setContainsPeanuts(item1.containsPeanuts);
                        item.setPrice(item1.price);
                        item.setSellerID(item.sellerID);
                        item.setDescriptionOfItem(item1.descriptionOfItem);
                        item.setGlutenFree(item1.glutenFree);
                        item.setImageLink(item1.imageLink);
                        item.setNameOfItem(item1.nameOfItem);
                        item.setIsVegetarian(item1.isVegetarian);
                        item.setQuantity(item1.quantity);
                        fRef.child(dataSnapshot1.getKey()).removeValue();
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Seller not found", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Error: Seller not found", Toast.LENGTH_SHORT).show();
                seller = null;
                dbCallback.runOnFail();
            }
        });
        return item;

    }

    public void addItemToActiveSellerProfile(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();
        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");

        Firebase fire = fRef.push();
        String itemID = fire.getKey();
        item.setItemID(itemID);

        fire.child("itemID").setValue(item.getItemID());
        fire.child("nameOfItem").setValue(item.getNameOfItem());
        fire.child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fire.child(quantity).setValue(item.getQuantity());
        fire.child("price").setValue(item.getPrice());
        fire.child("imageLink").setValue(item.getImageLink());
        fire.child("containsPeanuts").setValue(item.isContainsPeanuts());
        fire.child("glutenFree").setValue(item.isGlutenFree());
        fire.child("isVegetarian").setValue(item.isVegetarian());
        fire.child("sellerID").setValue(item.getSellerID());
        fire.child("containsEggs").setValue(item.isContainsEggs());
        fire.child("containsShellfish").setValue(item.isContainsShellfish());
        fire.child("containsDairy").setValue(item.isContainsDairy());
        dbCallback.runOnSuccess();
    }

    public void editItemInActiveSellerProfile(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();
        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");
        fRef.child(item.getItemID()).push();
        fRef.child(item.getItemID()).child("sellerID").setValue(sellerId);
        fRef.child(item.getItemID()).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(item.getItemID()).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(item.getItemID()).child(quantity).setValue(item.getQuantity());
        fRef.child(item.getItemID()).child("price").setValue(item.getPrice());
        fRef.child(item.getItemID()).child("imageLink").setValue(item.getImageLink());
        fRef.child(item.getItemID()).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(item.getItemID()).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(item.getItemID()).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(item.getItemID()).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(item.getItemID()).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(item.getItemID()).child("containsDairy").setValue(item.isContainsDairy());
        dbCallback.runOnSuccess();

        Log.d("ItemID", itemID);
    }


    public void editItemInSellerProfile(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/itemsForSale/");
        Log.d("UILKK", fRef.toString());
        Log.d("ITemIDII",item.getItemID()+"");
        fRef.child(item.getItemID());
        fRef.child(item.getItemID()).child("sellerID").setValue(sellerId);
        fRef.child(item.getItemID()).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(item.getItemID()).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(item.getItemID()).child(quantity).setValue(item.getQuantity());
        fRef.child(item.getItemID()).child("price").setValue(item.getPrice());
        fRef.child(item.getItemID()).child("imageLink").setValue(item.getImageLink());
        fRef.child(item.getItemID()).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(item.getItemID()).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(item.getItemID()).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(item.getItemID()).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(item.getItemID()).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(item.getItemID()).child("containsDairy").setValue(item.isContainsDairy());
        dbCallback.runOnSuccess();

        Log.d("ItemID", item.getItemID());
    }

    public void addItemToSellersProfile(Item item, DBCallback dbCallback) {
        sellerId = item.getSellerID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/itemsForSale/");

        String itemID = item.getItemID();

        fRef.child(itemID).push();
        fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(itemID).child(quantity).setValue(item.getQuantity());
        fRef.child(itemID).child("sellerID").setValue(sellerId);
        fRef.child(itemID).child("price").setValue(item.getPrice());
        fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
        fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        dbCallback.runOnSuccess();
    }

    public void moveItemToPreviouslySoldItems(Order order) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + order.getSellerID() + "/PreviouslySold/");

        UID = order.getBuyerID();
        Log.d("UIDFORCHECKOUT", UID + "");
        sellerId = order.getSellerID();

        ArrayList<Item> itemsOrdered = order.getItemsOrdered();
        String orderID = order.getOrderID();

        for (Item item : itemsOrdered) {
            itemID = item.getItemID();

            fRef.child(orderID);
            fRef.child(orderID).child("price").setValue(order.getPrice());
            fRef.child(orderID).child("transactionToken").setValue(order.getTransactionToken());
            fRef.child(orderID).child("buyerID").setValue(order.getBuyerID());
            fRef.child(orderID).child("storeName").setValue(order.getStoreName());
            fRef.child(orderID).child("isActive").setValue(order.isActive());
            fRef.child(orderID).child("isPickup").setValue(order.isPickup());
            fRef.child(orderID).child("toDeliver").setValue(order.isToDeliver());
            fRef.child(orderID).child("sellerAddress").setValue(order.getSellerAddress());
            fRef.child(orderID).child("buyerAddress").setValue(order.getBuyerAddress());
            fRef.child(orderID).child("sellerName").setValue(order.getSellerName());
            fRef.child(orderID).child("buyerName").setValue(order.getBuyerName());
            fRef.child(orderID).child("paymentType").setValue(order.getPaymentType());
            fRef.child(orderID).child("sellerPhoneNumber").setValue(order.getSellerPhoneNumber());
            fRef.child(orderID).child("buyerPhoneNumber").setValue(order.getBuyerPhoneNumber());
            fRef.child(orderID).child("timeStamp").setValue(order.getTimeStamp());
            fRef.child(orderID).child("items");
            fRef.child(orderID).child("items").child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(orderID).child("items").child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(orderID).child("items").child(itemID).child("quantityWanted").setValue(item.getQuantityWanted());
            fRef.child(orderID).child("items").child(itemID).child("price").setValue(item.getPrice());
            fRef.child(orderID).child("items").child(itemID).child("sellerPhoneNumber").setValue(item.getSellerPhoneNumber());
            fRef.child(orderID).child("items").child(itemID).child("sellerPhoneNumber").setValue(item.getSellerPhoneNumber());
            fRef.child(orderID).child("items").child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(orderID).child("items").child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(orderID).child("items").child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(orderID).child("items").child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(orderID).child("items").child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(orderID).child("items").child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(orderID).child("items").child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        }

    }

    public void moveItemToPreviouslyBoughtItems(Item item, DBCallback dbCallback) {

        Firebase fRef = new Firebase(URL + "UserProfiles/" + item.getBuyerID() + "/PreviouslyBought/");

        String itemID = item.getItemID();

        fRef.child(itemID);
        fRef.child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
        fRef.child(itemID).child("sellerID").setValue(item.getSellerID());
        fRef.child(itemID).child("buyerID").setValue(item.getBuyerID());
        fRef.child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
        fRef.child(itemID).child(quantity).setValue(item.getQuantity());
        fRef.child(itemID).child("price").setValue(item.getPrice());
        fRef.child(itemID).child("imageLink").setValue(item.getImageLink());
        fRef.child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
        fRef.child(itemID).child("glutenFree").setValue(item.isGlutenFree());
        fRef.child(itemID).child("isVegetarian").setValue(item.isVegetarian());
        fRef.child(itemID).child("containsEggs").setValue(item.isContainsEggs());
        fRef.child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
        fRef.child(itemID).child("containsDairy").setValue(item.isContainsDairy());

        dbCallback.runOnSuccess();
    }

    public ArrayList<Order> getAllPreviouslyBoughtOrders(String userID, final DBCallback dbCallback) {
        UID = userID;

        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Orders/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Order order = dataSnapshot1.getValue(Order.class);
                    order.setOrderID(dataSnapshot1.getKey());
                    if (previouslyBought.size() < sizeofAddDBList) {
                        order.setPrice(order.price);
                        order.setSellerID(order.sellerID);
                        order.setBuyerID(order.buyerID);
                        order.setIsReviewed(order.isReviewed);
                        order.setIsPickup(order.isPickup);
                        order.setToDeliver(order.toDeliver);
                        order.setItemsOrdered(order.itemsOrdered);
                        order.setReview(order.review);
                        order.setTimeStamp(order.timeStamp);
                        order.setSellerAddress(order.sellerAddress);
                        order.setBuyerAddress(order.buyerAddress);
                        order.setSellerName(order.sellerName);
                        order.setBuyerName(order.buyerName);
                        order.setTransactionToken(order.transactionToken);
                        order.setIsActive(order.isActive);
                        order.setStoreName(order.storeName);

                        previouslyBought.add(order);

                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Orders Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Orders Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (previouslyBought.size() == sizeofAddDBList) {
            updatePreviouslyBoughtList(dbCallback);
        }
        return previouslyBought;
    }

    //Method to get Item Details For Buyer Receipt. Only returning details needed for receipt
    public ArrayList<Item> getReceiptForSpecificOrderForBuyer(String orderID, String buyerID, final DBCallback dbCallback) {
        this.UID = buyerID;
        this.orderID = orderID;
        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Orders/" + orderID + "/items");

        Log.d("OrderGoing", fRef.toString());

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Item item = dataSnapshot1.getValue(Item.class);

                    item.setItemID(dataSnapshot1.getKey());
                    item.setQuantity(item.quantityWanted);
                    item.setSellerID(item.sellerID);
                    item.setBuyerID(item.buyerID);
                    item.setNameOfItem(item.nameOfItem);
                    item.setPrice(item.price);
                    item.setSellerPhoneNumber(item.sellerPhoneNumber);
                    item.setBuyerPhoneNumber(item.buyerPhoneNumber);
                    item.setQuantity(item.quantity);
                    item.setIsVegetarian(item.isVegetarian);
                    item.setImageLink(item.imageLink);


                    if (receiptForSpecificOrder.size() < sizeofAddDBList) {
                        receiptForSpecificOrder.add(item);
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Orders Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Orders Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (receiptForSpecificOrder.size() == sizeofAddDBList) {
            updateReceipt(dbCallback);
        }

        return receiptForSpecificOrder;
    }

    public void updateBuyerWhenSellerConfirmsOrderIsReady(Order order) {
        UID = order.getBuyerID();
        orderID = order.getOrderID();

        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Orders/" + orderID);
        fRef.child(orderID).child("isActive").setValue(order.isActive());
    }


    public ArrayList<Item> updateReceipt(DBCallback dbCallback) {
        if (receiptForSpecificOrder.size() < sizeofAddDBList) {
            getReceiptForSpecificOrderForBuyer(orderID, UID, callback);
        }
        dbCallback.runOnSuccess();

        return receiptForSpecificOrder;
    }

    //Method to get Item Details For Buyer Receipt. Only returning details needed for receipt
    public ArrayList<Item> getReceiptForSpecificOrderForSeller(String orderID, String sellerId, final DBCallback dbCallback) {
        this.sellerId = sellerId;
        this.orderID = orderID;
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Orders/" + orderID + "/items");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item = dataSnapshot1.getValue(Item.class);

                    item.setItemID(dataSnapshot1.getKey());
                    item.setQuantity(item.quantityWanted);
                    item.setSellerID(item.sellerID);
                    item.setBuyerID(item.buyerID);
                    item.setItemID(dataSnapshot1.getKey());
                    item.setNameOfItem(item.nameOfItem);
                    item.setPrice(item.price);
                    item.setSellerPhoneNumber(item.sellerPhoneNumber);
                    item.setBuyerPhoneNumber(item.buyerPhoneNumber);
                    item.setQuantity(item.quantity);
                    item.setIsVegetarian(item.isVegetarian);
                    item.setImageLink(item.imageLink);
                    item.setContainsDairy(item.containsDairy);
                    item.setContainsEggs(item.containsEggs);
                    item.setContainsPeanuts(item.containsPeanuts);
                    item.setContainsShellfish(item.containsShellfish);
                    item.setGlutenFree(item.glutenFree);

                    if (receiptForSpecificOrder.size() < sizeofAddDBList) {
                        receiptForSpecificOrder.add(item);
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Orders Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Orders Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (receiptForSpecificOrder.size() == sizeofAddDBList) {
            updateReceipt(dbCallback);
        }
        return receiptForSpecificOrder;
    }


    public ArrayList<Item> updateReceiptForSeller(DBCallback dbCallback) {
        if (receiptForSpecificOrder.size() < sizeofAddDBList) {
            getReceiptForSpecificOrderForSeller(orderID, sellerId, callback);
        }
        dbCallback.runOnSuccess();
        return receiptForSpecificOrder;
    }

    public ArrayList<Order> getAllPreviouslySoldOrders(String sellerId, final DBCallback dbCallback) {
        this.sellerId = sellerId;

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/Orders/");
        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Order order = dataSnapshot1.getValue(Order.class);
                    order.setOrderID(dataSnapshot1.getKey());
                    order.setPrice(order.price);
                    order.setIsPickup(order.isPickup);
                    order.setToDeliver(order.toDeliver);
                    order.setIsReviewed(order.isReviewed);
                    order.setSellerAddress(order.sellerAddress);
                    order.setBuyerAddress(order.buyerAddress);
                    order.setSellerName(order.sellerName);
                    order.setBuyerName(order.buyerName);
                    order.setTransactionToken(order.transactionToken);
                    order.setSellerID(order.sellerID);
                    order.setBuyerID(order.buyerID);
                    order.setItemsOrdered(order.itemsOrdered);
                    order.setReview(order.review);
                    order.setTimeStamp(order.timeStamp);
                    order.setIsActive(order.isActive);
                    order.setStoreName(order.storeName);

                    if (previouslySold.size() < sizeofAddDBList) {
                        previouslySold.add(order);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Orders Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Orders Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (previouslySold.size() == sizeofAddDBList) {
            updatePreviouslySoldList(dbCallback);
        }
        return previouslySold;
    }

    public ArrayList<Order> updatePreviouslySoldList(DBCallback dbCallback) {

        if (previouslySold.size() < sizeofAddDBList) {
            getAllPreviouslySoldOrders(sellerId, callback);
        }
        dbCallback.runOnSuccess();


        return previouslySold;
    }

    public void getItemsInSpecificOrder(String sellerId, String orderID, final DBCallback dbCallback) {
        this.sellerId = sellerId;
        this.orderID = orderID;

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/Orders/" + orderID);
        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item = dataSnapshot1.getValue(Item.class);
                    item.setItemID(dataSnapshot1.getKey());
                    item.setPrice(item.price);
                    item.setImageLink(item.imageLink);
                    item.setSellerID(item.sellerID);
                    item.setBuyerID(item.buyerID);
                    item.setNameOfItem(item.nameOfItem);
                    item.setQuantity(item.getQuantity());
                    item.setDescriptionOfItem(item.descriptionOfItem);

                    if (itemsInSpecificOrder.size() < sizeofAddDBList) {
                        itemsInSpecificOrder.add(item);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Orders Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Items For Order Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (itemsInSpecificOrder.size() == sizeofAddDBList) {
            updateItemsForSpecificOrder(dbCallback);
        }

    }

    public ArrayList<Item> updateItemsForSpecificOrder(DBCallback dbCallback) {

        if (itemsInSpecificOrder.size() < sizeofAddDBList) {
            getItemsInSpecificOrder(sellerId, orderID, callback);
        }
        dbCallback.runOnSuccess();


        return itemsInSpecificOrder;
    }

    public ArrayList<Order> updatePreviouslyBoughtList(DBCallback dbCallback) {

        if (previouslyBought.size() < sizeofAddDBList) {
            getAllPreviouslyBoughtOrders(UID, callback);
        }
        dbCallback.runOnSuccess();
        return previouslyBought;
    }

    public void addCurrentOrderToSellerDB(Order order, DBCallback dbCallback) {
        UID = order.getBuyerID();
        Log.d("UIDFORCHECKOUT", UID + "");
        sellerId = order.getSellerID();
        Log.d("SELLERIDFORCHECKOUT", sellerId + "");

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/Orders/");
        ArrayList<Item> itemsOrdered = order.getItemsOrdered();

        Firebase fire1 = fRef.child(UID).push();
        String orderID = fire1.getKey();
        order.setOrderID(orderID);

        for (Item item : itemsOrdered) {
            itemID = item.getItemID();

            Log.d("ItsItemID", itemID + "");

            Firebase fire = fRef.child(orderID).push();

            fRef.child(UID).child(orderID);
            fRef.child(orderID).child("price").setValue(order.getPrice());
            fRef.child(orderID).child("transactionToken").setValue(order.getTransactionToken());
            fRef.child(orderID).child("buyerID").setValue(order.getBuyerID());
            fRef.child(orderID).child("isPickup").setValue(order.isPickup());
            fRef.child(orderID).child("toDeliver").setValue(order.isToDeliver());
            fRef.child(orderID).child("storeName").setValue(order.getStoreName());
            fRef.child(orderID).child("isActive").setValue(order.isActive());
            fRef.child(orderID).child("sellerAddress").setValue(order.getSellerAddress());
            fRef.child(orderID).child("buyerAddress").setValue(order.getBuyerAddress());
            fRef.child(orderID).child("sellerName").setValue(order.getSellerName());
            fRef.child(orderID).child("buyerName").setValue(order.getBuyerName());
            fRef.child(orderID).child("paymentType").setValue(order.getPaymentType());
            fRef.child(orderID).child("sellerPhoneNumber").setValue(order.getSellerPhoneNumber());
            fRef.child(orderID).child("buyerPhoneNumber").setValue(order.getBuyerPhoneNumber());
            fRef.child(orderID).child("timeStamp").setValue(order.getTimeStamp());
            fRef.child(orderID).child("items");
            fRef.child(orderID).child("items").child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(orderID).child("items").child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(orderID).child("items").child(itemID).child("quantityWanted").setValue(item.getQuantityWanted());
            fRef.child(orderID).child("items").child(itemID).child("price").setValue(item.getPrice());
            fRef.child(orderID).child("items").child(itemID).child("sellerPhoneNumber").setValue(item.getSellerPhoneNumber());
            fRef.child(orderID).child("items").child(itemID).child("sellerPhoneNumber").setValue(item.getSellerPhoneNumber());
            fRef.child(orderID).child("items").child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(orderID).child("items").child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(orderID).child("items").child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(orderID).child("items").child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(orderID).child("items").child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(orderID).child("items").child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(orderID).child("items").child(itemID).child("containsDairy").setValue(item.isContainsDairy());

            moveItemToPreviouslySoldItems(order);
            copyOrderToBuyerProfile(order);
            updateSellerItemsWhenItemIsBought(item, callback);
        }
        dbCallback.runOnSuccess();
    }

    public void getItemsInSpecificOrderForBuyer(String buyerID, String orderID, final DBCallback dbCallback) {
        this.UID = buyerID;
        this.orderID = orderID;

        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Orders/" + orderID);

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item = dataSnapshot1.getValue(Item.class);
                    item.setItemID(dataSnapshot1.getKey());
                    item.setPrice(item.price);
                    item.setSellerID(item.sellerID);
                    item.setImageLink(item.imageLink);
                    item.setBuyerID(item.buyerID);
                    item.setNameOfItem(item.nameOfItem);
                    item.setQuantity(item.getQuantity());
                    item.setDescriptionOfItem(item.descriptionOfItem);

                    if (itemsInSpecificOrder.size() < sizeofAddDBList) {
                        itemsInSpecificOrder.add(item);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Items For Order Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Items For Order Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (itemsInSpecificOrder.size() == sizeofAddDBList) {
            updateItemsForSpecificOrderForBuyer(dbCallback);
        }

    }

    public void changeOrderActiveStatus(Order order) {
        UID = order.getBuyerID();
        orderID = order.getOrderID();

        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Orders/" + orderID);
        fRef.child(orderID);
        fRef.child(orderID).child("isActive").setValue(order.isActive());

        changeBuyerOrderActiveStatus(order);
    }

    public void changeBuyerOrderActiveStatus(Order order) {
        sellerId = order.getSellerID();
        orderID = order.getOrderID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Orders/" + orderID);

        fRef.child(orderID);
        fRef.child(orderID).child("isActive").setValue(order.isActive());


    }

    public ArrayList<Item> updateItemsForSpecificOrderForBuyer(DBCallback dbCallback) {

        if (itemsInSpecificOrder.size() < sizeofAddDBList) {
            getItemsInSpecificOrderForBuyer(UID, orderID, callback);
        }
        dbCallback.runOnSuccess();
        return itemsInSpecificOrder;
    }

    public void copyOrderToBuyerProfile(Order order) {

        UID = order.getBuyerID();
        sellerId = order.getSellerID();
        String orderID = order.getOrderID();

        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Orders/");

        fRef.child(sellerId).child(orderID);
        fRef.child(orderID).child("price").setValue(order.getPrice());
        fRef.child(orderID).child("sellerID").setValue(order.getSellerID());
        fRef.child(orderID).child("isReviewed").setValue(order.isReviewed());
        fRef.child(orderID).child("buyerID").setValue(order.getBuyerID());
        fRef.child(orderID).child("storeName").setValue(order.getStoreName());
        fRef.child(orderID).child("isPickup").setValue(order.isPickup());
        fRef.child(orderID).child("toDeliver").setValue(order.isToDeliver());
        fRef.child(orderID).child("paymentType").setValue(order.getPaymentType());
        fRef.child(orderID).child("isActive").setValue(order.isActive());
        fRef.child(orderID).child("sellerAddress").setValue(order.getSellerAddress());
        fRef.child(orderID).child("buyerAddress").setValue(order.getBuyerAddress());
        fRef.child(orderID).child("sellerName").setValue(order.getSellerName());
        fRef.child(orderID).child("buyerName").setValue(order.getBuyerName());
        fRef.child(orderID).child("sellerPhoneNumber").setValue(order.getSellerPhoneNumber());
        fRef.child(orderID).child("buyerPhoneNumber").setValue(order.getBuyerPhoneNumber());
        fRef.child(orderID).child("timeStamp").setValue(order.getTimeStamp());
        for( Item item: order.getItemsOrdered()) {
            itemID=item.getItemID();
            fRef.child(orderID).child("items");
            fRef.child(orderID).child("items").child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(orderID).child("items").child(itemID).child("sellerPhoneNumber").setValue(order.getSellerPhoneNumber());
            fRef.child(orderID).child("items").child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(orderID).child("items").child(itemID).child("quantityWanted").setValue(item.getQuantityWanted());
            fRef.child(orderID).child("items").child(itemID).child("price").setValue(item.getPrice());
            fRef.child(orderID).child("items").child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(orderID).child("items").child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(orderID).child("items").child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(orderID).child("items").child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(orderID).child("items").child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(orderID).child("items").child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(orderID).child("items").child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        }
    }

    public ArrayList<Item> getSellersOnSaleItems(final String sellerId, final DBCallback dbCallback) {
        this.sellerId = sellerId;

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");
        Log.d("UIRL1", fRef.toString());

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ACTIVE SELLER SIZE", dataSnapshot.getChildrenCount() + "");

                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item1 = dataSnapshot1.getValue(Item.class);
                    item2 = new Item();
                    item2.setItemID(dataSnapshot1.getKey());
                    Log.d("NAMEOFITEM", item1.nameOfItem);
                    item2.setQuantity(item1.quantity);
                    item2.setSellerID(sellerId);
                    item2.setNameOfItem(item1.nameOfItem);
                    item2.setPrice(item1.price);
                    item2.setDescriptionOfItem(item1.descriptionOfItem);
                    item2.setQuantity(item1.quantity);
                    Log.d("ItemQ2", item2.getNameOfItem());
                    item2.setIsVegetarian(item1.isVegetarian);
                    item2.setImageLink(item1.imageLink);
                    item2.setContainsDairy(item1.containsDairy);
                    item2.setContainsEggs(item1.containsEggs);
                    item2.setContainsPeanuts(item1.containsPeanuts);
                    item2.setContainsShellfish(item1.containsShellfish);
                    item2.setGlutenFree(item1.glutenFree);

                    if (items.size() < sizeofAddDBList) {
                        items.add(item2);
                        Log.d("LISTING", items.toString());
                    }
                }
                Log.d("GotHere", items.toString());
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("LISTINGB4Out", items.toString());

        return items;
    }

    public ArrayList<Item> updateItemsList(DBCallback dbCallback) {

        if (arrayListOfSellerItems.size() < sizeofAddDBList) {
            getSellersOnSaleItems(sellerId, dbCallback);
        }

        dbCallback.runOnSuccess();
        return arrayListOfSellerItems;
    }


    public void sendReviewedOrderToSellerDB(Order order, DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + order.getSellerID() + "/Reviews/");
        String orderID = order.getOrderID();

        fRef.child(orderID);
        fRef.child(orderID).child("buyerID").setValue(order.getBuyerID());
        fRef.child(orderID).child("numOfStars").setValue(order.getReview().getNumOfStars());
        fRef.child(orderID).child("description").setValue(order.getReview().getReviewDescription());

        sendReviewedOrderToBuyerDB(order, dbCallback);

    }

    public void setReviewForOrderInSellerProfile(final Order order){
        sellerId=order.getSellerID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/"+sellerId);
        Log.d("INTHEREVIEWSFOR",fRef.toString());

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Seller seller = dataSnapshot.getValue(Seller.class);
                int numofStars = seller.numOfTotalStars;
                int numOfReviews = seller.numOfReviews;

                    float newAvg1;

                    float numOfStarsForOrder = order.getReview().getNumOfStars();

                    if(numOfReviews==0){
                        newAvg1=numOfStarsForOrder;
                        numOfReviews++;
                    } else {

                        float newNumToAvg = numofStars + numOfStarsForOrder;

                        numOfReviews = numOfReviews + 1;

                        newAvg1 = newNumToAvg / 2;
                    }

                    sendReviewToSellerProfileAvg(sellerId, newAvg1, numOfReviews);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void sendReviewToSellerProfileAvg(String sellerId, float newAvg, int newNumOfReviews){
        this.sellerId=sellerId;

        Firebase fRef = new Firebase(URL + "SellerProfiles/"+sellerId);

        fRef.child("numOfTotalStars").setValue(newAvg);
        fRef.child("numOfReviews").setValue(newNumOfReviews);

    }

    private void sendReviewedOrderToBuyerDB(Order order, DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "UserProfiles/" + order.getBuyerID() + "/Reviews/");
        String orderID = order.getOrderID();

        fRef.child(orderID);
        fRef.child(orderID).child("sellerID").setValue(order.getSellerID());
        fRef.child(orderID).child("numOfStars").setValue(order.getReview().getNumOfStars());
        fRef.child(orderID).child("description").setValue(order.getReview().getReviewDescription());
        dbCallback.runOnSuccess();
    }


    public ArrayList<User> getArrayListOfUsers(final DBCallback dbCallback) {
        //TOOD come back to this method and rethink the proccess

        Firebase fRef = new Firebase(URL + "UserProfiles/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    User user = dataSnapshot1.getValue(User.class);

                    if (userList.size() < sizeofAddDBList) {
                        userList.add(user);
                    }
                    Log.d("AddressList", userList.toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dbCallback.runOnFail();
            }
        });

        if (userList.size() == sizeofAddDBList) {
            updateUserList(dbCallback);
        }

        return userList;
    }

    public ArrayList<User> updateUserList(DBCallback dbCallback) {

        if (userList.size() < sizeofAddDBList) {
            getArrayListOfUsers(dbCallback);
        }

        return userList;
    }

    public ArrayList<Item> getSellerItems(String sellerID, final DBCallback dbCallback) {
        sellerId = sellerID;

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerID + "/itemsForSale");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ACTIVE SELLER SIZE", dataSnapshot.getChildrenCount() + "");

                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item1 = dataSnapshot1.getValue(Item.class);
                    item2 = new Item();
                    item2.setItemID(dataSnapshot1.getKey());
                    Log.d("NAMEOFITEM", item1.nameOfItem);
                    item2.setQuantity(item1.quantity);
                    item2.setSellerID(sellerId);
                    item2.setNameOfItem(item1.nameOfItem);
                    item2.setPrice(item1.price);
                    item2.setDescriptionOfItem(item1.descriptionOfItem);
                    item2.setQuantity(item1.quantity);
                    item2.setIsVegetarian(item1.isVegetarian);
                    item2.setImageLink(item1.imageLink);
                    item2.setContainsDairy(item1.containsDairy);
                    item2.setContainsEggs(item1.containsEggs);
                    item2.setContainsPeanuts(item1.containsPeanuts);
                    item2.setContainsShellfish(item1.containsShellfish);
                    item2.setGlutenFree(item1.glutenFree);

                    if (items.size() < sizeofAddDBList) {
                        items.add(item2);
                        Log.d("LISTING", items.toString());
                    }
                }
                Log.d("GotHere", items.toString());
                dbCallback.runOnSuccess();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mContext, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d("LISTINGB4Out", items.toString());

        return items;
    }

    //Returns list of all users
    public ArrayList<Item> updateSellersItemsNow(DBCallback dbCallback) {

        if (items.size() < sizeofAddDBList) {
            getSellerItems(sellerId, dbCallback);
        }
        return items;
    }


    public ArrayList<Item> getSellerItemsOLD(final String sellerID, final DBCallback dbCallback) {
        sellerId = sellerID;

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerID + "/itemsForSale");

        fRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();
                Log.d("Number2", dataSnapshot.getChildrenCount() + "");

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Item item = dataSnapshot1.getValue(Item.class);

                    item2.setItemID(dataSnapshot1.getKey());
                    item2.setQuantity(item.quantity);
                    item2.setSellerID(sellerId);
                    item2.setItemID(dataSnapshot1.getKey());
                    item2.setNameOfItem(item.nameOfItem);
                    item2.setPrice(item.price);
                    item2.setQuantity(item.quantity);
                    item2.setIsVegetarian(item.isVegetarian);
                    item2.setImageLink(item.imageLink);
                    item2.setContainsDairy(item.containsDairy);
                    item2.setContainsEggs(item.containsEggs);
                    item2.setContainsPeanuts(item.containsPeanuts);
                    item2.setContainsShellfish(item.containsShellfish);
                    item2.setGlutenFree(item.glutenFree);

                    if (items.size() < sizeofAddDBList) {
                        items.add(item2);
                        Log.d("ITEMSLIST", items.toString());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        Log.d("catchThis", items.toString());
        return items;
    }

    public ArrayList<Item> updateItemsList2(String sellerId, DBCallback dbCallback) {

        if (items.size() < sizeofAddDBList) {
            getSellersOnSaleItems(sellerId, dbCallback);
        }
        Log.d("catchThisATR", items.toString());
        return items;
    }

    public ArrayList<User> getAllSellers(final DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "SellerProfiles/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Seller seller = dataSnapshot1.getValue(Seller.class);

                    seller.setStoreName(seller.storeName);
                    seller.setIsCooking(seller.isCooking);
                    seller.setPhotoLink(seller.photoLink);
                    seller.setNumOfReviews(seller.numOfReviews);
                    seller.setNumOfTotalStars(seller.numOfTotalStars);
                    seller.seteMail(seller.eMail);
                    seller.setDescription(seller.description);
                    seller.setAddressString(seller.addressString);
                    seller.setLatitude(seller.latitude);
                    seller.setLongitude(seller.longitude);
                    seller.setNumOfReviews(seller.numOfReviews);


                    if (allSellersInDB.size() < sizeofAddDBList) {
                        allSellersInDB.add(seller);
                    }
                    updateAllSellersList(dbCallback);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dbCallback.runOnFail();
            }
        });

        return allUsers;
    }

    //Returns list of all users
    public ArrayList<Seller> updateAllSellersList(DBCallback dbCallback) {

        if (allSellersInDB.size() < sizeofAddDBList) {
            getAllSellers(dbCallback);
        }
        return allSellersInDB;
    }

    //gets a specific user from the DB
    public User getSpecificUser(final String UID, final DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID);

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    User user1 = dataSnapshot1.getValue(User.class);

                    if (dataSnapshot1.getKey().equals(UID)) {
                        user.setName(user1.name);
                        user.setAddressString(user1.addressString);
                        user.seteMail(user1.eMail);
                        user.setUID(user1.UID);
                        user.setPhoneNumber(user1.phoneNumber);
                        user.setLongitude(user1.longitude);
                        user.setLatitude(user1.latitude);
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dbCallback.runOnFail();
            }
        });

        return user;
    }

    //signs out user and clears data
    public boolean signOutUser(DBCallback dbCallback) {
        fireBaseRef.unauth();
        UID = null;
        user.clearUser();
//        address.clearAddress();
//        userList.clear();
//        items.clear();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.USER_INFO_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        dbCallback.runOnSuccess();

        return true;
    }


    //adds an email/potential user to a users invite tree and sends an e-mail to the invited person
    public void addToInviteTree(String newUserInviteEmail, String inviteeID, DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "InviteTree");

        fRef.child(inviteeID).push();
        fRef.child(inviteeID).child("invited").setValue(newUserInviteEmail);

        dbCallback.runOnSuccess();
    }

    //Returns a list of emails of users invited by a specific user
    public ArrayList<String> getInviteListForSpecificUser(final DBCallback dbCallback) {
        final ArrayList<String> invitesSent = new ArrayList<>();

        Firebase fRef = new Firebase(URL + "InviteTree/" + UID);

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String email = (String) dataSnapshot1.getValue();

                    if (invitesSent.size() < sizeofAddDBList) {
                        invitesSent.add(email);
                    }

                    if (invitesSent.size() == sizeofAddDBList) {
                        dbCallback.runOnSuccess();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dbCallback.runOnFail();
            }
        });

        return invitesSent;
    }

    //method takes in an item and checks who the seller of that item is, then goes
    //into the database and subtracts the number that was bought from the quantity
    //the seller currently has available. So for this we can pass it for each item
    //in the order. You can run this in the background
    public void updateSellerItemsWhenItemIsBought(final Item item1, final DBCallback dbCallback) {

        sellerId = item1.getSellerID();

        Log.d("ITEMSELLERI1", sellerId);
        final int quantityWanted = item1.getQuantityWanted();

        Log.d("ITEMID2", item.getItemID() + "");

        final Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");

        Log.d("In UpdateSeller", "IM IN HERE");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    final Item item = dataSnapshot1.getValue(Item.class);
                    if (item1.getItemID().equals(dataSnapshot1.getKey())) {

                        fRef.child(itemID).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                int oldQuantity = item.quantity;
                                final int updateQuantityAvailable = oldQuantity - quantityWanted;
                                if (updateQuantityAvailable > 0) {
                                    Log.d("Quantity Available", updateQuantityAvailable + "");
                                    item.setQuantity(updateQuantityAvailable);
                                    subtractBoughtQuantityFromQuantityInDB(item, item.getSellerID(), item.getItemID(), updateQuantityAvailable, dbCallback);
                                } else {
                                    Toast.makeText(mContext, "Only " + item.quantity + item.getNameOfItem() + "'s Available for Sale, Please Choose A Lower Quantity", Toast.LENGTH_SHORT).show();
                                    dbCallback.runOnFail();//TODO: Test Callback sends user back to cart
                                }
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                                dbCallback.runOnFail();
                            }
                        });
                    }

                    item.setItemID(dataSnapshot1.getKey());
                    item.setPrice(item1.price);
                    item.setContainsDairy(item1.containsDairy);
                    item.setContainsEggs(item1.containsEggs);
                    item.setContainsPeanuts(item1.containsPeanuts);
                    item.setContainsShellfish(item1.containsShellfish);
                    item.setGlutenFree(item1.glutenFree);
                    item.setDescriptionOfItem(item1.descriptionOfItem);
                    item.setImageLink(item1.imageLink);
                    item.setIsVegetarian(item1.isVegetarian());
                    item.setSellerID(item1.sellerID);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                dbCallback.runOnFail();
            }
        });

        Log.d("OOFMethod", "IM OUTTA HERE");

    }

    //Method that actually updates the number in the database
    public void subtractBoughtQuantityFromQuantityInDB(Item item, String sellerId, String itemID, int quantityAvailable, DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/itemsForSale/");
        fRef.child(itemID);

        if (quantityAvailable > 0) {
            fRef.child(itemID).child("quantity").setValue(quantityAvailable);
            dbCallback.runOnSuccess();
        } else {
            removeItemFromSale(item, callback);
        }
    }

    //method to send an order to the sellers database and then send that order to the seller
    public void sendOrderToSeller(String sellerId, Order order, String UID, DBCallback dbCallback) {
        this.UID = UID;
        this.sellerId = sellerId;

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId + "/Orders/" + UID);

        ArrayList<Item> itemsOrdered = order.getItemsOrdered();

        Firebase fire1 = fRef.child(UID).push();
        String orderID = fire1.getKey();

        for (Item item : itemsOrdered) {

            String itemID = item.getItemID();
            fRef.child(UID).child(orderID).push();
            fRef.child(orderID).child(itemID);
            fRef.child(orderID).child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(orderID).child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(orderID).child(itemID).child(quantity).setValue(item.getQuantity());
            fRef.child(orderID).child(itemID).child("price").setValue(item.getPrice());
            fRef.child(orderID).child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(orderID).child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(orderID).child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(orderID).child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(orderID).child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(orderID).child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(orderID).child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        }

        getOrderToSendToSeller(UID, order, UID, dbCallback);
    }

    //method which returns the right order to be sent to the seller when buyer places an order
    public Order getOrderToSendToSeller(String sellerID, final Order order, String buyerID, final DBCallback dbCallback) {
        sellerId = sellerID;
        final String orderID = order.getOrderID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Orders/" + buyerID);

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Order order1 = dataSnapshot1.getValue(Order.class);

                    if (order1.getOrderID() == orderID) {
                        order1.setOrderID(orderID);
                        order1.setSellerID(sellerId);
                        order1.setPrice(order1.price);
                        order1.setBuyerID(order1.buyerID);

                        returnOrder = order1;
                    }
                }
                dbCallback.runOnSuccess();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Send Order To Seller, Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Send Order To Seller, Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();

            }
        });
        return returnOrder;
    }

    public void addUserReviewToUserProfile(User buyer, User seller, int numOfStars, String details, DBCallback dbCallback) {
        sellerId = seller.getUID();
        UID = buyer.getUID();
        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Reviews/");

        fRef.child(sellerId).push();
        fRef.child(sellerId).child("numOfStars").setValue(numOfStars);
        fRef.child(sellerId).child("reviewDescription").setValue(details);
        dbCallback.runOnSuccess();
    }

    public void addUserReviewToUserProfile(User buyer, User seller, int numOfStars, DBCallback dbCallback) {

        sellerId = seller.getUID();
        UID = buyer.getUID();
        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Reviews/");

        fRef.child(sellerId).push();
        fRef.child(sellerId).child("numOfStars").setValue(numOfStars);
        dbCallback.runOnSuccess();

    }

    public void addReviewToSellerProfile(String buyerID, String sellerID, int numOfStars) {
        sellerId = sellerID;
        UID = buyerID;
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Reviews/");

        fRef.child(UID).push();
        fRef.child(UID).child("numOfStars").setValue(numOfStars);
    }

    public void addReviewToSellerProfile(User buyer, User seller, int numOfStars, String details, DBCallback dbCallback) {
        sellerId = seller.getUID();
        UID = buyer.getUID();
        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Reviews/");

        fRef.child(UID).push();
        fRef.child(UID).child("numOfStars").setValue(numOfStars);
        fRef.child(UID).child("reviewDescription").setValue(details);
        dbCallback.runOnSuccess();
    }

    public void getAllReviewsForCertainSeller(String sellerID, final DBCallback dbCallback) {
        sellerId = sellerID;

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Reviews/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Review review = dataSnapshot1.getValue(Review.class);
                    review.setBuyerID(review.buyerID);
                    review.setNumOfStars(review.numOfStars);
                    review.setReviewDescription(review.reviewDescription);
                    review.setSellerID(review.sellerID);

                    if (sellersReviewArrayList.size() < sizeofAddDBList) {
                        sellersReviewArrayList.add(review);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Reviews Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Reviews Please Try Again", Toast.LENGTH_SHORT).show();
                dbCallback.runOnFail();
            }
        });

        if (sellersReviewArrayList.size() == sizeofAddDBList) {
            updateListOfReviewsForSeller(dbCallback);
        }

    }

    public void getListOfReviewsForCertainUser(String UID, DBCallback dbCallback) {
        this.UID = UID;
        Firebase fRef = new Firebase(URL + "UserProfiles/" + UID + "/Reviews/");

        fRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sizeofAddDBList = dataSnapshot.getChildrenCount();

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Review review = dataSnapshot1.getValue(Review.class);
                    review.setBuyerID(review.buyerID);
                    review.setNumOfStars(review.numOfStars);
                    review.setReviewDescription(review.reviewDescription);
                    review.setSellerID(review.sellerID);

                    if (reviewArrayList.size() < sizeofAddDBList) {
                        reviewArrayList.add(review);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Snackbar
                        .make(SignUpFirstActivity.coordinatorLayoutView, "Unable To Retrieve Reviews Please Try Again", Snackbar.LENGTH_SHORT)
                        .show();
                Toast.makeText(mContext, "Unable To Retrieve Reviews Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });

        if (reviewArrayList.size() == sizeofAddDBList) {
            updateListOfReviews(dbCallback);
        }
    }

    public ArrayList<Review> updateListOfReviews(DBCallback dbCallback) {
        if (reviewArrayList.size() < sizeofAddDBList) {

            getListOfReviewsForCertainUser(UID, dbCallback);
        }
        dbCallback.runOnSuccess();
        return reviewArrayList;
    }

    public ArrayList<Review> updateListOfReviewsForSeller(DBCallback dbCallback) {
        if (sellersReviewArrayList.size() < sizeofAddDBList) {
            getAllReviewsForCertainSeller(sellerId, dbCallback);
        }
        return sellersReviewArrayList;
    }

    public void updateSellerProfileWithNewAvg(Seller seller, char newAvg) {
        sellerId = seller.getUID();

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Reviews/");
        fRef.child(UID).push();
        fRef.child(UID).child("AvgNumOfStars").setValue(String.valueOf(newAvg));
    }

    //May want to implement this later
    public void moveOrderFromActiveToFulfilled(String sellerId, String orderID, String buyerID, DBCallback dbCallback) {
        this.sellerId = sellerId;
        this.UID = buyerID;

        Firebase fRef = new Firebase(URL + "SellerProfiles/" + sellerId + "/Orders/" + "/" + UID);

        fRef.child(buyerID).removeValue();
        dbCallback.runOnSuccess();
    }

    public void removeActiveSellerWhenLoggedOut(Seller seller, DBCallback dbCallback) {
        sellerId = seller.getUID();

        Firebase fRef = new Firebase(URL + "ActiveSellers/" + sellerId);

        fRef.child(sellerId).removeValue();
    }

    public void sentToFullfilledOrdersTable(Order order, DBCallback dbCallback) {
        sellerId = order.getSellerID();
        UID = order.getBuyerID();

        Firebase fRef = new Firebase(URL + "FulfilledOrders/");
        ArrayList<Item> itemsOrdered = order.getItemsOrdered();

        Firebase fire1 = fRef.child(sellerId).push();
        String orderID = fire1.getKey();
        for (Item item : itemsOrdered) {

            String itemID = item.getItemID();
            fRef.child(sellerId).child(UID).push();
            fRef.child(UID).child(orderID);
            fRef.child(orderID).child(itemID);
            fRef.child(orderID).child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(orderID).child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(orderID).child(itemID).child(quantity).setValue(item.getQuantity());
            fRef.child(orderID).child(itemID).child("price").setValue(item.getPrice());
            fRef.child(orderID).child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(orderID).child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(orderID).child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(orderID).child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(orderID).child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(orderID).child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(orderID).child(itemID).child("containsDairy").setValue(item.isContainsDairy());
        }

        dbCallback.runOnSuccess();
    }

    public void sentToFullfilledOrdersTableWithReview(Order order, DBCallback dbCallback) {
        sellerId = order.getSellerID();
        UID = order.getBuyerID();

        Firebase fRef = new Firebase(URL + "FulfilledOrders/");
        ArrayList<Item> itemsOrdered = order.getItemsOrdered();

        Firebase fire1 = fRef.child(sellerId).push();
        String orderID = fire1.getKey();
        for (Item item : itemsOrdered) {

            String itemID = item.getItemID();
            fRef.child(sellerId).child(UID).push();
            fRef.child(UID).child(orderID);
            fRef.child(orderID).child(itemID);
            fRef.child(orderID).child(itemID).child("nameOfItem").setValue(item.getNameOfItem());
            fRef.child(orderID).child(itemID).child("descriptionOfItem").setValue(item.getDescriptionOfItem());
            fRef.child(orderID).child(itemID).child(quantity).setValue(item.getQuantity());
            fRef.child(orderID).child(itemID).child("price").setValue(item.getPrice());
            fRef.child(orderID).child(itemID).child("imageLink").setValue(item.getImageLink());
            fRef.child(orderID).child(itemID).child("containsPeanuts").setValue(item.isContainsPeanuts());
            fRef.child(orderID).child(itemID).child("glutenFree").setValue(item.isGlutenFree());
            fRef.child(orderID).child(itemID).child("isVegetarian").setValue(item.isVegetarian());
            fRef.child(orderID).child(itemID).child("containsEggs").setValue(item.isContainsEggs());
            fRef.child(orderID).child(itemID).child("containsShellfish").setValue(item.isContainsShellfish());
            fRef.child(orderID).child(itemID).child("containsDairy").setValue(item.isContainsDairy());
            fRef.child(orderID).child(itemID).child("reviewDescription").setValue(order.getReview().getReviewDescription());
        }
        dbCallback.runOnSuccess();
    }

    public void addFoodAndImageHashmapToFoodAndImageTable(HashMap<String, String> foods, DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "PreMadeFoodDatabase/");
        for (Map.Entry<String, String> food : foods.entrySet()) {
            String name = food.getKey();
            String photoLink = food.getValue();
            fRef.child(name).setValue(photoLink);
        }
        dbCallback.runOnSuccess();
    }

    //May want to allow users to add their own photos later?
    public void addCustomMadeFoodItemToDatabase(String name, String linkToPhoto, DBCallback dbCallback) {
        Firebase fRef = new Firebase(URL + "CustomFoodDatabase/");

        fRef.child(name).push().setValue(linkToPhoto);
        dbCallback.runOnSuccess();

    }

}