package mobi.idealabs.ads.manage.controller

import androidx.core.app.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.mopub.common.MoPubReward
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubRewardedVideoListener
import com.mopub.mobileads.MoPubRewardedVideoManager.updateActivity
import com.mopub.mobileads.MoPubRewardedVideos
import com.mopub.network.RequestRateTracker
import mobi.idealabs.ads.bean.*
import mobi.idealabs.ads.manage.AdManager
import mobi.idealabs.ads.manage.AdSdk
import mobi.idealabs.ads.report.AdTracking
import mobi.idealabs.ads.report.utils.LogUtil
import mobi.idealabs.ads.view.AdRewardVideo

internal object AdRewardVideoController {

    fun initWithActivity(activity: ComponentActivity) {
    }


    val mopubRewardVideoListener = object : MoPubRewardedVideoListener {
        override fun onRewardedVideoClosed(adUnitId: String) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoClosed: ")
            findAdPlacementByAdUnit(adUnitId)?.apply {
                this.findCreateListeners(this).forEach {
                    it.onAdDismissed(this)
                }
                destroyAdPlacement(this)
                AdManager.mGlobalAdListener?.onAdDismissed(this)
            }
        }

        override fun onRewardedVideoCompleted(adUnitIds: MutableSet<String>, reward: MoPubReward) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoCompleted: ")
            findAdPlacementByAdUnit(adUnitId = adUnitIds.first())
                ?.apply {
                    this.findCreateListeners(this).forEach {
                        if (it is LifecycleAdPlacementObserver && it.adListener is RewardVideoAdListener) {
                            it.adListener.onRewardVideoCompleted(this)
                        }
                    }
                }
            showComplete = true
        }

        override fun onRewardedVideoPlaybackError(adUnitId: String, errorCode: MoPubErrorCode) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoPlaybackError: ")
            findAdPlacementByAdUnit(adUnitId = adUnitId)
                ?.apply {
                    this.findCreateListeners(this).forEach {
                        if (it is LifecycleAdPlacementObserver && it.adListener is RewardVideoAdListener) {
                            it.adListener.onRewardVideoPlayError(
                                this,
                                AdErrorCode.convertMopubError(errorCode)
                            )
                        }
                    }
                }
        }

        override fun onRewardedVideoLoadFailure(adUnitId: String, errorCode: MoPubErrorCode) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoLoadFailure: ")
            findAdPlacementByAdUnit(adUnitId)
                ?.apply {
                    if (rewardAdLoadMap[adUnitId] == true && AdSdk.canRetry) {
                        rewardAdLoadMap[adUnitId] = false
                        RequestRateTracker.getInstance().registerRateLimit(adUnitId, null, null)
                        loadAdPlacement(this)
                    } else {
                        AdManager.mGlobalAdListener?.onAdFailed(
                            this,
                            AdErrorCode.convertMopubError(errorCode)
                        )

                        this.findCreateListeners(this).forEach {
                            it.onAdFailed(
                                this,
                                AdErrorCode.convertMopubError(errorCode)
                            )
                        }
                    }
                }
        }


        override fun onRewardedVideoClicked(adUnitId: String) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoClicked: ")
            findAdPlacementByAdUnit(adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdClicked(this)
                this.findCreateListeners(this).forEach { it.onAdClicked(this) }
            }
        }

        override fun onRewardedVideoStarted(adUnitId: String) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoStarted: ")
            findAdPlacementByAdUnit(adUnitId)?.apply {
                showTime = System.currentTimeMillis()
                showComplete = false
                AdManager.mGlobalAdListener?.onAdShown(this)
                this.findCreateListeners(this).forEach { it.onAdShown(this) }
            }
        }

        override fun onRewardedVideoLoadSuccess(adUnitId: String) {
            LogUtil.d("AdRewardVideoController", "onRewardedVideoLoadSuccess: ")
            findAdPlacementByAdUnit(adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdLoaded(this)
                this.findCreateListeners(this).forEach { it.onAdLoaded(this) }
            }

        }
    }

    private var showTime = -1L
    private var showComplete: Boolean = false


    init {
        MoPubRewardedVideos.setRewardedVideoListener(mopubRewardVideoListener)
    }

    private val rewardAdLoadMap = mutableMapOf<String, Boolean>()//boolean  表示加载失败后是否再次加载
    private val rewardVideoAds = LinkedHashMap<AdPlacement, AdRewardVideo>(4, 0.75f, true)

    internal fun findAdPlacementByAdUnit(adUnitId: String): AdPlacement? {
        return rewardVideoAds.keys.find { it.adUnitId == adUnitId }
    }


    fun loadAdPlacement(adPlacement: AdPlacement) {
        if (!isReady(adPlacement)) {
            val adRewardVideo = loadRewardVideo(adPlacement)
            rewardAdLoadMap[adPlacement.adUnitId] = true
            loadAdInternal(adRewardVideo)
        }
    }

    private fun loadRewardVideo(adPlacement: AdPlacement): AdRewardVideo {
        return rewardVideoAds[adPlacement]
            ?: AdRewardVideo(adUnitId = adPlacement.adUnitId).apply {
                rewardVideoAds[adPlacement] = this
            }
    }

    private fun loadAdInternal(adRewardVideo: AdRewardVideo) {
        adRewardVideo.load()

    }


    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement,
        adListener: AdListener
    ): Boolean {
        if (lifecycleOwner is ComponentActivity) {
            updateActivity(lifecycleOwner)
        } else if (lifecycleOwner is Fragment) {
            lifecycleOwner.activity?.apply {
                updateActivity(this)
            }
        }
        val adPlacementObserver = LifecycleAdPlacementObserver(
            lifecycleOwner.lifecycle,
            adPlacement,
            adListener
        )
        AdTracking.reportAdChance(
            EventMeta(
                "",
                adPlacement.name,
                adPlacement.chanceName,
                AdType.REWARDED_VIDEO.type,
                startTimeIL = System.currentTimeMillis()
            )
        )
        if (!adPlacement.containsLifecycleListener(adPlacementObserver)) {
            adPlacement.addLifecycleListener(adPlacementObserver)
        }

        return if (isReady(adPlacement)) {
            MoPubRewardedVideos.showRewardedVideo(adPlacement.adUnitId)
            true
        } else {
            loadAdPlacement(adPlacement)
            false
        }
    }

    fun isReady(adPlacement: AdPlacement): Boolean {
        return loadRewardVideo(adPlacement).isReady()
    }

    fun destroyAdPlacement(adPlacement: AdPlacement) {
        adPlacement.clearListeners()
        rewardVideoAds.remove(adPlacement)
        when (adPlacement.mode) {
            PlacementMode.DESTROY -> rewardVideoAds.remove(adPlacement)
            PlacementMode.REBUILD -> {
                rewardVideoAds.remove(adPlacement)
                AdManager.preloadAdPlacement(adPlacement)
            }
            PlacementMode.FORCE_REFRESH -> {
                AdManager.preloadAdPlacement(adPlacement)
            }
        }
    }

}
