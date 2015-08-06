package com.futcore.restaurant.models;

import java.net.URI;

public class Contact
{
    public int contactId;
    public int companyId;
    public String fName;
    public String title;
    public String cellphone;
    public String landphone;
    public String email;
    public URI thumbUrl;

    public Contact(int contactId, int companyId, String fName, String title, String cellphone, String landphone, String email, URI thumbUrl)
    {
        this.contactId = contactId;
        this.companyId = companyId;
        this.fName = fName;
        this.title = title;
        this.cellphone = cellphone;
        this.landphone = landphone;
        this.email = email;
        this.thumbUrl = thumbUrl;
    }
}
