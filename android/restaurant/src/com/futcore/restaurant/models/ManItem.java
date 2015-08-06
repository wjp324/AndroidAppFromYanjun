package com.futcore.restaurant.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManItem  implements  java.io.Serializable 
{
    public static final String ID_FIELD_NAME = "itemId";
    
    //	@DatabaseField(canBeNull = false, id = true)
    //	@DatabaseField(canBeNull = false, generatedId = true)
	@DatabaseField(canBeNull = false, id = true)
    //	private Integer itemId;
    private String itemId;
    //	@DatabaseField(canBeNull = false, foreign = true)
 	@DatabaseField(foreign = true)
	private ManUser manUser;
	@DatabaseField
	private String itemName;
	@DatabaseField
	private int itemScore;
	@DatabaseField
	private String itemCrit;
	@DatabaseField
	private String itemDesc;
	@DatabaseField
	private Date createTime;
	@DatabaseField
	private Date updateTime;
	@DatabaseField
    private int estDuration;
	@DatabaseField
	private byte isDelete;
	@DatabaseField
	private byte isSpecial;

	public ManItem() {
	}

	public ManItem(String itemId, ManUser manUser, String itemName, int itemScore,
                   Date createTime, int estDuration, byte isDelete, byte isSpecial) {
        this.itemId = itemId;
		this.manUser = manUser;
		this.itemName = itemName;
		this.itemScore = itemScore;
		this.createTime = createTime;
        this.estDuration = estDuration;
		this.isDelete = isDelete;
		this.isSpecial = isSpecial;
	}

	public ManItem(String itemId, ManUser manUser, String itemName, String itemCrit, int itemScore,
                   Date createTime, int estDuration, byte isDelete, byte isSpecial) {
        this.itemId = itemId;
		this.manUser = manUser;
		this.itemName = itemName;
        this.itemCrit = itemCrit;
		this.itemScore = itemScore;
		this.createTime = createTime;
        this.estDuration = estDuration;
		this.isDelete = isDelete;
		this.isSpecial = isSpecial;
	}
    

	public ManItem(String itemId, ManUser manUser, String itemName, int itemScore, String itemCrit,
                   String itemDesc, Date createTime, Date updateTime, int estDuration, byte isDelete, byte isSpecial) {
        this.itemId = itemId;
		this.manUser = manUser;
		this.itemName = itemName;
		this.itemScore = itemScore;
        this.itemCrit = itemCrit;
		this.itemDesc = itemDesc;
		this.createTime = createTime;
		this.updateTime = updateTime;
        this.estDuration = estDuration;
		this.isDelete = isDelete;
		this.isSpecial = isSpecial;
	}

    /*	public Integer getItemId() {
		return this.itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
    */


	public String getItemId() {
		return this.itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public ManUser getManUser() {
		return this.manUser;
	}

	public void setManUser(ManUser manUser) {
		this.manUser = manUser;
	}

	public String getItemName() {
		return this.itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getItemScore() {
		return this.itemScore;
	}

	public void setItemScore(int itemScore) {
		this.itemScore = itemScore;
	}

	public String getItemCrit() {
		return this.itemCrit;
	}

	public void setItemCrit(String itemCrit) {
		this.itemCrit = itemCrit;
	}

	public String getItemDesc() {
		return this.itemDesc;
	}

	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public int getEstDuration() {
		return this.estDuration;
	}

	public void setEstDuration(int estDuration) {
		this.estDuration = estDuration;
	}

	public byte getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(byte isDelete) {
		this.isDelete = isDelete;
	}

	public byte getIsSpecial() {
		return this.isSpecial;
	}

	public void setIsSpecial(byte isSpecial) {
		this.isSpecial = isSpecial;
	}

    /*    public static ManItem fromJsonObj(JSONObject obj)
    {
        ManItem item = new ManItem();
        item.itemId = obj.get("itemId");
        item.manUser = null;
        item.itemName = obj.get("itemName");
        item.itemScore = obj.get("itemScore");
        item.itemDesc = obj.get("");
        
            
	private Integer itemId;
	private ManUser manUser;
	private String itemName;
	private int itemScore;
	private String itemDesc;
	private Date createTime;
	private Date updateTime;
    private int estDuration;
	private byte isDelete;
        
        
        return item;
    }
    */


    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        ManItem item = (ManItem)other;
        return itemId.equals(item.itemId);
        /*        return (
                x == point.x &&
                y == point.y &&
                (name == point.name || 
                 (name != null && name.equals(point.name)))
                );
        */
    }
    
    @Override
    public int hashCode() {
        return itemId.hashCode();
        //        return x ^ y;
    }    

    
}
