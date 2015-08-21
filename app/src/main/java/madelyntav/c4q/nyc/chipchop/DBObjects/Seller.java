package madelyntav.c4q.nyc.chipchop.DBObjects;

import java.util.ArrayList;

/**
 * Created by c4q-madelyntavarez on 8/20/15.
 */
public class Seller {

    ArrayList<Item> sellerItems;
    String eMail;
    String name;
    Address address;
    String photoLink;
    String phoneNumber;
    String UID;
    String description;
    String latitude;
    String longitude;
    Boolean isCooking;
    String storeName;
    int numOfReviews;

    public Seller(){}

    public Seller(String UID){
        this.UID=UID;
    }

    public Seller(String UID, String eMail){
        this.UID=UID;
        this.eMail = eMail;
    }
    public Seller(String UID, String eMail, String name, Address address, String phoneNumber){
        this.UID=UID;
        this.eMail = eMail;
        this.name=name;
        this.address=address;
        this.phoneNumber=phoneNumber;
    }

    public Seller(String UID, String eMail, String name, Address address, String photoLink, String phoneNumber){
        this.UID=UID;
        this.eMail = eMail;
        this.name=name;
        this.address=address;
        this.photoLink=photoLink;
        this.phoneNumber=phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ArrayList<Item> getSellerItems() { return sellerItems; }

    public void setSellerItems(ArrayList<Item> sellerItems) { this.sellerItems = sellerItems; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getUID() { return UID; }

    public void setUID(String UID) { this.UID = UID; }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsCooking() {
        return isCooking;
    }

    public void setIsCooking(Boolean isCooking) {
        this.isCooking = isCooking;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getNumOfReviews() {
        return numOfReviews;
    }

    public void setNumOfReviews(int numOfReviews) {
        this.numOfReviews = numOfReviews;
    }

    public void clearUser(){
        eMail =null;
        name=null;
        address=null;
        phoneNumber=null;
        UID=null;
        photoLink=null;
    }
}
