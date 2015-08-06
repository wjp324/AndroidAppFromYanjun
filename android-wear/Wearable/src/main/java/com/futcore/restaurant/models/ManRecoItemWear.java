package com.futcore.restaurant.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DatabaseField;

public class ManRecoItemWear implements java.io.Serializable {

	@DatabaseField(canBeNull = false, id = true)
    private Integer recoId;
    //	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh=true)
	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh=true)
	private ManItem manItem;
	@DatabaseField
    private String modelConfig;
	@DatabaseField
    private int recoDuration;
	@DatabaseField
    //	private Date recoDate;
	private long recoDate;
        
	public ManRecoItemWear(Integer recoId, ManItem manItem, String modelConfig, int recoDuration, long recoDate) {
        this.recoId = recoId;
        this.manItem = manItem;
        this.modelConfig = modelConfig;
        this.recoDuration = recoDuration;
        this.recoDate = recoDate;
	}

	public ManRecoItemWear() {
        
	}

    public Integer getRecoId()
    {
        return this.recoId;
    }

    public void setRecoId(Integer recoId)
    {
        this.recoId = recoId;
    }

	public ManItem getManItem() {
		return this.manItem;
	}

	public void setManItem(ManItem manItem) {
		this.manItem = manItem;
	}

    public String getModelConfig()
    {
        return this.modelConfig;
    }

    public void setModelConfig(String modelConfig)
    {
        this.modelConfig = modelConfig;
    }

    public int getRecoDuration()
    {
        return this.recoDuration;
    }

    public void setRecoDuration(int recoDuration)
    {
        this.recoDuration = recoDuration;
    }

	public long getRecoDate() {
		return this.recoDate;
	}

	public void setRecoDate(long recoDate) {
        this.recoDate = recoDate;
	}

    public ManRecoItem toManRecoItem()
    {
        return new ManRecoItem
            (
                recoId,
                manItem,
                modelConfig,
                recoDuration,
                new Date(recoDate)
            );
    }

    public static ManRecoItemWear fromManRecoItem(ManRecoItem ri)
    {
        ManRecoItemWear riw = new ManRecoItemWear();
        riw.recoId = ri.getRecoId();
        riw.manItem = ri.getManItem();
        riw.modelConfig = ri.getModelConfig();
        riw.recoDuration = ri.getRecoDuration();
        riw.recoDate = ri.getRecoDate().getTime();

        return riw;
    }
}

