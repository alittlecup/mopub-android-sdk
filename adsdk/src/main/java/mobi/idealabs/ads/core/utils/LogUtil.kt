package mobi.idealabs.ads.core.utils

import android.util.Log
import mobi.idealabs.ads.core.controller.AdSdk

object LogUtil {
    fun d(tag: String, msg: String) {
        if (AdSdk.logAble) {
            Log.d(tag, msg)
        }
    }
}