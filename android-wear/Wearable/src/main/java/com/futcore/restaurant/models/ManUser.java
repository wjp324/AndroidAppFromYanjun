package com.futcore.restaurant.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManUser implements  java.io.Serializable 
{
	@DatabaseField(canBeNull = false, id = true)
	private Integer userId;
	@DatabaseField
	private String username;
	@DatabaseField
	private String password;
	@DatabaseField
	private Date createTime;
	@DatabaseField
	private Date updateTime;
	@DatabaseField
	private Date lastLogin;
	@DatabaseField
	private String lastIp;
	@DatabaseField
	private byte isDelete;

	public ManUser() {
	}

	public ManUser(String username, Date createTime, byte isDelete) {
		this.username = username;
		this.createTime = createTime;
		this.isDelete = isDelete;
	}

	public ManUser(Integer userId, String username, String password, Date createTime, Date updateTime, Date lastLogin, String lastIp, byte isDelete) {
        this.userId = userId;
		this.username = username;
		this.password = password;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.lastLogin = lastLogin;
		this.lastIp = lastIp;
		this.isDelete = isDelete;
	}

	public ManUser(String username, String password, Date createTime,
			Date updateTime, Date lastLogin, String lastIp, byte isDelete) {
		this.username = username;
		this.password = password;
		this.createTime = createTime;
		this.updateTime = updateTime;
		this.lastLogin = lastLogin;
		this.lastIp = lastIp;
		this.isDelete = isDelete;
	}
    

	public Integer getUserId() {
		return this.userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public Date getLastLogin() {
		return this.lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getLastIp() {
		return this.lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public byte getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(byte isDelete) {
		this.isDelete = isDelete;
	}
}
