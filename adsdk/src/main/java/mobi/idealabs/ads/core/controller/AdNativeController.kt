package mobi.idealabs.ads.core.controller

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.mopub.nativeads.*
import mobi.idealabs.ads.core.bean.*
import mobi.idealabs.ads.core.network.AdTracking
import mobi.idealabs.ads.core.view.AdNative


object AdNativeController {
    private val activeAdIdNativeAdSource = HashMap<String, NativeAdSource>()

    internal val adNativeListener = object : AdNativeListener {
        override fun onNativeDestroy(adNative: AdNative) {
            findAdPlacement(adNative.adUnitId)?.apply {
                this.clearListeners()
                AdManager.mGlobalAdListener?.onAdDismissed(this)
                this.findActiveListeners(this).forEach { it.onAdDismissed(this) }
            }
        }

        override fun onNativeLoaded(adNative: AdNative) {
            findAdPlacement(adNative.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdLoaded(this)
                this.findActiveListeners(this).forEach {
                    it.onAdLoaded(this)
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


    private fun findAdPlacement(adUnitId: String): AdPlacement? {
        return AdSdk.findAdPlacement(adUnitId)
    }

    fun loadAdPlacement(adPlacement: AdPlacement, remoteListener: Boolean = true) {
        if (!AdManager.enable) return
        var nativeAdSource = getNativeAdSource(adPlacement)
        var secondNativeAdSource: NativeAdSource? = null
        if (NativeAdSourceManager.isEnableSecondCache(adUnitId = adPlacement.adUnitId)) {
            secondNativeAdSource =
                NativeAdSourceManager.getSecondNativeAdSource(adPlacement.adUnitId)
        }
        adRendererRegistry.rendererIterable.forEach {
            nativeAdSource.registerAdRenderer(it)
            secondNativeAdSource?.registerAdRenderer(it)
        }
        if (remoteListener) {
            nativeAdSource.adSourceListener = null
            secondNativeAdSource?.adSourceListener = null

        }
        nativeAdSource.loadAds(AdSdk.application!!, adPlacement.adUnitId, null)
        secondNativeAdSource?.loadAds(AdSdk.application!!, adPlacement.adUnitId, null)
    }


    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement,
        parent: FrameLayout,
        adListener: AdListener
    ): Boolean {
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
        var nativeAdSource = getNativeAdSource(adPlacement)
        val adUnitId = adPlacement.adUnitId
        if (NativeAdSourceManager.getSecondNativeAdSource(adUnitId = adUnitId)
                ?.hasAvailableAds() == true
        ) {
            nativeAdSource =
                NativeAdSourceManager.getSecondNativeAdSource(adUnitId = adUnitId)!!
            activeAdIdNativeAdSource[adUnitId] = nativeAdSource
        }
        var result = nativeAdSource.hasAvailableAds()
        nativeAdSource.setAdSourceListener {
            nativeAdSource.adSourceListener = null
            if (parent.isVisible) {
                var nativeAd = nativeAdSource.dequeueAd()
                if (nativeAd != null) {
                    var adRender = adRendererRegistry.getRendererForAd(nativeAd.baseNativeAd)
                    if (adRender != null) {
                        nativeAd.moPubAdRenderer = adRender
                    }
                    var nativeAdView = getNativeAdView(nativeAd)
                    parent.removeAllViews()
                    parent.addView(nativeAdView, FrameLayout.LayoutParams(-1, -1))
                }
            }
        }
        loadAdPlacement(adPlacement, false)
        return result
    }


    private fun getNativeAdView(nativeAd: NativeAd): View {
        var adapterHelper = AdapterHelper(AdSdk.application!!, 0, 3)
        return adapterHelper.getAdView(null, null, nativeAd)
    }


    private fun getNativeAdSource(adPlacement: AdPlacement): NativeAdSource {
        return NativeAdSourceManager.getNativeAdSource(adPlacement.adUnitId)
    }

    internal fun isReady(adPlacement: AdPlacement): Boolean {
        var hasAvailableAds = getNativeAdSource(adPlacement).hasAvailableAds()
        if (hasAvailableAds) return true
        return NativeAdSourceManager.getSecondNativeAdSource(adPlacement.adUnitId)
            ?.hasAvailableAds() ?: false
    }

    internal fun destroyAdPlacement(adPlacement: AdPlacement) {
        var nativeAdSource = activeAdIdNativeAdSource[adPlacement.adUnitId]
        nativeAdSource?.clear()
        nativeAdSource?.adSourceListener = null
    }

    private val adRendererRegistry = AdRendererRegistry();

    internal fun registerAdRenderer(
        moPubNativeAdRenderer: MoPubAdRenderer<*>
    ) {
        val iterator = adRendererRegistry.rendererIterable.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (next.javaClass == moPubNativeAdRenderer.javaClass) {
                iterator.remove()
                break
            }
        }
        adRendererRegistry.registerAdRenderer(moPubNativeAdRenderer)
    }
}