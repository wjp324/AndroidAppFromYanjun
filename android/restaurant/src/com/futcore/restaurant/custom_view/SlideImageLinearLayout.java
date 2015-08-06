package com.futcore.restaurant.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.futcore.restaurant.R;

public class SlideImageLinearLayout extends LinearLayout
{
    private Context myContext;

    public SlideImageLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //        super.onMeasure(widthMeasureSpec, heightMeasureSpec+((int)myContext.getResources().getDimension(R.dimen.quickplay_offset)));
        super.onMeasure(widthMeasureSpec+Math.abs((int)myContext.getResources().getDimension(R.dimen.quickplay_offset)*2), heightMeasureSpec);
    }
}
