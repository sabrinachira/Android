package com.example.sabri.petsproject3;

/**
 * Created by jaydo on 3/20/2018.
 */

public class DownloadTask_KP extends DownloadTask {
    MainActivity myActivity;


    DownloadTask_KP(MainActivity activity) {
        attach(activity);
    }

    @Override
    protected void onPostExecute(String result) {
        if (myActivity != null) {
            myActivity.processJSON(result);
        }
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
}