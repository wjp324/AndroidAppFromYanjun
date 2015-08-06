//package com.ebay.bis.heatmap20.persistence.domain;
package com.futcore.restaurant.models;

public class ManItemScore implements java.io.Serializable 
{
    private ManItem manItem;
    private int score;
    private long time;

    private long estDurSec = 0l;

    public ManItemScore(ManItem manItem, int score, long time)
    {
        this.manItem =  manItem;
        this.score = score;
        this.time = time;
    }

    public ManItemScore(ManItem manItem, int score, long time, long estDurSec)
    {
        this.manItem =  manItem;
        this.score = score;
        this.time = time;
        this.estDurSec = estDurSec;
    }
    
    
    public ManItem getManItem() {
        return this.manItem;
    }
 
    public void setManItem(ManItem value) {
        this.manItem = value;
    }
 
    public int getScore() {
        return this.score;
    }
 
    public void setScore(int value) {
        this.score = value;
    }
 
    public long getTime() {
        return this.time;
    }
 
    public void setTime(long value) {
        this.time = value;
    }

    public long getEstDurSec() {
        return this.estDurSec;
    }
 
    public void setEstDurSec(long value) {
        this.estDurSec = value;
    }




    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        
        ManItemScore item = (ManItemScore)other;
        return (getManItem().getItemId()).equals(item.getManItem().getItemId());
    }
    
    @Override
    public int hashCode() {
        //        return itemId.hashCode();
        //        return (getManItem().getItemId()+"+"+time).hashCode();
        return (getManItem().getItemId()).hashCode();
        //        return x ^ y;
    }    
    
}

