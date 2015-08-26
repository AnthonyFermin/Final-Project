package madelyntav.c4q.nyc.chipchop.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBCallback;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.DBObjects.User;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.adapters.SellerItemsAdapter;
import madelyntav.c4q.nyc.chipchop.adapters.SellerListAdapter;


public class Fragment_Buyer_Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    FloatingActionButton refreshButton;
    ImageView arrowImage;
    SlidingUpPanelLayout slidingPanel;
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public final static String PREF_NAME = "Settings";
    public static final String LASTLONGITUDE = "LastLongitude";
    public static final String LASTLATITUDE = "LastLatitude";
    public Button signupButton;
    public ArrayList<madelyntav.c4q.nyc.chipchop.DBObjects.Address> addressList;
    private LocationRequest mLocationRequest;
    private GoogleMap map;
    private Circle mCircle;
    private Geofence mGeofence;
    private SharedPreferences preferences;
    private Location location = new Location("Current Location");
    private boolean gps_enabled = false;
    private GoogleApiClient googleApiClient;
    private DBHelper dbHelper;
    private ArrayList<Seller> sellers;
    private RecyclerView sellersList;
    private View root;
    ArrayList<User> userList;
    User user;
    User user1;
    ArrayList<LatLng> latsList;

    DBCallback emptyCallback;

    public static final String TAG = "fragment_buyer_map";

    BuyActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (root != null) {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null)
                parent.removeView(root);
        }
        try {
            root = inflater.inflate(R.layout.fragment_buyer_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        dbHelper = DBHelper.getDbHelper(getActivity());

        activity = (BuyActivity) getActivity();

        activity.setCurrentFragment(Fragment_Buyer_Map.TAG);


        emptyCallback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };


        latsList= new ArrayList<>();
        addressList=new ArrayList<>();
        userList= new ArrayList<>();
        signupButton= (Button) root.findViewById(R.id.signInButton);

        // Connect to Geolocation API to make current location request
        locationServiceIsAvailable();
        connectGoogleApiClient();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)        // 10 seconds, in milliseconds
                .setFastestInterval(5000); // 1 second, in milliseconds

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();

        sellers = new ArrayList<>();
        populateItems();

        arrowImage = (ImageView) root.findViewById(R.id.arrow_image);

        slidingPanel = (SlidingUpPanelLayout) root.findViewById(R.id.slidinglayout);
        slidingPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {

            }

            @Override
            public void onPanelCollapsed(View view) {
                arrowImage.setImageDrawable(getResources().getDrawable(R.drawable.up));

            }

            @Override
            public void onPanelExpanded(View view) {
                arrowImage.setImageDrawable(getResources().getDrawable(R.drawable.down));
                sellersList.setFocusable(true);
            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        sellersList = (RecyclerView) root.findViewById(R.id.buyers_orders_list);
        sellersList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        sellersList.addItemDecoration(new MarginDe(this));
        sellersList.setHasFixedSize(true);
//        sellersList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        sellersList.setAdapter(new NumberedAdapter(30));

        SellerListAdapter sellersListAdapter = new SellerListAdapter(getActivity(), sellers);
        sellersList.setAdapter(sellersListAdapter);

        //getListForMarkers.execute();

        refreshButton = (FloatingActionButton) root.findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: WRITE CODE TO REFRESH RECYCLERVIEW !!
            }
        });


        return root;
    }

    //test method to populate RecyclerView
    private void populateItems(){
        for(int i = 0; i < 10; i++) {

            sellers.add(new Seller("test", "Github Cat", "Github Cat", new madelyntav.c4q.nyc.chipchop.DBObjects.Address(), "http://wisebread.killeracesmedia.netdna-cdn.com/files/fruganomics/imagecache/605x340/blog-images/food-186085296.jpg", "ajs;djf;d"));

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        locationServiceIsAvailable();
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
    }

    public void locationServiceIsAvailable() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Toast.makeText(getActivity(), "Location Services disabled", Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (location != null)
                    handleNewLocation(location);
            }
        }, 2000);
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location == null)
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        else
            handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Map", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override // must override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        preferences = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LASTLATITUDE, String.valueOf(location.getLatitude()));
        editor.putString(LASTLONGITUDE, String.valueOf(location.getLongitude())).apply();
        handleNewLocation(location);
    }

    private void handleNewLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng locationLatLng = new LatLng(latitude, longitude);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        // Set initial view to current location
        map.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    protected synchronized void connectGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();
        Log.d("Map", "Connected to Google API Client");
    }


    public interface OnBuyerMapFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void addWithinRangeMarkersToMap() {
                for (madelyntav.c4q.nyc.chipchop.DBObjects.Address address : addressList) {

                    String userName= address.getName();
                    double gLat = address.getLatitude();
                    double gLng = address.getLongitude();

                    double lat = 40.7484;
                    double lng = -73.9857;

                    Circle circle = map.addCircle(new CircleOptions()
                            .center(new LatLng(lat, lng))
                            .radius(100000)
                            .strokeColor(Color.RED));

                    float[] distance = new float[addressList.size()];

                    Location.distanceBetween(lat, lng,
                            gLat, gLng, distance);

                    if (distance[0] < circle.getRadius()) {

                        map.addMarker(new MarkerOptions()
                                .position(new LatLng(gLat, gLng))
                                .title(userName));

                    } else {

                    }
                }
            }

    // Task to get LatLng List and populate markers when done
    AsyncTask<Void, Void, Void> getListForMarkers = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            dbHelper.getAllActiveSellers(emptyCallback);

            while(sellers.size()==0){
                // thread cannot continue until dbHelper.getUserListLatLng returns an ArrayList
                Log.d("LATSLIST", addressList.toString());

                sellers.addAll(dbHelper.getAllActiveSellers(emptyCallback));
            }
            //create markers list by sorting throught latsList
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("LATSLIST", addressList.toString());
            addWithinRangeMarkersToMap();
        }
    };

    @Override
    public void onDetach() {
        activity.setCurrentFragment("");
        super.onDetach();
    }


}