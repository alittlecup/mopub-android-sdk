package mobi.idealabs.ads.core.controller

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import mobi.idealabs.ads.report.LifecycleOwnerFragment
import java.lang.ref.WeakReference
import java.util.*

object ActivityLifeManager {
    internal val activityStack = LinkedList<Activity>()
    internal var topActivity = WeakReference<Activity>(null)


    internal fun initWithActivity(activity: Activity) {
        activity.application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                activityStack.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity::class.java.name.contains("idealabs")) {
                    activityStack.add(activity)
                } else {
                    LifecycleOwnerFragment.injectIfNeededIn(activity)

                }
            }

            override fun onActivityResumed(activity: Activity) {
                topActivity = WeakReference(activity)
            }
        })
        activityStack.add(activity)
    }

    fun findCurrentActivity(): Activity? {
        val activity = if (activityStack.isNotEmpty()) activityStack.first else null
        Log.d("ActivityLifeManager", "findCurrentActivity: $activity")
        return activity
    }


}