package com.futcore.restaurant.models;

public class VCardInfo
{
    public String fName = "";
    public String organization = "";
    public String telephone = "";
    public String telephone2 = "";
    public String country = "";
    public String locality = "";
    public String region = "";
    public String postalCode = "";
    public String streetAddress = "";
    public String email = "";
    public String title = "";
    public String url = "";
    public String note = "";

    public VCardInfo(String fName, String organization,String telephone,String telephone2, String country, String locality,String region,String postalCode, String streetAddress, String email, String title,String url,String note)
    {
        this.fName = fName;
        this.organization = organization;
        this.telephone = telephone;
        this.telephone2 = telephone2;
        this.country = country;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.streetAddress = streetAddress;
        this.email = email;
        this.title = title;
        this.url = url;
        this.note = note;
    }
}
