package madelyntav.c4q.nyc.chipchop.DBObjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    String streetAddress;
    String apartment;
    String city;
    String state;
    String zipCode;
    String userID;
    double latitude;
    double longitude;
    String name;

    public Address(){}

    public Address(String UID){
        userID=UID;
    }

    public Address(String streetAddress, String apartment, String city, String state, String zipCode,String userID){
        this.streetAddress=streetAddress;
        this.apartment=apartment;
        this.city=city;
        this.state=state;
        this.zipCode=zipCode;
        this.userID=userID;

    }

    public Address(String streetAddress, String apartment, String city, String state, String userID){
        this.streetAddress=streetAddress;
        this.apartment=apartment;
        this.city=city;
        this.state=state;
        this.userID=userID;

    }

    public Address(String streetAddress, String apartment, String city, String state, String zipCode,String userID, String name, double latitude, double longitude){
        this.streetAddress=streetAddress;
        this.apartment=apartment;
        this.city=city;
        this.state=state;
        this.name = name;
        this.zipCode=zipCode;
        this.userID=userID;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }



    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void clearAddress(){
        streetAddress=null;
        apartment=null;
        city=null;
        state=null;
        zipCode=null;
    }

    @Override
    public String toString() {
        return streetAddress+", " + apartment+", "+city+", "+state.toUpperCase()+" "+zipCode;
    }
}
