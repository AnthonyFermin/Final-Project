package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import madelyntav.c4q.nyc.chipchop.BuyActivity;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.FoodItemSelectDialog;
import madelyntav.c4q.nyc.chipchop.R;

public class CartListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Button removeItemButton;
    private ArrayList<Item> cartItems;
    private Context context;
    private int lastPosition = -1;
    BuyActivity activity;

    public CartListAdapter(Context context, ArrayList<Item> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        activity = (BuyActivity) context;
    }


    private class CheckoutViewHolder extends RecyclerView.ViewHolder {

        CardView container;
        ImageView image;
        TextView name;
        TextView price;
        TextView quantity;
        TextView description;

        ImageView vegan, glutenFree, dairy, nut, egg, shellFish;


        public CheckoutViewHolder(View itemView) {
            super(itemView);

            container = (CardView) itemView.findViewById(R.id.card_view);
            image = (ImageView) itemView.findViewById(R.id.food_image);
            name = (TextView) itemView.findViewById(R.id.food_name_tv);
            price = (TextView) itemView.findViewById(R.id.food_price_tv);
            quantity = (TextView) itemView.findViewById(R.id.food_quantity_tv);
            description = (TextView) itemView.findViewById(R.id.food_description_tv);

            vegan = (ImageView) itemView.findViewById(R.id.vegan);
            glutenFree = (ImageView) itemView.findViewById(R.id.gluten_free);
            dairy = (ImageView) itemView.findViewById(R.id.dairy);
            nut = (ImageView) itemView.findViewById(R.id.nut);
            egg = (ImageView) itemView.findViewById(R.id.egg);
            shellFish = (ImageView) itemView.findViewById(R.id.shellfish);

            removeItemButton = (Button) itemView.findViewById(R.id.remove_item_button);
            removeItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    //order code unnecessary
                    Order order = activity.getCurrentOrder();
                    order.setItemsOrdered(cartItems);
                    activity.setCurrentOrder(order);
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);
        return new CheckoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        Item checkoutItem = cartItems.get(position);
        final CheckoutViewHolder vh = (CheckoutViewHolder) viewHolder;
        int amtWanted = checkoutItem.getQuantityWanted();

        vh.name.setText(checkoutItem.getNameOfItem());
        vh.price.setText("$ " + (checkoutItem.getPrice() * amtWanted) + "");
        vh.quantity.setText(checkoutItem.getQuantityWanted() + "");
        vh.description.setText(checkoutItem.getDescriptionOfItem());
        if(checkoutItem.getImageLink() != null && !checkoutItem.getImageLink().isEmpty() && checkoutItem.getImageLink().length() > 200) {
            final String imageLink = checkoutItem.getImageLink();
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
                }
            }.execute();
        }

        if(checkoutItem.isVegetarian())
            vh.vegan.setVisibility(View.VISIBLE);
        if(checkoutItem.isGlutenFree())
            vh.glutenFree.setVisibility(View.VISIBLE);
        if(checkoutItem.isContainsDairy())
            vh.dairy.setVisibility(View.VISIBLE);
        if(checkoutItem.isContainsPeanuts())
            vh.nut.setVisibility(View.VISIBLE);
        if(checkoutItem.isContainsEggs())
            vh.egg.setVisibility(View.VISIBLE);
        if(checkoutItem.isContainsShellfish())
            vh.shellFish.setVisibility(View.VISIBLE);

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
        return cartItems.size();
    }
}


