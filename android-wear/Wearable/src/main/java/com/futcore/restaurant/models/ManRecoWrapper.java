package com.futcore.restaurant.models;

//import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DataType;

public class ManRecoWrapper implements java.io.Serializable {
    private static final long serialVersionUID =-55522L;

    public static final String ID_FIELD_NAME = "cTime";

	@DatabaseField(canBeNull = false, id = true)
    private Long cTime;

    //	@DatabaseField(canBeNull = false)
    //    private String seRecos;
	@DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    private HashMap<Long, List<ManItemScore>> seRecos;

    public ManRecoWrapper()
    {
    }

    /*    public ManRecoWrapper(Long cTime, String seRecos)
    {
        this.cTime = cTime;
        this.seRecos = seRecos;
    }
    */

    public ManRecoWrapper(Long cTime, HashMap<Long, List<ManItemScore>> seRecos)
    {
        this.cTime = cTime;
        this.seRecos = seRecos;
    }
    
    /*    public String getSeRecos() {
		return this.seRecos;
	}

	public void setSeRecos(String seRecos) {
		this.seRecos = seRecos;
	}
    */
    
    public HashMap<Long, List<ManItemScore>> getSeRecos() {
		return this.seRecos;
	}

	public void setSeRecos(HashMap<Long, List<ManItemScore>> seRecos) {
		this.seRecos = seRecos;
	}

    public Long getCTime() {
		return this.cTime;
	}

	public void setCTime(Long cTime) {
		this.cTime = cTime;
	}
}

