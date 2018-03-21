package com.example.sabri.petsproject3;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.util.AttributeSet;

/**
 * Created by jaydo on 3/20/2018.
 */

public class WebImageView_KP extends WebImageView {

    public MainActivity myActivity;
    public WebImageView_KP(Context context, AttributeSet set) {
        super(context,set);
    }
    public WebImageView_KP(Context context) {
        super(context);
    }


    /**
     * important do not hold a reference so garbage collector can grab old
     * defunct dying activity
     */
    void detach() {
        myActivity = null;
    }

    /**
     * @param activity
     *            grab a reference to this activity, mindful of leaks
     */
    void attach(MainActivity activity) {
        this.myActivity = activity;
    }

    /**
     * default image to show if we cannot load desired one
     *
     * @param drawable
     */
    public void setPlaceholderImage(Drawable drawable) {
        // error check
        if (drawable != null) {
            mPlaceholder = drawable;
//            if (mImage == null) {
            setImageDrawable(mPlaceholder);
//            }
        }
    }

    /**
     * get default from resources
     *
     * @param resid
     */
    public void setPlaceholderImage(int resid) {
        mPlaceholder = getResources().getDrawable(resid);
        if (mImage == null) {
            setImageDrawable(mPlaceholder);
        }
    }

}