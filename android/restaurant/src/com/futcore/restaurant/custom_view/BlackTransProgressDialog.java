package com.futcore.restaurant.custom_view;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.futcore.restaurant.R;

public class BlackTransProgressDialog extends Dialog {
    
    /*    public static BlackTransProgressDialog show(Context context, CharSequence title,
            CharSequence message) {
        return show(context, title, message, false);
    }

    public static BlackTransProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static BlackTransProgressDialog show(Context context, CharSequence title,
            CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static BlackTransProgressDialog show(Context context, CharSequence title, CharSequence message, boolean indeterminate, boolean cancelable, OnCancelListener cancelListener) {
        BlackTransProgressDialog dialog = new BlackTransProgressDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        //        dialog.setMessage(message);
        //        dialog.addContentView(new ProgressBar(context), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        //        TextView mMessage = new TextView(context);
        //        mMessage.setText(message);
        //        dialog.addContentView(mMessage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LinearLayout dCon = new LinearLayout(context);
        
        dialog.setContentView(R.layout.black_trans_dialog);
        TextView mMessage = (TextView)findViewById(R.id.message);
        mMessage.setText(message);
        
        dialog.show();
        return dialog;
    }
    */

    public BlackTransProgressDialog(Context context, String message) {
        super(context, R.style.BlackTransProgressDialog);
        setContentView(R.layout.black_trans_dialog);
        setCanceledOnTouchOutside(false);
        TextView mMessage = (TextView)findViewById(R.id.message);
        ProgressBar mPb = (ProgressBar)findViewById(R.id.pb);
        mPb.setIndeterminate(true);
        mMessage.setText(message);
    }
}
