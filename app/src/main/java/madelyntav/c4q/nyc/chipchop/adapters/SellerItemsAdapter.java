package madelyntav.c4q.nyc.chipchop.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import madelyntav.c4q.nyc.chipchop.DBCallback;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.R;
import madelyntav.c4q.nyc.chipchop.SellActivity;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Seller_CreateItem;
import madelyntav.c4q.nyc.chipchop.fragments.Fragment_Seller_Items;

public class SellerItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DBCallback itemRemovalCallback;
    private List<Item> sellerItems;
    private Context context;
    private int lastPosition = -1;
    private DBHelper dbHelper;
    private SellActivity activity;
    private boolean isActive;
    Fragment_Seller_Items fragment;
    private DBCallback emptyCallback;

    public SellerItemsAdapter(final Context context, Fragment_Seller_Items fragment, final List<Item> sellerItems, boolean isActive) {
        this.context = context;
        this.sellerItems = sellerItems;
        dbHelper = DBHelper.getDbHelper(context);
        activity = (SellActivity) context;
        this.isActive = isActive;
        this.fragment = fragment;
        emptyCallback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };
    }

    private class SellersViewHolder extends RecyclerView.ViewHolder {

        Button removeItemButton;
        CardView container;
        ImageView image;
        TextView name;
        TextView price;
        TextView quantity;
        TextView description;
        SwitchCompat activeSwitch;
        ImageView vegan, glutenFree, dairy, nut, egg, shellFish;

        public SellersViewHolder(View itemView) {
            super(itemView);

            container = (CardView) itemView.findViewById(R.id.card_view);
            image = (ImageView) itemView.findViewById(R.id.food_image);
            name = (TextView) itemView.findViewById(R.id.food_name_tv);
            price = (TextView) itemView.findViewById(R.id.food_price_tv);
            quantity = (TextView) itemView.findViewById(R.id.food_quantity_tv);
            description = (TextView) itemView.findViewById(R.id.food_description_tv);
            activeSwitch = (SwitchCompat) itemView.findViewById(R.id.active_toggle);

            vegan = (ImageView) itemView.findViewById(R.id.vegan);
            glutenFree = (ImageView) itemView.findViewById(R.id.gluten_free);
            dairy = (ImageView) itemView.findViewById(R.id.dairy);
            nut = (ImageView) itemView.findViewById(R.id.nut);
            egg = (ImageView) itemView.findViewById(R.id.egg);
            shellFish = (ImageView) itemView.findViewById(R.id.shellfish);

            itemRemovalCallback = new DBCallback() {
                @Override
                public void runOnSuccess() {
                    Toast.makeText(context,"Item removed successfully", Toast.LENGTH_SHORT).show();
                    Snackbar
                            .make(Fragment_Seller_Items.coordinatorLayoutView, "Item removed successfully", Snackbar.LENGTH_SHORT)
                            .show();

                }

                @Override
                public void runOnFail() {
                    Toast.makeText(context,"Unable to remove item", Toast.LENGTH_SHORT).show();
                    Snackbar
                            .make(Fragment_Seller_Items.coordinatorLayoutView, "Unable to remove item", Snackbar.LENGTH_SHORT)
                            .show();
                }
            };

            removeItemButton = (Button) itemView.findViewById(R.id.remove_item_button);
            removeItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: add popup dialog asking user to confirm deletion
                    if(isActive) {
                        if (activity.isCurrentlyCooking()) {
                            dbHelper.removeItemFromSale(sellerItems.get(getAdapterPosition()), itemRemovalCallback);
                        } else {
                            //TODO: Madelyn: are these the correct methods to remove items from the seller profile in the DB?
                            dbHelper.removeItemFromSellerProfile(sellerItems.get(getAdapterPosition()), itemRemovalCallback);
                            Log.d("ITEMID moved", sellerItems.get(getAdapterPosition()).getItemID() + "");
                        }
                    }

                    sellerItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Item itemToEdit = sellerItems.get(getAdapterPosition());
                    Log.d("ITEMGGG",itemToEdit.getItemID());

                    activity.setItemToEdit(itemToEdit);
                    activity.replaceSellerFragment(new Fragment_Seller_CreateItem());
                }
            });
            
            activeSwitch.setChecked(isActive);
            activeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Item item = sellerItems.get(getAdapterPosition());

                    if(isActive) {
                        if (activity.isCurrentlyCooking()) {
                            dbHelper.removeItemFromSale(sellerItems.get(getAdapterPosition()), emptyCallback);
                        } else {
                            //TODO: Madelyn: are these the correct methods to remove items from the seller profile in the DB?
                            dbHelper.removeItemFromSellerProfile(sellerItems.get(getAdapterPosition()), emptyCallback);
                            Log.d("ITEMID moved", sellerItems.get(getAdapterPosition()).getItemID() + "");
                        }

                        fragment.getActiveItems().remove(getAdapterPosition());
                        notifyDataSetChanged();

                        fragment.getInActiveItems().add(item);
                        fragment.getInactiveList().getAdapter().notifyDataSetChanged();
                    }else{
                        if (activity.isCurrentlyCooking()) {
                            dbHelper.addItemToActiveSellerProfile(sellerItems.get(getAdapterPosition()), emptyCallback);
                        } else {
                            //TODO: Madelyn: are these the correct methods to remove items from the seller profile in the DB?
                            dbHelper.addItemToSellerProfileDB(sellerItems.get(getAdapterPosition()), emptyCallback);
                            Log.d("ITEMID moved", sellerItems.get(getAdapterPosition()).getItemID() + "");
                        }

                        sellerItems.remove(getAdapterPosition());
                        notifyDataSetChanged();

                        fragment.getActiveItems().add(item);
                        fragment.getActiveList().getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selleritem_list_item, parent, false);
        return new SellersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        Item sellerItem = sellerItems.get(position);
        final SellersViewHolder vh = (SellersViewHolder) viewHolder;
        vh.name.setText(sellerItem.getNameOfItem());
        vh.price.setText("$ " + sellerItem.getPrice());
        vh.quantity.setText(sellerItem.getQuantity() + "");
        vh.description.setText(sellerItem.getDescriptionOfItem());
        if(sellerItem.getImageLink() != null && !sellerItem.getImageLink().isEmpty() && sellerItem.getImageLink().length() > 200) {
            final String imageLink = sellerItem.getImageLink();
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

        if(sellerItem.isVegetarian())
            vh.vegan.setVisibility(View.VISIBLE);
        if(sellerItem.isGlutenFree())
            vh.glutenFree.setVisibility(View.VISIBLE);
        if(sellerItem.isContainsDairy())
            vh.dairy.setVisibility(View.VISIBLE);
        if(sellerItem.isContainsPeanuts())
            vh.nut.setVisibility(View.VISIBLE);
        if(sellerItem.isContainsEggs())
            vh.egg.setVisibility(View.VISIBLE);
        if(sellerItem.isContainsShellfish())
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
        return sellerItems.size();
    }
}


