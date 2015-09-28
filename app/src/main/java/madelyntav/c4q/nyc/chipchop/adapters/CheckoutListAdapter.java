package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.R;

public class CheckoutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Item> checkoutItems;
        private Context context;
        private int lastPosition = -1;

        public CheckoutListAdapter(Context context, List<Item> checkoutItems) {
            this.context = context;
            this.checkoutItems = checkoutItems;
        }

        private class CheckoutViewHolder extends RecyclerView.ViewHolder {

            LinearLayout container;
            ImageView image;
            TextView name;
            TextView price;
            TextView quantity;


            public CheckoutViewHolder(View itemView) {
                super(itemView);

                container = (LinearLayout) itemView.findViewById(R.id.container);
                image = (ImageView) itemView.findViewById(R.id.food_image);
                name = (TextView) itemView.findViewById(R.id.food_name_tv);
                price = (TextView) itemView.findViewById(R.id.food_price_tv);
                quantity = (TextView) itemView.findViewById(R.id.food_quantity_tv);

            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_list_item, parent, false);
            return new CheckoutViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

            Item checkoutItem = checkoutItems.get(position);
            final CheckoutViewHolder vh = (CheckoutViewHolder) viewHolder;
            vh.name.setText(checkoutItem.getNameOfItem());
            vh.price.setText("$ " + checkoutItem.getPrice() + "");
            vh.quantity.setText(checkoutItem.getQuantityWanted() + "");
            if(checkoutItem.getImageLink() != null && !checkoutItem.getImageLink().isEmpty() && checkoutItem.getImageLink().length() > 200) {
                final String imageLink = checkoutItem.getImageLink();
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        byte[] decoded = org.apache.commons.codec.binary.Base64.decodeBase64(imageLink.getBytes());
                        return BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        vh.image.setImageBitmap(bitmap);
                    }
                }.execute();
            }

            setAnimation(vh.container, position);

        }

    private void setAnimation(View viewToAnimate, int position){
        // only animates the view if it was not already displayed on the screen
        if(position > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom); //can make a custom animation here
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
        @Override
        public int getItemCount() {
            return checkoutItems.size();
        }
    }




