package com.futcore.restaurant.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Shop implements Parcelable
{
    public int shopId;
    public String shopName = "";
    //    public String shopTags = "";
    public HashMap<Integer, String> shopTags = new HashMap<Integer, String>();
    public int average;
    public int distance;
    public double rating;
    public double taste;
    public double environment;
    public double service;
    public String address;
    public String contact;
    //    public URI thumbUrl = null;
    public String thumbUrl = "";
    public double lat;
    public double lng;

    public Shop(int shopId, String shopName, HashMap<Integer,String> shopTags, int average, int distance, double rating, double taste, double environment, double service, String address, String contact, String thumbUrl, double lat, double lng) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopTags = shopTags;
        this.average = average;
        this.distance = distance;
        this.rating = rating;
        this.taste = taste;
        this.environment = environment;
        this.service = service;
        this.address = address;
        this.contact = contact;
        this.thumbUrl = thumbUrl;
        this.lat = lat;
        this.lng = lng;
        /*        this.postID = postID;
                  this.commentID = commentID;
                  this.position = position;
                  this.name = name;
                  this.emailURL = authorEmail;
                  this.status = status;
                  this.comment = comment;
                  this.postTitle = postTitle;
                  this.authorURL = authorURL;
                  this.authorEmail = authorEmail;
                  this.profileImageUrl = profileImageUrl;
                  this.dateCreatedFormatted = dateCreatedFormatted;
        */
    }

    public String getShopTagsString(String sep)
    {
        String shopTagsString = "";
        Iterator<Entry<Integer, String>> it = shopTags.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            shopTagsString += pairs.getValue().toString().trim().replace("　","")+sep;
            
            //            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            //            it.remove(); // avoids a ConcurrentModificationException
        }
        return shopTagsString;
    }

    public String getShopTagsString()
    {
        return getShopTagsString(" ");
    }


    // Parcelling part
    public Shop(Parcel in){
        //        String[] data = new String[3];

        //        in.readStringArray(data);
        //        this.id = data[0];
        //        this.name = data[1];
        //        this.grade = data[2];
        
        
        this.shopId = in.readInt();
        this.shopName = in.readString();
        Bundle shopTagsB = in.readBundle();
        this.shopTags = (HashMap<Integer, String>) shopTagsB.getSerializable("shopTags");        
        this.average = in.readInt();
        this.distance = in.readInt();
        this.rating = in.readDouble();
        this.taste = in.readDouble();
        this.environment = in.readDouble();
        this.service = in.readDouble();
        this.address = in.readString();
        this.contact = in.readString();
        this.thumbUrl = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        /*        dest.writeStringArray(new String[] {this.shopId,
                                            this.shopName,
                                            this.shopTags,
                                            this.average,
                                            this.distance,
                                            this.rating,
                                            this.taste,
                                            this.environment,
                                            this.service,
                                            this.address,
                                            this.contact,
                                            this.thumbUrl,
                                            this.lat,
                                            this.lng});
        */
        Bundle shopTagsB = new Bundle();
        shopTagsB.putSerializable("shopTags", shopTags);
        
        dest.writeInt(shopId);
        dest.writeString(shopName);
        //        dest.writeMap(shopTags);
        dest.writeBundle(shopTagsB);
        dest.writeInt(average);
        dest.writeInt(distance);
        dest.writeDouble(rating);
        dest.writeDouble(taste);
        dest.writeDouble(environment);
        dest.writeDouble(service);
        dest.writeString(address);
        dest.writeString(contact);
        dest.writeString(thumbUrl);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
    
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Shop createFromParcel(Parcel in) {
                return new Shop(in); 
            }

            public Shop[] newArray(int size) {
                return new Shop[size];
            }
        };    
}

