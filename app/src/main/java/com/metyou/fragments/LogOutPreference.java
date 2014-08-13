package com.metyou.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.metyou.LoginActivity;
import com.metyou.R;

/**
 * Created by mihai on 8/13/14.
 */
public class LogOutPreference extends DialogPreference {

    public LogOutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText(R.string.log_out);
        setNegativeButtonText(android.R.string.cancel);
        setDialogMessage(R.string.log_out_message);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            // start login activity and log out
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setAction(LoginActivity.LOG_OUT_ACTION);
            getContext().startActivity(intent);
            ((Activity) getContext()).finish();
        }
    }
}
