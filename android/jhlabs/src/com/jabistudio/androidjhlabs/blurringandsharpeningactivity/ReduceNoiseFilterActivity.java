package com.jabistudio.androidjhlabs.blurringandsharpeningactivity;

import com.jabistudio.androidjhlabs.R;
import com.jabistudio.androidjhlabs.SuperFilterActivity;
import com.jabistudio.androidjhlabs.filter.DespeckleFilter;
import com.jabistudio.androidjhlabs.filter.ReduceNoiseFilter;
import com.jabistudio.androidjhlabs.filter.util.AndroidUtils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReduceNoiseFilterActivity extends SuperFilterActivity implements OnClickListener{
    private static final String TITLE = "ReduceNoise";
    
    private static final String APPLY_STRING = "Apply:";
    private int mApplyCount = 0;
    
    private TextView mApplyText;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TITLE);
        //noise image
        mOriginalImageView.setImageResource(R.drawable.noiseimage);
        mModifyImageView.setImageResource(R.drawable.noiseimage);
        filterButtonSetup(mMainLayout);
    }
    /**
     * filterButtonSetting
     * @param mainLayout
     */
    private void filterButtonSetup(LinearLayout mainLayout){
        mApplyText = new TextView(this);
        mApplyText.setText(APPLY_STRING + mApplyCount);
        mApplyText.setTextSize(TITLE_TEXT_SIZE);
        mApplyText.setTextColor(Color.BLACK);
        mApplyText.setGravity(Gravity.CENTER);
        
        Button button = new Button(this);
        button.setText(APPLY_STRING);
        button.setOnClickListener(this);
         
        mainLayout.addView(mApplyText);
        mainLayout.addView(button);
    }
    @Override
    public void onClick(View view) {
        final int width = mModifyImageView.getDrawable().getIntrinsicWidth();
        final int height = mModifyImageView.getDrawable().getIntrinsicHeight();
        
        int[] colors = AndroidUtils.drawableToIntArray(mModifyImageView.getDrawable());
        
        ReduceNoiseFilter filter = new ReduceNoiseFilter();
        
        colors = filter.filter(colors, width, height);
        
        setModifyView(colors, width, height);
        
        mApplyCount++;
        mApplyText.setText(APPLY_STRING + mApplyCount);
    }
}
