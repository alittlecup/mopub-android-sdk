package mobi.idealabs.ads.core.controller

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import mobi.idealabs.ads.core.bean.*
import mobi.idealabs.ads.core.network.AdTracking
import mobi.idealabs.ads.core.utils.LogUtil
import mobi.idealabs.ads.core.view.AdBanner

object AdBannerController {

    private val matchLayoutParams = FrameLayout.LayoutParams(-1, -1)


    internal val defaultAdBannerListener = object : AdBannerListener {

        override fun onBannerLoadStart(banner: AdBanner) {
            LogUtil.d("AdBanner", "onBannerLoadStart: $banner")
            chanceTime = System.currentTimeMillis()
            getAdByAdUnit(banner.adUnitId)?.apply {
                this.findActiveListeners(this).lastOrNull()?.let {
                    if (it is LifecycleAdPlacementObserver && it.adListener is BannerAdListener) {
                        it.adListener.onBannerAdStartLoad(this)
                    }
                }
            }
        }

        override fun onBannerLoaded(banner: AdBanner) {
            LogUtil.d("AdBanner", "onBannerLoaded: $banner")
            getAdByAdUnit(banner.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdLoaded(this)
                this.findActiveListeners(this).forEach { it.onAdLoaded(this) }
            }
        }

        override fun onBannerFailed(banner: AdBanner, errorCode: AdErrorCode) {
            LogUtil.d("AdBanner", "onBannerFailed: $banner ,error $errorCode")
            getAdByAdUnit(banner.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdFailed(this, errorCode)
                var hasActivityListener = false
                this.findActiveListeners(this).forEach {
                    it.onAdFailed(this, errorCode)
                    hasActivityListener = true
                }
                if (hasActivityListener) {
                    reportChance(banner.adUnitId)
                }
            }


        }

        override fun onBannerClicked(banner: AdBanner) {
            LogUtil.d("AdBanner", "onBannerClicked: $banner")
            getAdByAdUnit(banner.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdClicked(this)
                this.findActiveListeners(this).forEach { it.onAdClicked(this) }
            }

        }

        override fun onBannerExpanded(banner: AdBanner) {
            LogUtil.d("AdBanner", "onBannerExpanded: $banner")
        }

        override fun onBannerCollapsed(banner: AdBanner) {
            LogUtil.d("AdBanner", "onBannerCollapsed: $banner")
        }

        override fun onBannerShown(banner: AdBanner) {
            LogUtil.d("AdBanner", "onBannerShown: $banner")
            reportChance(banner.adUnitId)
            getAdByAdUnit(banner.adUnitId)?.apply {
                AdManager.mGlobalAdListener?.onAdShown(this)
                this.findActiveListeners(this).lastOrNull()?.let { it.onAdShown(this) }
            }


        }
    }
    internal val defaultBannerAdListener = object : MopubBannerAdListener {

        override fun onBannerLoaded(banner: MoPubView) {
            LogUtil.d("AdBanner", "onBannerLoaded: ")
            defaultAdBannerListener.onBannerLoaded(banner as AdBanner)
        }

        override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
            defaultAdBannerListener.onBannerFailed(
                banner as AdBanner,
                AdErrorCode.convertMopubError(errorCode)
            )

        }

        override fun onBannerClicked(banner: MoPubView?) {
            defaultAdBannerListener.onBannerClicked(banner as AdBanner)

        }

        override fun onBannerLoadStart(banner: MoPubView) {
            LogUtil.d("AdBanner", "onBannerLoadStart: ")
            defaultAdBannerListener.onBannerLoadStart(banner as AdBanner)
        }

        override fun onBannerExpanded(banner: MoPubView?) {
            defaultAdBannerListener.onBannerExpanded(banner as AdBanner)

        }

        override fun onBannerCollapsed(banner: MoPubView?) {
            defaultAdBannerListener.onBannerCollapsed(banner as AdBanner)
        }
    }

    private var chanceTime = -1L

    internal fun getAdByAdUnit(adUnitId: String): AdPlacement? {
        return mBannerMap.keys.find { it.adUnitId == adUnitId }
    }

    private val mBannerMap = LinkedHashMap<AdPlacement, AdBanner>(8, 0.57f, true)


    fun showAdPlacement(
        lifecycleOwner: LifecycleOwner,
        adPlacement: AdPlacement,
        viewGroup: FrameLayout,
        adListener: AdListener
    ): Boolean {
        var adBanner = findAdBannerFromContainer(adPlacement)
        val activity: Activity? =
            if (lifecycleOwner is Activity) lifecycleOwner else if (lifecycleOwner is Fragment) lifecycleOwner.activity else null

        if (adBanner != null && adBanner.parent == viewGroup) {
            return true
        } else {
            destroyAdPlacement(adPlacement)
            adBanner = createAdBanner(activity, adPlacement)
        }
        if (adBanner == null) return false

        adPlacement.addLifecycleListener(
            LifecycleAdPlacementObserver(
                lifecycleOwner.lifecycle,
                adPlacement,
                adListener
            )
        )

        if (activity == null) {
            return false
        }

        chanceTime = System.currentTimeMillis()

        adBanner.loadAd()

        showAdBanner(adBanner, viewGroup)

        return false
    }

    private fun showAdBanner(adBanner: AdBanner, viewGroup: FrameLayout) {
        var sameContainer = adBanner.parent == viewGroup
        if (!sameContainer) {
            clearParent(adBanner)
            viewGroup.addView(adBanner, matchLayoutParams)
        }
    }


    private fun createAdBanner(context: Context?, adPlacement: AdPlacement): AdBanner? {
        require(adPlacement.adType == AdType.BANNER) {
            "create Banner must be Banner Type "
        }
        return if (context != null) {
            val adBanner = AdBanner(context)
                .apply {
                    this.adSize = MoPubView.MoPubAdSize.valueOf(adPlacement.adSize.adSize)
                    this.adUnitId = adPlacement.adUnitId
                }
            mBannerMap[adPlacement] = adBanner
            adBanner
        } else {
            null
        }

    }

    private fun findAdBannerFromContainer(adPlacement: AdPlacement): AdBanner? {
        require(adPlacement.adType == AdType.BANNER) {
            "find Banner must be Banner Type "
        }
        return mBannerMap[adPlacement]
    }


    private fun clearParent(view: View) {
        var parent = view.parent
        if (parent != null) {
            (parent as ViewGroup).removeView(view)
        }
    }

    private fun destroyAdBanner(adBanner: AdBanner) {
        clearParent(adBanner)
        adBanner.destroy()
        adBanner.bannerAdListener = null
    }

    fun destroyAdPlacement(adPlacement: AdPlacement) {
        var adBanner = findAdBannerFromContainer(adPlacement)
        adPlacement.clearListeners()
        mBannerMap.remove(adPlacement)
        reportChance(adPlacement.adUnitId)
        if (adBanner == null) return
        destroyAdBanner(adBanner)

    }

    fun isReady(adPlacement: AdPlacement): Boolean {
        return findAdBannerFromContainer(adPlacement)?.parent != null
    }

    private fun reportChance(adUnitId: String) {
        if (chanceTime == -1L) {
            return
        }
        AdSdk.findAdPlacement(adUnitId)?.apply {
            var eventMeta = EventMeta(
                "",
                name,
                chanceName,
                AdType.BANNER.type,
                startTimeIL = chanceTime,
                duration = (System.currentTimeMillis() - chanceTime).toInt()
            )
            AdTracking.reportAdChance(eventMeta)

        }
        chanceTime = -1
    }


}