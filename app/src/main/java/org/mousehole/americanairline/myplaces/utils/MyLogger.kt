package org.mousehole.americanairline.myplaces.utils

import android.util.Log

object MyLogger {
    private const val TAG = "TAG_X"

    fun debug(msg:String) {
        Log.d(TAG, msg)
    }

    fun error(t:Throwable) {
        Log.e(TAG, t.message, t)
    }
}