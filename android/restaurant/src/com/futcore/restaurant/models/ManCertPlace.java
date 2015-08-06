package com.futcore.restaurant.models;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManCertPlace implements java.io.Serializable {

	@DatabaseField(canBeNull = false, id = true)
	private Integer placeId;
    //	@DatabaseField(canBeNull = false, foreign = true)
    @DatabaseField(foreign = true)
	private ManUser manUser;
	private Double placeLat;
	private Double placeLng;
	@DatabaseField(canBeNull = false)
	private String placeName;
	private String placeDesc;
	@DatabaseField(canBeNull = false)
	private byte isDelete;

	public ManCertPlace() {
	}

	public ManCertPlace(ManUser manUser, String placeName, byte isDelete) {
		this.manUser = manUser;
		this.placeName = placeName;
		this.isDelete = isDelete;
	}

	public ManCertPlace(ManUser manUser, Double placeLat, Double placeLng,
			String placeName, String placeDesc, byte isDelete) {
		this.manUser = manUser;
		this.placeLat = placeLat;
		this.placeLng = placeLng;
		this.placeName = placeName;
		this.placeDesc = placeDesc;
		this.isDelete = isDelete;
	}

	public Integer getPlaceId() {
		return this.placeId;
	}

	public void setPlaceId(Integer placeId) {
		this.placeId = placeId;
	}

	public ManUser getManUser() {
		return this.manUser;
	}

	public void setManUser(ManUser manUser) {
		this.manUser = manUser;
	}

	public Double getPlaceLat() {
		return this.placeLat;
	}

	public void setPlaceLat(Double placeLat) {
		this.placeLat = placeLat;
	}

	public Double getPlaceLng() {
		return this.placeLng;
	}

	public void setPlaceLng(Double placeLng) {
		this.placeLng = placeLng;
	}

	public String getPlaceName() {
		return this.placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceDesc() {
		return this.placeDesc;
	}

	public void setPlaceDesc(String placeDesc) {
		this.placeDesc = placeDesc;
	}

	public byte getIsDelete() {
		return this.isDelete;
	}

	public void setIsDelete(byte isDelete) {
		this.isDelete = isDelete;
	}

}
