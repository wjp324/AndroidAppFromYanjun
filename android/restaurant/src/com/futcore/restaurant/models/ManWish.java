package com.futcore.restaurant.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManWish implements  java.io.Serializable 
{

    public static final String CREATETIME_FIELD_NAME = "createTime";
    public static final String DELETE_FIELD_NAME = "isDelete";
    
	@DatabaseField(canBeNull = false, id = true)
	private Integer wishId;
	@DatabaseField
	private String wishName;
	@DatabaseField
	private Date createTime;
	@DatabaseField
	private Date expireTime;
	@DatabaseField
	private byte isDelete;

    public ManWish()
    {
    }

    public ManWish(String wishName, Date createTime, byte isDelete)
    {
        this.wishName = wishName;
        this.createTime = createTime;
        this.isDelete = isDelete;
    }

    public ManWish(Integer wishId, String wishName, Date createTime, Date expireTime, byte isDelete)
    {
        this.wishId = wishId;
        this.wishName = wishName;
        this.createTime = createTime;
        this.expireTime = expireTime;
        this.isDelete = isDelete;
    }

	public Integer getWishId() {
		return this.wishId;
	}

	public void setWishId(Integer wishId) {
		this.wishId = wishId;
	}


	public String getWishName() {
		return this.wishName;
	}

	public void setWishName(String wishName) {
		this.wishName = wishName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getExpireTime() {
		return this.expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public byte getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(byte isDelete) {
		this.isDelete = isDelete;
	}
}
