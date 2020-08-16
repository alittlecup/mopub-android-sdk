package mobi.idealabs.ads.manage.controller

import androidx.core.app.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import mobi.idealabs.ads.bean.*
import mobi.idealabs.ads.view.AdInterstitial
import mobi.idealabs.ads.manage.AdManager
import mobi.idealabs.ads.manage.AdSdk
import mobi.idealabs.ads.report.ActivityLifeManager
import mobi.idealabs.ads.report.AdTracking
import mobi.idealabs.ads.report.utils.LogUtil
import kotlin.collections.set

internal object AdInterstitialController {

    fun initWithActivity(activity: ComponentActivity) {
    }


    private val mInterstitialMap = LinkedHashMap<AdPlacement, AdInterstitial>(4, 0.57f, true)
    private val mInterstitialAdListener = object : MoPubInterstitial.InterstitialAdListener {

        override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
            LogUtil.d("AdInterstitial", "onInterstitialLoaded: ")
            getAdByAdUnit((interstitial as AdInterstitial).adsUnitId)
                ?.apply {
                    AdManager.mGlobalAdListener?.onAdLoaded(this)

                    this.findActiveListeners(this).forEach { it.onAdLoaded(this) }
                }

        }

        override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
            if (interstitial == null) return
            LogUtil.d("AdInterstitial", "onInterstitialShown:")
            getAdByAdUnit((interstitial as AdInterstitial).adsUnitId)
                ?.apply {
                    AdManager.mGlobalAdListener?.onAdShown(this)
                    this.findActiveListeners(this).forEach { it.onAdShown(this) }
                }
            showTime = System.currentTimeMillis()

        }

        override fun onInterstitialFailed(
            interstitial: MoPubInterstitial?,
            errorCode: MoPubErrorCode?
        ) {
            LogUtil.d("AdInterstitial", "onInterstitialFailed: ")
            getAdByAdUnit((interstitial as AdInterstitial).adsUnitId)
                ?.apply {
                    if (interstitialLoadMap[interstitial] == true && AdSdk.canRetry) {
                        interstitialLoadMap[interstitial] = false
//                        RequestRateTracker.getInstance().registerRateLimit(adUnitId, null, null)
                        interstitial.load()
                    } else {
                        AdManager.mGlobalAdListener?.onAdFailed(
                            this,
                            AdErrorCode.convertMopubError(errorCode)
                        )
                        this.findActiveListeners(this)
                            .forEach {
                                it.onAdFailed(
                                    this,
                                    AdErrorCode.convertMopubError(errorCode)
                                )
                            }
                    }
                }
        }

        override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
            LogUtil.d("AdInterstitial", "onInterstitialDismissed: ")
            getAdByAdUnit((interstitial as AdInterstitial).adsUnitId)
                ?.apply {
                    destroyAdPlacement(this)
                    AdManager.mGlobalAdListener?.onAdDismissed(this)
                    this.findActiveListeners(this).forEach { it.onAdDismissed(this) }
                }
        }

        override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
            LogUtil.d("AdInterstitial", "onInterstitialClicked: ")
            getAdByAdUnit((interstitial as AdInterstitial).adsUnitId)
                ?.apply {
                    AdManager.mGlobalAdListener?.onAdClicked(this)

                    this.findActiveListeners(this).forEach { it.onAdClicked(this) }
                }
        }
    }
    private var showTime = -1L

    internal fun getAdByAdUnit(adUnitId: String): AdPlacement? {
        return mInterstitialMap.keys.find { it.adUnitId == adUnitId }
    }

    private val interstitialLoadMap =
        mutableMapOf<MoPubInterstitial, Boolean>()//boolean  表示加载失败后是否再次加载


    fun loadAdPlacement(adPlacement: AdPlacement) {
        var adInterstitial =
            loadAdInterstitial(
                adPlacement
            )
        if (adInterstitial != null) {
            loadAdInterstitialInternal(
                adInterstitial
            )
        }
    }

    private fun loadAdInterstitialInternal(adInterstitial: AdInterstitial) {
        if (!adInterstitial.isReady) {
            adInterstitial.load()
            interstitialLoadMap[adInterstitial] = true
        }
    }

    private fun loadAdInterstitial(adPlacement: AdPlacement): AdInterstitial? {
        return findAdInterstitial(
            adPlacement
        ) ?: createAdInterstitial(
            adPlacement
        )
    }

    private fun createAdInterstitial(adPlacement: AdPlacement): AdInterstitial? {
        val findCurrentActivity = ActivityLifeManager.findCurrentActivity()
        return if (findCurrentActivity != null) {
            AdInterstitial(
                findCurrentActivity,
                adPlacement.adUnitId
            ).apply {
                mInterstitialMap[adPlacement] = this
                interstitialAdListener =
                    mInterstitialAdListener
            }
        } else {
            null
        }
    }

    private fun findAdInterstitial(adPlacement: AdPlacement): AdInterstitial? {
        return mInterstitialMap[adPlacement]
    }

    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement,
        adListener: AdListener
    ): Boolean {

        AdTracking.reportAdChance(
            EventMeta(
                "",
                adPlacement.name,
                adPlacement.chanceName,
                AdType.INTERSTITIAL.type,
                startTimeIL = System.currentTimeMillis()
            )
        )
        var adInterstitial =
            loadAdInterstitial(
                adPlacement
            ) ?: return false

        adPlacement.addLifecycleListener(
            LifecycleAdPlacementObserver(
                lifecycleOwner.lifecycle,
                adPlacement,
                adListener
            )
        )
        adInterstitial.chanceName = adPlacement.chanceName
        return if (adInterstitial.isReady) {
            LogUtil.d("AdInterstitial", "showAdPlacement: ${lifecycleOwner.lifecycle.currentState}")
            if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                adInterstitial.show()
            } else {
                lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_RESUME) {
                            adInterstitial.show()
                            source.lifecycle.removeObserver(this)
                        }
                    }
                })
            }
            true
        } else {
            loadAdInterstitialInternal(
                adInterstitial
            )
            false
        }
    }

    fun destroyAdPlacement(adPlacement: AdPlacement) {
        adPlacement.clearListeners()
        var adInterstitial: AdInterstitial? =
            findAdInterstitial(
                adPlacement
            )
                ?: return
        when (adPlacement.mode) {
            PlacementMode.DESTROY -> {
                mInterstitialMap.remove(adPlacement)
                adInterstitial?.destroy()
            }
            PlacementMode.FORCE_REFRESH -> {
                adInterstitial?.forceRefresh()
            }
            PlacementMode.REBUILD -> {
                mInterstitialMap.remove(adPlacement)
                adInterstitial?.destroy()
                AdManager.preloadAdPlacement(adPlacement)
            }
        }

    }

    fun isReady(adPlacement: AdPlacement): Boolean {
        var adInterstitial =
            loadAdInterstitial(
                adPlacement
            ) ?: return false
        return adInterstitial.isReady
    }

}