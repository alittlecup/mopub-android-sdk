package mobi.idealabs.ads.core.controller

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import mobi.idealabs.ads.core.utils.LogUtil
import java.lang.ref.WeakReference
import java.util.*

object ActivityLifeManager : LifecycleOwner {
    internal val activityStack = LinkedList<Activity>()
    internal var topActivity = WeakReference<Activity>(null)
    internal var clickActivityName: String? = null

    private val lifecycleRegistry = LifecycleRegistry(this)

    internal fun initWithActivity(activity: Activity) {
        activity.application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                LogUtil.d("ActivityLifeManager", "onActivityDestroyed: $activity")
                notifyTopActivityLifecycleEvent(activity, Lifecycle.Event.ON_DESTROY)
                activityStack.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                notifyTopActivityLifecycleEvent(activity, Lifecycle.Event.ON_STOP)
            }

            override fun onActivityStopped(activity: Activity) {
                notifyTopActivityLifecycleEvent(activity, Lifecycle.Event.ON_STOP)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                LogUtil.d("ActivityLifeManager", "onActivityCreated: $activity")

                if (activity::class.java.name.contains("idealabs")) {
                    LogUtil.d("ActivityLifeManager", "onActivityCreated+add: $activity")
                    activityStack.add(activity)
                }
            }

            override fun onActivityResumed(activity: Activity) {
                notifyTopActivityLifecycleEvent(activity, Lifecycle.Event.ON_RESUME)
                topActivity = WeakReference(activity)
            }
        })
        activityStack.add(activity)
    }

    private fun notifyTopActivityLifecycleEvent(
        activity: Activity,
        lifecycleEvent: Lifecycle.Event
    ) {
        Log.d(
            "ActivityLifeManager",
            "notifyTopActivityLifecycleEvent:${activity::class.java.name} $lifecycleEvent -$clickActivityName"
        )
        if (showNotifyLifecycleEvent(activity)) {
            lifecycleRegistry.handleLifecycleEvent(lifecycleEvent)
        }
    }

    private fun showNotifyLifecycleEvent(activity: Activity): Boolean {
        if (clickActivityName.isNullOrEmpty()) return false
        return clickActivityName == activity::class.java.name
    }

    fun findCurrentActivity(): Activity? {
        val activity = if (activityStack.isNotEmpty()) activityStack.first else null
        Log.d("ActivityLifeManager", "findCurrentActivity: $activity")
        return activity
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

}