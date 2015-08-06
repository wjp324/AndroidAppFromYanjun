package com.futcore.restaurant.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.futcore.restaurant.R;

public class BlackTransWarningDialog extends Dialog
{
    private int secCount = 2;
    
    public BlackTransWarningDialog(Context context, String message)
    {
        super(context, R.style.BlackTransProgressDialog);
        setContentView(R.layout.black_trans_warning);
        //        setCanceledOnTouchOutside(false);
        TextView mMessage = (TextView)findViewById(R.id.message);
        mMessage.setText(message);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    //Do something after 100ms
                }
            }, 1000*secCount);        
    }
    
}
