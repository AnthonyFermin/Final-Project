package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.HelperMethods;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Buyer_SellerProfile;

public class SellerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Seller> sellers;
    private Context context;
    private int lastPosition = -1;

    public SellerListAdapter(Context context, ArrayList<Seller> sellers) {
        this.context = context;
        this.sellers = sellers;
    }

    private class SellersViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView name;
        TextView description;
        ImageView image;
        TextView distance;
        RatingBar ratingBar;

        public SellersViewHolder(View itemView) {
            super(itemView);

            container = (RelativeLayout) itemView.findViewById(R.id.container);
            name = (TextView) itemView.findViewById(R.id.store_name);
            description = (TextView) itemView.findViewById(R.id.store_description);
            image = (ImageView) itemView.findViewById(R.id.food_background);
            distance = (TextView) itemView.findViewById(R.id.distance_tv);
            ratingBar= (RatingBar) itemView.findViewById(R.id.rating_bar);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("YEHDHH",sellers.get(getAdapterPosition()).getAddressString()+"");
                    BuyActivity activity = (BuyActivity) context;
                    HelperMethods.setSellerToView(sellers.get(getAdapterPosition()));
                    activity.setSellerToView(sellers.get(getAdapterPosition()));
                    activity.replaceFragment(new Fragment_Buyer_SellerProfile());
                }
            });

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_search_list_item, parent, false);
        return new SellersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Seller seller = sellers.get(position);
        final SellersViewHolder vh = (SellersViewHolder) viewHolder;
        vh.name.setText(seller.getStoreName());
        vh.description.setText(seller.getDescription());
        vh.distance.setText(String.valueOf(seller.getDistanceFromBuyer() + " mi"));
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
                    vh.image.setImageBitmap(bitmap);
                    vh.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }.execute();
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
        return sellers.size();
    }
}
