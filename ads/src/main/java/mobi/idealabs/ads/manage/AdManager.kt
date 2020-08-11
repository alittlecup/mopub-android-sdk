package mobi.idealabs.ads.manage

import android.widget.FrameLayout
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LifecycleOwner
import mobi.idealabs.ads.bean.AdListener
import mobi.idealabs.ads.bean.AdPlacement
import mobi.idealabs.ads.bean.AdType
import mobi.idealabs.ads.bean.DefaultAdListener
import mobi.idealabs.ads.manage.controller.AdBannerController
import mobi.idealabs.ads.manage.controller.AdInterstitialController
import mobi.idealabs.ads.manage.controller.AdNativeController
import mobi.idealabs.ads.manage.controller.AdRewardVideoController

object AdManager {
    var enable = false
    internal fun initWithActivity(activity: ComponentActivity) {
        enable = true
        AdBannerController.initWithActivity(activity)
        AdInterstitialController.initWithActivity(activity)
        AdRewardVideoController.initWithActivity(activity)
    }

    fun preloadAdPlacement(
        adPlacement: AdPlacement, nativeLayoutRes: Int = -1
    ) {
        if (!enable) return
        try {
            when (adPlacement.adType) {
                AdType.BANNER -> {}
                AdType.INTERSTITIAL -> AdInterstitialController.loadAdPlacement(
                    adPlacement
                )
                AdType.REWARDED_VIDEO -> AdRewardVideoController.loadAdPlacement(
                    adPlacement
                )
                AdType.NATIVE -> {
                    require(nativeLayoutRes != -1) {
                        "The Native Layout Resource  must > -1"
                    }
                    AdNativeController.loadAdPlacement(adPlacement, nativeLayoutRes)
                }
            }
        } catch (e: Exception) {

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

    fun showAdChange(
        lifecycleOwner: LifecycleOwner,
        adChanceName: String,
        viewGroup: FrameLayout? = null,
        adListener: AdListener = DefaultAdListener(), nativeLayoutRes: Int = -1
    ): Boolean {
        if (!enable) return false
        var adPlacement = AdSdk.adPlacementFinder?.invoke(adChanceName)
        return adPlacement?.let {
            adPlacement.chanceName = adChanceName
            showAdPlacement(
                lifecycleOwner,
                adPlacement,
                viewGroup,
                adListener,
                nativeLayoutRes
            )
        } ?: false
    }

    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement,
        viewGroup: FrameLayout? = null,
        adListener: AdListener = DefaultAdListener(),
        nativeLayoutRes: Int = -1
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
                require(nativeLayoutRes != -1) {
                    "The Native Layout Resource  must > -1"
                }
                AdNativeController.showAdPlacement(
                    lifecycleOwner,
                    adPlacement,
                    nativeLayoutRes,
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

    var mGlobalAdListener: AdListener? = null


}