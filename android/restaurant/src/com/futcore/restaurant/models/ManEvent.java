package com.futcore.restaurant.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManEvent implements java.io.Serializable {
    private static final long serialVersionUID =-333L;

    public static final String EST_END_TIME_FIELD_NAME = "estEndTime";
    public static final String UPDATETIME_FIELD_NAME = "updateTime";
    public static final String ID_FIELD_NAME = "eventId";

    public static final String DELETE_FIELD_NAME = "isDelete";

    

    public static final byte DELETE_TYPE_PLAN = 10;
    public static final byte DELETE_TYPE_RECO = 11;
    public static final byte DELETE_TYPE_PERMANENT = 12;
    

    //	@DatabaseField(canBeNull = false, generatedId = true)
	@DatabaseField(canBeNull = false, id = true)
	//private Integer eventId;
    private String eventId;
    //	@DatabaseField(canBeNull = false, foreign = true)
	@DatabaseField(foreign = true)
	private ManUser manUser;
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh=true)
	private ManItem manItem;
	@DatabaseField
	private int eventScore;
	@DatabaseField
    private double eventComplete;
	@DatabaseField
    private double eventLat;
	@DatabaseField
    private double eventLng;
	@DatabaseField
	private int contDay;
	@DatabaseField
	private Date createTime;
	@DatabaseField
	private Date startTime;
	@DatabaseField
	private Date updateTime;
	@DatabaseField
	private Date estEndTime;
	@DatabaseField
    private int estDuration;
	@DatabaseField
	private byte isDelete;

	public ManEvent() {
	}

	public ManEvent(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, int contDay, Date createTime, Date estEndTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
		this.contDay = contDay;
		this.createTime = createTime;

        //set set
        this.startTime = createTime;
        
        this.estDuration = estDuration;
        this.estEndTime = estEndTime;
		this.isDelete = isDelete;
	}

	public ManEvent(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, int contDay, Date createTime, Date startTime, Date estEndTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
		this.contDay = contDay;
		this.createTime = createTime;

        //set set
        this.startTime = startTime;
        
        this.estDuration = estDuration;
        this.estEndTime = estEndTime;
		this.isDelete = isDelete;
	}
    
    

	public ManEvent(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, int contDay, Date createTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
		this.contDay = contDay;
		this.createTime = createTime;
        this.estDuration = estDuration;
		this.isDelete = isDelete;
	}

	public ManEvent(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, double eventLat, double eventLng, int contDay, Date createTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
        this.eventLat = eventLat;
        this.eventLng = eventLng;
		this.contDay = contDay;
		this.createTime = createTime;
        this.estDuration = estDuration;
		this.isDelete = isDelete;
	}

	public ManEvent(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, double eventLat, double eventLng, int contDay, Date createTime, Date updateTime, Date estEndTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
        this.eventLat = eventLat;
        this.eventLng = eventLng;
		this.contDay = contDay;
		this.createTime = createTime;
		this.updateTime = updateTime;
        this.estEndTime = estEndTime;
        this.estDuration = estDuration;
		this.isDelete = isDelete;
	}

    /*	public Integer getEventId() {
		return this.eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
    */
    
    public String getEventId() {
		return this.eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

    public ManUser getManUser() {
		return this.manUser;
	}

	public void setManUser(ManUser manUser) {
		this.manUser = manUser;
	}

	public ManItem getManItem() {
		return this.manItem;
	}

	public void setManItem(ManItem manItem) {
		this.manItem = manItem;
	}

	public int getEventScore() {
		return this.eventScore;
	}

	public void setEventScore(int eventScore) {
		this.eventScore = eventScore;
	}

	public double getEventComplete() {
		return this.eventComplete;
	}

	public void setEventComplete(double eventComplete) {
		this.eventComplete = eventComplete;
	}

	public double getEventLat() {
		return this.eventLat;
	}

	public void setEventLat(double eventLat) {
		this.eventLat = eventLat;
	}

	public double getEventLng() {
		return this.eventLng;
	}

	public void setEventLng(double eventLng) {
		this.eventLng = eventLng;
	}

	public int getContDay() {
		return this.contDay;
	}

	public void setContDay(int contDay) {
		this.contDay = contDay;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getEstEndTime() {
		return this.estEndTime;
	}

	public void setEstEntTime(Date estEndTime) {
		this.estEndTime = estEndTime;
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

}
