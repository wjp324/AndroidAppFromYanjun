package com.futcore.restaurant.models;

import java.net.URI;

public class Item
{
    public int itemId;
    public int shopId;
    public String itemName;
    public Float price;
    public Float mktPrice;
    public String desc;
    public URI thumbUrl = null;

    public Item(int itemId, int shopId, String itemName, Float price, Float mktPrice, String desc, URI thumbUrl)
    {
        this.itemId = itemId;
        this.shopId = shopId;
        this.itemName = itemName;
        this.price = price;
        this.mktPrice = mktPrice;
        this.desc = desc;
        this.thumbUrl = thumbUrl;
    }
}
