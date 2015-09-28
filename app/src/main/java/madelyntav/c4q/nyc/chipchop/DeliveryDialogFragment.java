package madelyntav.c4q.nyc.chipchop;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;

public class DeliveryDialogFragment extends android.support.v4.app.DialogFragment {

    Button deliverButton, pickupButton;
    Seller seller;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View root = inflater.inflate(R.layout.fragment_dialog_delivery, container, false);

        initializeData();
        bindViews(root);
        initializeViews();
        setListeners();

        return root;
    }

    private void initializeViews() {
        if(seller.isDeliveryAvailable()){
            deliverButton.setVisibility(View.VISIBLE);
        }

        if(!seller.isPickUpAvailable()){
            deliverButton.setVisibility(View.VISIBLE);
            pickupButton.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        deliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperMethods.getCurrentOrder().setToDeliver(true);
                HelperMethods.getCurrentOrder().setIsPickup(false);
                getDialog().dismiss();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PaymentDialogFragment alertDialog = new PaymentDialogFragment();
                alertDialog.show(fm, "fragment_alert");
            }
        });


        pickupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperMethods.getCurrentOrder().setIsPickup(true);
                HelperMethods.getCurrentOrder().setToDeliver(false);
                getDialog().dismiss();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PaymentDialogFragment alertDialog = new PaymentDialogFragment();
                alertDialog.show(fm, "fragment_alert");
            }
        });

    }

    private void bindViews(View root) {

        pickupButton = (Button) root.findViewById(R.id.pickup_button);
        deliverButton = (Button) root.findViewById(R.id.delivery_button);
    }

    private void initializeData() {
        seller = HelperMethods.getSellerToView();
    }
}
