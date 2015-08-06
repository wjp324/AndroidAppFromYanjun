package com.futcore.restaurant.models;

import java.net.URI;

public class Promo{
    //    public String postID = "";
    public int promoId;
    public String promoName = "";
    public URI thumbUrl = null;
    public String message = "";
    public String startTime = "";
    public String endTime = "";
    public int downloadCount;
    
    //    public int position;
    /*    public String name = "";
          public String emailURL = "";
          public String status = "";
          public String comment = "";
          public String postTitle = "";
          public String authorURL = "";
          public String authorEmail = "";
          public String dateCreatedFormatted = "";
          public URI profileImageUrl = null;
    */

    public Promo(int promoId, String promoName, URI thumbUrl, String message, String startTime, String endTime, int downloadCount) {
        this.promoId = promoId;
        this.promoName = promoName;
        this.thumbUrl = thumbUrl;
        this.message = message;
        this.startTime = startTime;
        this.endTime = endTime;
        this.downloadCount = downloadCount;
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

}
