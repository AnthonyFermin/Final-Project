package madelyntav.c4q.nyc.chipchop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import com.firebase.client.Firebase;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import madelyntav.c4q.nyc.chipchop.DBObjects.Address;
import madelyntav.c4q.nyc.chipchop.DBObjects.DBHelper;
import madelyntav.c4q.nyc.chipchop.DBObjects.Item;
import madelyntav.c4q.nyc.chipchop.DBObjects.Order;
import madelyntav.c4q.nyc.chipchop.DBObjects.Seller;
import madelyntav.c4q.nyc.chipchop.DBObjects.User;

public class MainActivity extends AppCompatActivity {
    Firebase firebaseRef;
    String userID="5";
    String sellerID="101";
    String name="Madey";
    String phoneNumber="646-549-0877";
    String streetAddress="570 West 189 street";
    String apt="5E";
    String city="New York";
    String state="NY";
    String zipCode="10040";
    Item item;
    String email="madey189@aol.com";
    Order order;
    User user;
    Seller seller;
    Address address;
    Address sellerAddress;
    private String sellerName="BRailyn Torres";
    private String sellerPhone="646-777-9087";
    private String sellerStreet="256 Wadsworth Avenue";
    private String sellerApt="2A";
    private String sellerCity="New York";
    private String sellerState="NY";
    private String sellerZipcode="10033";
    private String sellerEmail="JackFinnish@gmail.com";
    private Seller seller2;
    private static DBHelper dbHelper = null;
    private Item item2;
    private Item item3;
    private ArrayList<Item> itemsForSale;
    private ArrayList<User> userList;
    private User user2;
    private DBCallback emptyCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper=DBHelper.getDbHelper(this);
        userList=new ArrayList<>();

        emptyCallback = new DBCallback() {
            @Override
            public void runOnSuccess() {

            }

            @Override
            public void runOnFail() {

            }
        };

        item=new Item();
        order=new Order();
        address=new Address(streetAddress,apt,city,state,zipCode,userID);
        user=new User(userID,email,name,address,phoneNumber);
        sellerAddress= new Address(sellerStreet,sellerApt,sellerCity,sellerState,sellerZipcode,sellerID);
        seller=new Seller(sellerID,sellerEmail,sellerName,sellerAddress,"Madey's Empanadas",sellerPhone);
        seller2=new Seller("21","HYUGFHD@gmail.com","Alvin K",sellerAddress,"jfhdjkfh","212-740-9510");
        item=new Item(sellerID,userID,"Mashed Potatoes",10,"With Butter and Cheese","JFJJFDGHDF");
        item.setQuantityWanted(5);
        item.setItemID("-JxMXJyP0nlWajRkhq3i");
        item2=new Item(sellerID,userID,"Beans",20,"Red Beans","5yhhfuid");
        item2.setItemID("-JxMXJyWCY8AN_LQOOP2");
        item2.setQuantityWanted(20);
        item3= new Item(sellerID,userID,"Flan",50,"Dominican Flan","fvhkzdhfjkbg");
        item3.setQuantityWanted(30);
        itemsForSale=new ArrayList<>();
        user2=new User("900",email,sellerName,address,sellerPhone);
        itemsForSale.add(item);
        itemsForSale.add(item2);
        itemsForSale.add(item3);

        dbHelper.getAllActiveSellers(emptyCallback);

    }


    //Madelyn's Testing Code Below


    public void sendData(View view) {

        dbHelper.addSellerProfileInfoToDB(seller);
        //dbHelper.addUserProfileInfoToDB(user);
        //dbHelper.addSellerProfileInfoToDB(seller);
        //dbHelper.addSellerProfileInfoToDB(seller);
        //dbHelper.addActiveSellerToTable(seller);
        //dbHelper.addItemToActiveSellerTable(item);
        //dbHelper.addItemToSellerProfileDB(item);


        //dbHelper.sendArrayListOfItemsToItemsForSale(itemsForSale,sellerID);
//        Order order= new Order("5",itemsForSale,"101");
//        order.setOrderID("-JxMPmoZCm8rUg8G_UEp");
//        Review review= new Review("101","5",4);
//        order.setReview(review);
//        dbHelper.sendReviewedOrderToSellerDB(order);

        //dbHelper.addCurrentOrderToSellerDB(order);
    }
        //itemsForSale.addAll(dbHelper.getSellersOnSaleItems("101"));
    //    dbHelper.sendArrayListOfItemsToItemsForSale(itemsForSale,sellerID);
        //dbHelper.removeSellersFromActiveSellers(seller);

        //dbHelper.removeItemFromSale(item.getItemID(),item.getSellerID());

//        dbHelper.addCurrentOrderToSellerDB(order);

    public void getData(View v){
       // dbHelper.getSellerFromDB("101");
       // dbHelper.getSpecificActiveSeller(sellerID);
       // dbHelper.getSellerFromDB("101");
        //dbHelper.getUserFromDB("5");
        //activeSellerList.addAll(dbHelper.getAllActiveSellers());
        //dbHelper.getSpecificActiveSeller("21");
        //dbHelper.sendSellerToActiveSellerTable(seller);
        //Order order= new Order(user.getUID(),itemsForSale,sellerID);
        //dbHelper.addCurrentOrderToSellerDB(order);

        //itemsForSale.clear();

        //itemsForSale.addAll(dbHelper.getSellersOnSaleItems(sellerID));
//        userList.addAll(dbHelper.getArrayListOfUsers());
//        Log.d("SUSERSItemsMain", userList.toString());


           // dbHelper.updateSellerItemsWhenItemIsBought(item2);
           // dbHelper.updateSellerItemsWhenItemIsBought(item2, emptyCallback);

    }

   public void saveImageToEncodedString(String fileName){
       InputStream inputStream = null;
       try {
           inputStream = new FileInputStream(fileName);
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       byte[] bytes;
       byte[] buffer = new byte[8192];
       int bytesRead;
       ByteArrayOutputStream output = new ByteArrayOutputStream();
       try {
           while ((bytesRead = inputStream.read(buffer)) != -1) {
               output.write(buffer, 0, bytesRead);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       bytes = output.toByteArray();
       String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
   }

}
