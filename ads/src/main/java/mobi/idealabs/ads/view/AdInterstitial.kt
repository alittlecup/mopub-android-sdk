package mobi.idealabs.ads.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import androidx.core.app.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mopub.common.util.Reflection
import com.mopub.mobileads.*
import com.mopub.mobileads.factories.CustomEventInterstitialAdapterFactory
import mobi.idealabs.ads.manage.AdSdk
import mobi.idealabs.ads.manage.controller.AdInterstitialController
import mobi.idealabs.ads.report.ActivityLifeManager
import mobi.idealabs.ads.report.utils.LogUtil
import java.lang.ref.WeakReference

class AdInterstitial(activity: Activity, adUnitId: String) :
    MoPubInterstitial(activity, adUnitId) {
    private val adInterstitialView: AdInterstitialView
    private var mCustomEventInterstitialAdapter: CustomEventInterstitialAdapter? = null

    private var weakActivity = WeakReference<Activity>(null)

    private val lifecycleEventObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                source.lifecycle.removeObserver(this)
                val currentActivity = ActivityLifeManager.findCurrentActivity()
                if (currentActivity != null) {
                    updateActivity(currentActivity)
                } else {
                    AdSdk.findAdPlacement(adUnitId)?.also {
                        AdInterstitialController.destroyAdPlacement(it)
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun updateActivity(activity: Activity) {
        weakActivity = WeakReference(activity)
        if (mCustomEventInterstitialAdapter != null) {
            reflectField(mCustomEventInterstitialAdapter!!, "mContext", activity)
        }
        if (activity is ComponentActivity) {
            activity.lifecycle.addObserver(lifecycleEventObserver)
        }
    }

    init {
        adInterstitialView =
            AdInterstitialView(activity.application).apply {
                this.adUnitId = this@AdInterstitial.adUnitId
            }
        updateActivity(activity)
        clearMopubInterstitial()
    }

    internal var chanceName: String = ""


    private fun clearMopubInterstitial() {
        invalidateInterstitialAdapter()
        reflectField(this, "mActivity", Activity())

        var interstitialViewField =
            Reflection.getPrivateField(MoPubInterstitial::class.java, "mInterstitialView")
        reflectMethod(interstitialViewField.get(this), "destroy")

        reflectClassField("mInterstitialView", adInterstitialView)
    }


    private fun reflectClassField(fieldName: String, any: Any) {
        try {
            var reflectField =
                Reflection.getPrivateField(MoPubInterstitial::class.java, fieldName)
            reflectField.set(this, any)
        } catch (e: Exception) {
        }
    }


    inner class AdInterstitialView(context: Context) : MoPubInterstitial.MoPubInterstitialView(context) {
        override fun loadCustomEvent(
            customEventClassName: String?,
            serverExtras: MutableMap<String, String>?
        ) {
            if (mAdViewController == null) {
                return
            }
            if (TextUtils.isEmpty(customEventClassName)) {
                loadFailUrl(MoPubErrorCode.ADAPTER_NOT_FOUND)
                return
            }
            invalidateInterstitialAdapter()

            mCustomEventInterstitialAdapter = CustomEventInterstitialAdapterFactory.create(
                this@AdInterstitial,
                customEventClassName,
                serverExtras,
                mAdViewController!!.broadcastIdentifier,
                mAdViewController!!.adReport
            ).apply {
                reflectField(
                    this,
                    "mCustomEventInterstitialAdapterListener",
                    this@AdInterstitial
                )


            }
            var activity = weakActivity.get()

            if (activity == null || activity.isDestroyed) {
                activity = ActivityLifeManager.findCurrentActivity()
            }
            if (activity == null) return
            reflectField(mCustomEventInterstitialAdapter!!, "mContext", activity)
            LogUtil.d("AdInterstitialView", "loadCustomEvent: $activity")

            reflectClassField("mCustomEventInterstitialAdapter", mCustomEventInterstitialAdapter!!)
            loadCustomEventInterstitial()
        }

    }


    private fun loadCustomEventInterstitial() {//Interstitial The requested instance does not exist
        if (mCustomEventInterstitialAdapter != null) {
            reflectMethod(mCustomEventInterstitialAdapter, "loadInterstitial")
        }
    }

    private fun invalidateInterstitialAdapter() {
        if (mCustomEventInterstitialAdapter != null) {
            reflectMethod(mCustomEventInterstitialAdapter, "invalidate")
        }
    }

    private fun reflectMethod(any: Any?, methodName: String) {
        try {
            Reflection.MethodBuilder(any, methodName)
                .setAccessible()
                .execute()
        } catch (e: Exception) {
        }
    }

    private fun reflectField(any: Any, fieldName: String, field: Any) {
        try {
            var reflectField =
                Reflection.getPrivateField(any::class.java, fieldName)
            reflectField.set(any, field)
        } catch (e: Exception) {
        }
    }

}

