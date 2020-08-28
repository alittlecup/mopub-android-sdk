package mobi.idealabs.ads.report

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LifecycleRegistryOwner

class LifecycleOwnerFragment : android.app.Fragment(), LifecycleOwner {
    val lifecycleRegistry = LifecycleRegistry(this)

    companion object {
        internal const val LIFECYCLE_OWNER_FRAGMENT =
            "mobi.idealabs.ads.LifecycleDispatcher.lifecycle_owner_fragment_tag"

        fun injectIfNeededIn(activity: Activity) {
            if (activity is LifecycleOwner || activity is LifecycleRegistryOwner) {
                return
            }
            val fragmentManager = activity.fragmentManager
            if (fragmentManager.findFragmentByTag(LIFECYCLE_OWNER_FRAGMENT) == null) {
                fragmentManager.beginTransaction()
                    .add(LifecycleOwnerFragment(), LIFECYCLE_OWNER_FRAGMENT)
                    .commit()
                fragmentManager.executePendingTransactions()
            }
        }

        fun get(activity: Activity): LifecycleOwnerFragment? {
            return activity.fragmentManager.findFragmentByTag(LIFECYCLE_OWNER_FRAGMENT) as LifecycleOwnerFragment?
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dispatch(Lifecycle.Event.ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        dispatch(Lifecycle.Event.ON_START)

    }

    override fun onResume() {
        super.onResume()
        dispatch(Lifecycle.Event.ON_RESUME)

    }

    override fun onStop() {
        super.onStop()
        dispatch(Lifecycle.Event.ON_STOP)

    }

    override fun onDestroy() {
        super.onDestroy()
        dispatch(Lifecycle.Event.ON_DESTROY)
    }

    private fun dispatch(event: Lifecycle.Event) {
        lifecycleRegistry.handleLifecycleEvent(event)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

}