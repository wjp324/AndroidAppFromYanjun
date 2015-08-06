package com.futcore.restaurant.models;

public class Member
{
    public String username;
    public String thumb;
    public String selfDesc;
    public byte gender;
    
    public Member(String username, String thumb, String selfDesc, byte gender)
    {
        this.username = username;
        this.thumb = thumb;
        this.selfDesc = selfDesc;
        this.gender = gender;
    }
}
