package mobi.idealabs.ads.report.utils

import android.util.Log
import mobi.idealabs.ads.manage.AdSdk

object LogUtil {
    fun d(tag: String, msg: String) {
        if (AdSdk.logAble) {
            Log.d(tag, msg)
        }
    }
}