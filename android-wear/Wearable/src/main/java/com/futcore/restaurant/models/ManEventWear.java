package com.futcore.restaurant.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManEventWear implements java.io.Serializable {
    private static final long serialVersionUID =-555L;

    public static final String EST_END_TIME_FIELD_NAME = "estEndTime";
    public static final String UPDATETIME_FIELD_NAME = "updateTime";
    public static final String CREATETIME_FIELD_NAME = "createTime";
    public static final String STARTTIME_FIELD_NAME = "startTime";
    public static final String ID_FIELD_NAME = "eventId";
    public static final String DELETE_FIELD_NAME = "isDelete";

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
	private long createTime;
	@DatabaseField
	private long startTime;
	@DatabaseField
	private long updateTime;
	@DatabaseField
	private long estEndTime;
	@DatabaseField
    private int estDuration;
	@DatabaseField
	private byte isDelete;

	public ManEventWear() {
	}

	public ManEventWear(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, int contDay, long createTime, long estEndTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
		this.contDay = contDay;
		this.createTime = createTime;
        ///
        this.startTime = createTime;
        
        this.estDuration = estDuration;
        this.estEndTime = estEndTime;
		this.isDelete = isDelete;
	}
    

	public ManEventWear(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, int contDay, long createTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
		this.contDay = contDay;
		this.createTime = createTime;
        ///
        this.startTime = createTime;
        
        this.estDuration = estDuration;
		this.isDelete = isDelete;
	}

	public ManEventWear(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, double eventLat, double eventLng, int contDay, long createTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
        this.eventLat = eventLat;
        this.eventLng = eventLng;
		this.contDay = contDay;
		this.createTime = createTime;
        ///
        this.startTime = createTime;
        
        this.estDuration = estDuration;
		this.isDelete = isDelete;
	}

	public ManEventWear(String eventId, ManUser manUser, ManItem manItem, int eventScore, double eventComplete, double eventLat, double eventLng, int contDay, long createTime, long updateTime, long estEndTime, int estDuration, byte isDelete) {
        this.eventId = eventId;
		this.manUser = manUser;
		this.manItem = manItem;
		this.eventScore = eventScore;
        this.eventComplete = eventComplete;
        this.eventLat = eventLat;
        this.eventLng = eventLng;
		this.contDay = contDay;
		this.createTime = createTime;
        ///
        this.startTime = createTime;
        
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

	public long getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
    
	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public long getEstEndTime() {
		return this.estEndTime;
	}

	public void setEstEntTime(long estEndTime) {
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

    public ManEvent toManEvent()
    {
        return new ManEvent
            (
                eventId,
                manUser,
                manItem,
                eventScore,
                eventComplete,
                eventLat,
                eventLng,
                contDay,
                new Date(createTime),
                new Date(startTime),
                new Date(updateTime),
                new Date(estEndTime),
                estDuration,
                isDelete
            );
    }

    public static ManEventWear fromManEvent(ManEvent eve)
    {
        ManEventWear evew1 = new ManEventWear();


        
        //        ManEventWear evew = new ManEventWear
        //            (
        evew1.eventId = eve.getEventId();
        evew1.manUser = eve.getManUser();
        evew1.manItem = eve.getManItem();
        evew1.eventScore = eve.getEventScore();
        evew1.eventComplete = eve.getEventComplete();
        evew1.eventLat = eve.getEventLat();
        evew1.eventLng = eve.getEventLng();
        evew1.contDay = eve.getContDay();
        evew1.createTime = eve.getCreateTime().getTime();
        evew1.startTime = eve.getStartTime().getTime();
        if(eve.getUpdateTime()!=null)
            evew1.updateTime = eve.getUpdateTime().getTime();
        //        else
        //            evew1.updateTime = null;
        evew1.estEndTime = eve.getEstEndTime().getTime();
        evew1.estDuration = eve.getEstDuration();
        evew1.isDelete = eve.getIsDelete();
                    //            );
        
        return evew1;
    }
}
