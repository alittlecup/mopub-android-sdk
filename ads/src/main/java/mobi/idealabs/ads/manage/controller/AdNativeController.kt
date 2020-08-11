package mobi.idealabs.ads.manage.controller

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import mobi.idealabs.ads.bean.*
import mobi.idealabs.ads.manage.AdManager
import mobi.idealabs.ads.manage.AdSdk
import mobi.idealabs.ads.report.AdTracking
import mobi.idealabs.ads.view.AdNative


object AdNativeController {

    private val nativeAdPlacementMap = LinkedHashMap<AdPlacement, AdNative>(8, 0.75f, true)

    internal fun initWithActivity(activity: ComponentActivity) {

    }

    private val adNativeListener = object : AdNativeListener {
        override fun onNativeDestroy(adNative: AdNative) {
            findAdPlacement(adNative.adUnitId)?.apply {
                nativeAdPlacementMap.remove(this)
                this.clearListeners()
                AdManager.mGlobalAdListener?.onAdDismissed(this)
                this.findActiveListeners(this).forEach { it.onAdDismissed(this) }
            }
        }

        override fun onNativeLoaded(adNative: AdNative) {
            findAdPlacement(adNative.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdLoaded(this)
                var hasActiveListener = false
                this.findActiveListeners(this).forEach {
                    it.onAdLoaded(this)
                    hasActiveListener = true
                }
            }
        }

        override fun onNativeFailed(adNative: AdNative, errorCode: AdErrorCode) {
            findAdPlacement(adNative.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdFailed(
                    this,
                    errorCode
                )
                this.findActiveListeners(this).forEach { it.onAdFailed(this, errorCode) }
            }

        }

        override fun onNativeClicked(adNative: AdNative) {
            findAdPlacement(adNative.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdClicked(this)
                this.findActiveListeners(this).forEach { it.onAdClicked(this) }
            }
        }

        override fun onNativeShown(adNative: AdNative) {
            findAdPlacement(adNative.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdShown(this)
                this.findActiveListeners(this).forEach { it.onAdShown(this) }
            }
        }

    }

    internal fun findAdPlacement(adUnitId: String): AdPlacement? {
        return nativeAdPlacementMap.keys.find { it.adUnitId == adUnitId }
    }

    fun loadAdPlacement(adPlacement: AdPlacement, @LayoutRes layoutRes: Int) {
        if (!AdManager.enable) return
        var adNative = nativeAdPlacementMap[adPlacement]
        if (adNative == null) {
            adNative = createAdNative(adPlacement)
        }
        adNative.loadAd(layoutRes)
    }

    fun createAdNative(adPlacement: AdPlacement): AdNative {
        val adNative = AdNative(AdSdk.application!!, adPlacement.adUnitId)
        adNative.adNativeListener = adNativeListener
        nativeAdPlacementMap[adPlacement] = adNative
        return adNative
    }

    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement, @LayoutRes layoutRes: Int,
        parent: FrameLayout,
        adListener: AdListener
    ): Boolean {
        var moPubNative = nativeAdPlacementMap[adPlacement]

        AdTracking.reportAdChance(
            EventMeta(
                "",
                adPlacement.name,
                adPlacement.chanceName,
                AdType.NATIVE.type,
                startTimeIL = System.currentTimeMillis()
            )
        )

        adPlacement.addLifecycleListener(
            LifecycleAdPlacementObserver(
                lifecycleOwner.lifecycle,
                adPlacement,
                adListener
            )
        )
        if (moPubNative == null) {
            moPubNative = createAdNative(adPlacement)
        }
        var result = false
        if (!isReady(adPlacement)) {
            loadAdPlacement(adPlacement, layoutRes)
        } else {
            result = true
        }
        var showAd = moPubNative.showAd()
        if (showAd?.parent != parent) {
            var viewParent = showAd?.parent
            if (viewParent != null) {
                (viewParent as ViewGroup).removeView(showAd)
            }
            parent.addView(showAd, FrameLayout.LayoutParams(-1, -1))
        }

        return result

    }

    fun isReady(adPlacement: AdPlacement): Boolean {
        var moPubNative = nativeAdPlacementMap[adPlacement]
        return moPubNative != null && moPubNative.isReady()
    }

    fun destroyAdPlacement(adPlacement: AdPlacement) {
        var mode = adPlacement.mode
        var adNative = nativeAdPlacementMap[adPlacement]

        when (mode) {
            PlacementMode.DESTROY -> {
                adNative?.destroyAd()
            }
            PlacementMode.FORCE_REFRESH -> {
                adNative?.destroyAd()
            }
            PlacementMode.REBUILD -> {
                adNative?.destroyAd()
            }
        }

    }
}