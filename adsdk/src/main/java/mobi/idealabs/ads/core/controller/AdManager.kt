package mobi.idealabs.ads.core.controller

import android.util.Log
import android.widget.FrameLayout
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import com.mopub.nativeads.MoPubAdRenderer
import mobi.idealabs.ads.core.bean.AdListener
import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.AdType
import mobi.idealabs.ads.core.bean.DefaultAdListener

object AdManager {
    var enable = false
    internal fun initWithActivity(activity: ComponentActivity) {
        enable = true
    }

    fun preloadAdPlacement(
        adPlacement: AdPlacement
    ) {
        if (!enable) return
        try {
            when (adPlacement.adType) {
                AdType.BANNER -> {
                }
                AdType.INTERSTITIAL -> AdInterstitialController.loadAdPlacement(
                    adPlacement
                )
                AdType.REWARDED_VIDEO -> AdRewardVideoController.loadAdPlacement(
                    adPlacement
                )
                AdType.NATIVE -> {
                    AdNativeController.loadAdPlacement(adPlacement)
                }
            }
        } catch (e: Exception) {
            Log.d("AdManager", "preloadAdPlacement: "+e)
        }

    }

    fun registerAdRenderer(moPubNativeAdRenderer: MoPubAdRenderer<*>) {
        AdNativeController.registerAdRenderer(moPubNativeAdRenderer)
    }

    fun registerMockAdRenderer(moPubNativeAdRenderer: List<MoPubAdRenderer<*>>) {
        AdNativeController.registerMockAdRenderer(moPubNativeAdRenderer)
    }

    fun preloadAdPlacementByName(placementName: String) {
        val adPlacement = AdSdk.findAdPlacementByName(placementName)
        if (adPlacement != null) {
            preloadAdPlacement(adPlacement)
        }
    }

    fun isReadyByName(placementName: String): Boolean {
        val adPlacement = AdSdk.findAdPlacementByName(placementName)
        return if (adPlacement != null) {
            isReady(adPlacement)
        } else {
            false
        }
    }

    fun isReady(adPlacement: AdPlacement): Boolean {
        if (!enable) return false

        return when (adPlacement.adType) {
            AdType.BANNER -> AdBannerController.isReady(
                adPlacement
            )
            AdType.INTERSTITIAL -> AdInterstitialController.isReady(
                adPlacement
            )
            AdType.REWARDED_VIDEO -> AdRewardVideoController.isReady(
                adPlacement
            )
            AdType.NATIVE -> AdNativeController.isReady(adPlacement)
        }
    }

    fun showAdChance(
        lifecycleOwner: LifecycleOwner,
        adChanceName: String,
        viewGroup: FrameLayout? = null,
        adListener: AdListener = DefaultAdListener()
    ): Boolean {
        if (!enable) return false
        var adPlacement = AdSdk.findAdPlacementByChanceName(adChanceName)
        return adPlacement?.let {
            adPlacement.chanceName = adChanceName
            showAdPlacement(
                lifecycleOwner,
                adPlacement,
                viewGroup,
                adListener
            )
        } ?: false
    }

    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement,
        viewGroup: FrameLayout? = null,
        adListener: AdListener = DefaultAdListener()
    ): Boolean {
        if (!enable) return false
        return when (adPlacement.adType) {
            AdType.BANNER -> {
                require(viewGroup != null) {
                    "The Banner Type show must have container viewGroup"
                }
                AdBannerController.showAdPlacement(
                    lifecycleOwner,
                    adPlacement,
                    viewGroup = viewGroup,
                    adListener = adListener
                )
            }
            AdType.INTERSTITIAL -> {

                AdInterstitialController.showAdPlacement(
                    lifecycleOwner,
                    adPlacement,
                    adListener
                )
            }
            AdType.REWARDED_VIDEO -> {
                AdRewardVideoController.showAdPlacement(
                    lifecycleOwner,
                    adPlacement,
                    adListener
                )
            }
            AdType.NATIVE -> {
                require(viewGroup != null) {
                    "The Native Type show must have container viewGroup"
                }

                AdNativeController.showAdPlacement(
                    lifecycleOwner,
                    adPlacement,
                    viewGroup,
                    adListener
                )
            }
        }
    }


    fun destroyAdPlacement(adPlacement: AdPlacement) {
        if (!enable) return
        when (adPlacement.adType) {
            AdType.BANNER -> AdBannerController.destroyAdPlacement(
                adPlacement
            )
            AdType.INTERSTITIAL -> AdInterstitialController.destroyAdPlacement(
                adPlacement
            )
            AdType.REWARDED_VIDEO -> AdRewardVideoController.destroyAdPlacement(
                adPlacement
            )
            AdType.NATIVE -> AdNativeController.destroyAdPlacement(adPlacement)
        }

    }

    fun destroyAdPlacementByName(placementName: String) {
        val adPlacement = AdSdk.findAdPlacementByName(placementName)
        if (adPlacement != null) {
            destroyAdPlacement(adPlacement)
        }
    }

    var mGlobalAdListener: AdListener? = null

    public fun enableSecondNativeCache(adUnitId: String) {
        NativeAdSourceManager.enableSecondNativeAdSource(adUnitId);
    }
}