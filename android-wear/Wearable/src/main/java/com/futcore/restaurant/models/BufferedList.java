package com.futcore.restaurant.models;

import java.util.List;
import java.util.ArrayList;

public class BufferedList
{
    private List<ManEventWear> mList;
    private static final int MAX_LENGTH = 10;
    
    public BufferedList()
    {
        mList = new ArrayList<ManEventWear>();
    }

    public void add(ManEventWear eve)
    {
        mList.add(eve);
        if(mList.size()>=MAX_LENGTH)  // do not support bulk add, so we can do so
            mList.remove(0);
            //            return false;
    }

    public void remove(int index)
    {
        mList.remove(index);
    }

    public List<ManEventWear> getEventList()
    {
        return mList;
    }

    public ManEventWear getEvent(int index)
    {
        return mList.get(index);
    }
}
