package com.futcore.restaurant.models;
 
public class MusicSection {
    String sectid;
    long startsec;
    long endsec;
    long musicid;
 
    public MusicSection() {
    }

    public MusicSection(String sectid, long startsec, long endsec, long musicid)
    {
        this.sectid = sectid;
        this.startsec = startsec;
        this.endsec = endsec;
        this.musicid = musicid;
    }
 
    public String getSectid() {
        return this.sectid;
    }
 
    public void setSectid(String value) {
        this.sectid = value;
    }
 
    public long getStartsec() {
        return this.startsec;
    }
 
    public void setStartsec(long value) {
        this.startsec = value;
    }
 
    public long getEndsec() {
        return this.endsec;
    }
 
    public void setEndsec(long value) {
        this.endsec = value;
    }
 
    public long getMusicid() {
        return this.musicid;
    }
 
    public void setMusicid(long value) {
        this.musicid = value;
    }
}
