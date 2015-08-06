package com.futcore.restaurant.models;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ManUpdateStatus implements java.io.Serializable {
	@DatabaseField
	private Date updateDailyLast;
	@DatabaseField
	private Date downRecoModelLast;

	public Date getUpdateDailyLast() {
		return this.updateDailyLast;
	}

	public void setUpdateDailyLast(Date updateDailyLast) {
		this.updateDailyLast = updateDailyLast;
	}

    public Date getDownRecoModelLast()
    {
        return this.downRecoModelLast;
    }

    public void setDownRecoModelLast(Date downRecoModelLast)
    {
        this.downRecoModelLast = downRecoModelLast;
    }
}

