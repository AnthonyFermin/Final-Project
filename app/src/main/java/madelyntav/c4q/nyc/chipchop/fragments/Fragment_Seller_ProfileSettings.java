package madelyntav.c4q.nyc.chipchop.fragments;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import madelyntav.c4q.nyc.chipchop.DBCallback;
import madelyntav.c4q.nyc.chipchop.DBObjects.Address;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.DBObjects.User;
import madelyntav.c4q.nyc.chipchop.GeolocationAPI.Geolocation;
import madelyntav.c4q.nyc.chipchop.GeolocationAPI.GeolocationAPI;
import madelyntav.c4q.nyc.chipchop.GeolocationAPI.Location;
import madelyntav.c4q.nyc.chipchop.HelperMethods;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.SellActivity;
import madelyntav.c4q.nyc.chipchop.ServiceSellerNotify;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Fragment_Seller_ProfileSettings extends Fragment {


    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/geocode";

    DBHelper dbHelper;

    public static final String TAG = "fragment_seller_profile";

    ToggleButton cookingStatus;
    TextView cookingStatusTV;

    TextView sellerNameTV;

    EditText storeNameET;
    EditText addressET;
    EditText aptET;
    EditText cityET;
    EditText zipcodeET;
    EditText phoneNumberET;
    EditText stateET;
    View coordinatorLayoutView;

    Button saveButton;

    String storeName;
    String address;
    String apt;
    String city;
    String state;
    String zipcode;
    String phoneNumber;
    String imageLink;

    Seller seller = null;
    User user;
    Address userAddress;
    ArrayList<Item> sellerItems;

    SellActivity activity;

    RelativeLayout loadingPanel;
    LinearLayout containingView;

    ImageButton profilePhoto;
    public static final int RESULT_OK = -1;
    private Uri imageFileUri;
    Intent intent;
    private String stringVariable = "file:///sdcard/_pictureholder_id.jpg";

    DBCallback emptyCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_seller_profile, container, false);

        initializeData();

        initializeViews(root);

        loadSellerInfo();

        setListeners();

        return root;
    }

    private void loadSellerInfo(){

        Log.d("Seller Profile", "LOADING SELLER INFO");
        if(activity.getSeller() == null) {
            seller = dbHelper.getSellerFromDB(dbHelper.getUserID());
            Log.d("Load Seller Info", "LOADING FROM DB: " + dbHelper.getUserID());
            load();
        }else{
            seller = activity.getSeller();
            Log.d("Load Seller Info", "PREVIOUSLY CACHED");
            setEditTexts();
            loadingPanel.setVisibility(View.GONE);
            containingView.setVisibility(View.VISIBLE);
        }

    }

    private void getGeoLocation(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();

        GeolocationAPI geolocationAPI = restAdapter.create(GeolocationAPI.class);

        address = addressET.getText().toString();
        address = address.trim().replace(" ", "+");
        storeName = storeNameET.getText().toString().trim();
        apt = aptET.getText().toString();
        city = cityET.getText().toString().trim().replace(' ', '+');
        state = stateET.getText().toString().trim().replace(' ', '+');
        phoneNumber = phoneNumberET.getText().toString().replace("-", "");

        String queryString = address + ",+" + city + ",+" + state;// + "&key=" + APIKEY;
        Log.i("RETROFIT- Geocode Query", queryString);

        geolocationAPI.getGeolocation(queryString, new Callback<Geolocation>() {
            @Override
            public void success(Geolocation geolocation, Response response) {
                String uid = dbHelper.getUserID();
                Location location = geolocation.getResults().get(0).getGeometry().getLocation();

                address = address.replace('+', ' ');
                city = city.replace('+', ' ');

                userAddress = new Address(address, apt, city, state, zipcode, uid);

                Log.i("RETROFIT: LatLng", "" + location.getLat() + ", " + location.getLng());

                userAddress.setLatitude(location.getLat());
                userAddress.setLongitude(location.getLng());
                seller = new Seller(uid, user.geteMail(), user.getName(), userAddress, storeName, phoneNumber);
                seller.setLatitude(location.getLat() + "");
                seller.setLongitude(location.getLng() + "");
                seller.setIsCooking(false);
                seller.setPhotoLink(imageLink);
                dbHelper.addSellerProfileInfoToDB(seller);
                dbHelper.setSellerCookingStatus(seller.getIsCooking());
                activity.setSeller(seller);

                user.setAddress(userAddress);
                user.setPhoneNumber(phoneNumber);
                dbHelper.addUserProfileInfoToDB(user);

                Snackbar
                        .make(coordinatorLayoutView, "Changes Saved", Snackbar.LENGTH_SHORT)
                        .show();

                setReadOnlyAll(true);
                saveButton.setText("Edit Profile");
            }

            @Override
            public void failure(RetrofitError error) {
                Snackbar
                        .make(coordinatorLayoutView, "Invalid Address", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });


    }

    private void load(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                int i = 0;
                do{
                    Log.d("Load Seller Info", "Attempt #" + i);
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (i > 10){
                        Log.d("Load Seller Info", "DIDN'T LOAD");
                        break;
                    }
                    i++;
                }while(seller == null || seller.getStoreName() == null || seller.getStoreName().isEmpty());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if(seller != null) {
                    Log.d("Load Seller Info", "LOAD FROM DB SUCCESSFUL");
                    Log.d("SELLER INFO", "UID: " + seller.getUID());
                    Log.d("SELLER INFO", "Name: " + seller.getName());
                    Log.d("SELLER INFO", "Store Name: " + seller.getStoreName());
                    Log.d("SELLER INFO", "isCooking: " + seller.getIsCooking());
                    setEditTexts();
                }else{
                    //TODO:display cannot connect to internet error message
                    Log.d("Load Seller Info", "NOT FOUND IN DB, NEW SELLER CREATED");
                    seller = new Seller(dbHelper.getUserID(),user.geteMail(),user.getName(),user.getAddress(),"",user.getPhoneNumber());
                    seller.setIsCooking(false);
                    dbHelper.addSellerProfileInfoToDB(seller);
                    dbHelper.setSellerCookingStatus(false);
                    Snackbar
                            .make(coordinatorLayoutView, "New Seller Profile Created, Please add a store name", Snackbar.LENGTH_SHORT)
                            .show();
                }
                activity.setSeller(seller);
                loadingPanel.setVisibility(View.GONE);
                containingView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }

    private void setEditTexts(){
        Log.d("SELLER INFO", "Is Cooking: " + seller.getIsCooking());
        activity.setCookingStatus(seller.getIsCooking());
        if(activity.isCurrentlyCooking()){
            cookingStatus.setChecked(true);
            cookingStatusTV.setVisibility(View.VISIBLE);
            saveButton.setEnabled(false);
            Intent intent = new Intent(activity,ServiceSellerNotify.class).putExtra(ServiceSellerNotify.SELLER_ID,seller.getUID());
            activity.startService(intent);
        }else{
            cookingStatus.setChecked(false);
            cookingStatusTV.setVisibility(View.INVISIBLE);
            saveButton.setEnabled(true);
        }

        Address address = user.getAddress();
        sellerNameTV.setText(user.getName());
        storeNameET.setText(seller.getStoreName());
        if(address != null) {
            addressET.setText(address.getStreetAddress());
            aptET.setText(address.getApartment());
            cityET.setText(address.getCity());
            stateET.setText(address.getState());
            zipcodeET.setText(address.getZipCode());
        }
        phoneNumberET.setText(user.getPhoneNumber());
        imageLink = seller.getPhotoLink();
        if(imageLink != null && !imageLink.isEmpty() && imageLink.length() > 200) {
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
                    profilePhoto.setImageBitmap(bitmap);
                }
            }.execute();
        }

    }

    private boolean hasOneItemWithPosQuant(){

        for(Item item: sellerItems){
            if(item.getQuantity() > 0)
                return true;
        }
        return false;
    }

    //This handles the activity for the intent: using the camera and choosing from a gallery.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageFileUri = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.getContentResolver().query(imageFileUri,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            rescaleImageForDb(filePath);
            Log.d("Seller Profile","ImageLink: " + imageLink);
        }

        if (requestCode == 0 && resultCode == RESULT_OK) {
            imageFileUri = Uri.parse(stringVariable);
            String filePath = imageFileUri.getPath();
            rescaleImageForDb(filePath);
            Log.d("Seller Profile","ImageLink: " + imageLink);
        }

        Log.d("Seller Profile", "Image Uri: " + imageFileUri);
        if (imageFileUri != null) {
            profilePhoto.setImageURI(imageFileUri);
        }
    }

    private void rescaleImageForDb(final String filePath) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                Bitmap rotatedBitmap = rotateBitmap(bitmap,filePath);
                Bitmap scaledBitmap = null;
                if(rotatedBitmap != null) {
                    scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, 500, 333, false);
                }else{
                    scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500,333, false);
                }


                File file = new File(filePath);
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file);
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return scaledBitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageLink = HelperMethods.saveImageToEncodedString(filePath);
                profilePhoto.setImageBitmap(bitmap);
                Log.d("Item Creation", "ImageLink: " + imageLink);
            }
        }.execute();
    }

    private Bitmap rotateBitmap(Bitmap bitmap, String filePath){
        Bitmap rotatedBitmap = null;

        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            Matrix matrix = new Matrix();

            switch(orientation){
                case 3:
                    matrix.postRotate(180);
                    break;
                case 6:
                    matrix.postRotate(90);
                    break;
                case 8:
                    matrix.postRotate(270);
                    break;
            }

            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotatedBitmap;
    }

    //This is for the dialog box: Camera or Gallery
    private void showListViewDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Set Profile Image");
        final String[] items = {"Camera", "Gallery"};
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (items[which].equalsIgnoreCase("Camera")) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo = new File(stringVariable);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        startActivityForResult(intent, 0);
                    }
                }

                if (items[which].equalsIgnoreCase("Gallery")) {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
//        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    private void initializeViews(View root){
        sellerNameTV = (TextView) root.findViewById(R.id.seller_name_tv);

        storeNameET = (EditText) root.findViewById(R.id.store_name);
        addressET = (EditText) root.findViewById(R.id.address);
        aptET = (EditText) root.findViewById(R.id.apt);
        cityET = (EditText) root.findViewById(R.id.city);
        stateET = (EditText) root.findViewById(R.id.state);
        zipcodeET = (EditText) root.findViewById(R.id.zipcode);
        phoneNumberET = (EditText) root.findViewById(R.id.phone_number);
        stateET = (EditText) root.findViewById(R.id.state);
        setReadOnlyAll(true);

        cookingStatus = (ToggleButton) root.findViewById(R.id.cooking_status);
        cookingStatusTV = (TextView) root.findViewById(R.id.cooking_status_text);
        saveButton = (Button) root.findViewById(R.id.save_button);
        coordinatorLayoutView = root.findViewById(R.id.snackbarPosition);


        if(activity.isCurrentlyCooking()){
            cookingStatus.setChecked(true);
            cookingStatusTV.setVisibility(View.VISIBLE);
            saveButton.setEnabled(false);
        }else{
            cookingStatus.setChecked(false);
            cookingStatusTV.setVisibility(View.INVISIBLE);
            saveButton.setEnabled(true);
        }

        profilePhoto = (ImageButton) root.findViewById(R.id.profile_image);

        //view initialization for loading progress bar spinner
        loadingPanel = (RelativeLayout) root.findViewById(R.id.loadingPanel);
        containingView = (LinearLayout) root.findViewById(R.id.container);
        containingView.setVisibility(View.INVISIBLE);
        loadingPanel.setVisibility(View.VISIBLE);

    }

    private void setListeners(){

        cookingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveButton.getText().toString().equalsIgnoreCase("save changes")) {
                    saveButton.callOnClick();
                }
                if (cookingStatus.getText().toString().equalsIgnoreCase("on")) {
                    //TODO: add confirmation dialog when changing cooking status mention to click save to commit changes
                    sellerItems = activity.getSellerItems();
                    if(storeNameET.getText().toString().isEmpty()){
                        Snackbar
                                .make(coordinatorLayoutView, "Please add a store name", Snackbar.LENGTH_SHORT)
                                .show();
                        cookingStatus.setChecked(false);
                    }else if (sellerItems != null && hasOneItemWithPosQuant()) {
                        seller.setUID(dbHelper.getUserID());
                        seller.setItems(sellerItems);
                        Log.d("Seller Info", seller.getUID() + "");
                        Log.d("Seller Info", seller.getName() + "");
                        Log.d("Seller Info", seller.getStoreName() + "");
                        seller.setIsCooking(true);
                        seller.setPhotoLink(imageLink);
                        dbHelper.setSellerCookingStatus(true);
                        activity.setSeller(seller);
                        dbHelper.sendSellerToActiveSellerTable(seller);

                        activity.setCookingStatus(true);
                        saveButton.setEnabled(false);
                        cookingStatusTV.setVisibility(View.VISIBLE);
                        Snackbar
                                .make(coordinatorLayoutView, "Cooking Status Active", Snackbar.LENGTH_SHORT)
                                .show();
                        Intent intent = new Intent(activity,ServiceSellerNotify.class).putExtra(ServiceSellerNotify.SELLER_ID,seller.getUID());
                        activity.startService(intent);
                    } else {
                        cookingStatusTV.setVisibility(View.INVISIBLE);
                        Snackbar
                                .make(coordinatorLayoutView, "Please add an item with positive quantity", Snackbar.LENGTH_SHORT)
                                .show();
                        activity.replaceSellerFragment(new Fragment_Seller_Items());
                    }

                } else {
                    saveButton.setEnabled(true);
                    cookingStatusTV.setVisibility(View.INVISIBLE);
                    dbHelper.removeSellersFromActiveSellers(seller, emptyCallback);
                    activity.setCookingStatus(false);
                    seller.setIsCooking(false);
                    activity.setSeller(seller);
                    dbHelper.setSellerCookingStatus(false);
                    Snackbar
                            .make(coordinatorLayoutView, "Cooking Status Deactivated", Snackbar.LENGTH_SHORT)
                            .show();

                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: check if all EditTexts are filled in correctly ie: phone number has correct format
                if(saveButton.getText().toString().equalsIgnoreCase("save changes")) {
                    getGeoLocation();
                }else{
                    if(activity.isCurrentlyCooking()){
                        Snackbar
                                .make(coordinatorLayoutView, "Cannot edit seller profile while actively cooking", Snackbar.LENGTH_SHORT)
                                .show();
                    }else {
                        saveButton.setText("Save Changes");
                        setReadOnlyAll(false);
                    }
                }
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showListViewDialog();
            }
        });

        phoneNumberET.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

    }

    private void initializeData(){

        emptyCallback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };

        dbHelper = DBHelper.getDbHelper(getActivity());
        activity = (SellActivity) getActivity();
        imageLink = "";

        user = activity.getUser();

        activity.setCurrentFragment(TAG);

    }

    private void setReadOnlyAll(boolean readOnly){
        setReadOnly(storeNameET, readOnly);
        setReadOnly(addressET, readOnly);
        setReadOnly(aptET, readOnly);
        setReadOnly(cityET, readOnly);
        setReadOnly(zipcodeET, readOnly);
        setReadOnly(phoneNumberET, readOnly);
        setReadOnly(stateET, readOnly);
    }

    private void setReadOnly(EditText view, boolean readOnly) {
        view.setFocusable(!readOnly);
        view.setFocusableInTouchMode(!readOnly);
        view.setClickable(!readOnly);
        view.setLongClickable(!readOnly);
        view.setCursorVisible(!readOnly);
        if(readOnly){
            view.setTextColor(Color.BLACK);
        }else{
            view.setTextColor(Color.parseColor("#D51F27"));
        }
    }

}
