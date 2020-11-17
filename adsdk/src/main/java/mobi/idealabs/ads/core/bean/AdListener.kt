package mobi.idealabs.ads.core.bean

import com.mopub.mobileads.MoPubView
import mobi.idealabs.ads.core.view.AdBanner
import mobi.idealabs.ads.core.view.AdNative

interface AdListener {
    /**
     * MoPub真正 发起请求时回调
     */
    fun onAdStartLoad(adPlacement: AdPlacement?)

    fun onAdLoaded(adPlacement: AdPlacement?)
    fun onAdFailed(adPlacement: AdPlacement?, adErrorCode: AdErrorCode)
    fun onAdShown(adPlacement: AdPlacement?)
    fun onAdDismissed(adPlacement: AdPlacement?)
    fun onAdClicked(adPlacement: AdPlacement?)
}

interface BannerAdListener : AdListener {
    /**
     *  在调用loadAd()时就直接返回
     */
    fun onBannerAdStartLoad(adPlacement: AdPlacement?)
}

interface RewardVideoAdListener : AdListener {
    fun onRewardVideoCompleted(adPlacement: AdPlacement)
    fun onRewardVideoPlayError(adPlacement: AdPlacement, adErrorCode: AdErrorCode)
}

open class DefaultAdListener : AdListener {
    override fun onAdStartLoad(adPlacement: AdPlacement?) {
    }

    override fun onAdLoaded(adPlacement: AdPlacement?) {
    }

    override fun onAdFailed(adPlacement: AdPlacement?, adErrorCode: AdErrorCode) {
    }

    override fun onAdShown(adPlacement: AdPlacement?) {
    }

    override fun onAdDismissed(adPlacement: AdPlacement?) {
    }

    override fun onAdClicked(adPlacement: AdPlacement?) {
    }
}
interface AdBannerListener {

    fun onBannerLoadStart(banner: AdBanner)
    fun onBannerLoaded(banner: AdBanner)
    fun onBannerFailed(banner: AdBanner, errorCode: AdErrorCode)
    fun onBannerClicked(banner: AdBanner)
    fun onBannerExpanded(banner: AdBanner)
    fun onBannerCollapsed(banner: AdBanner)
    fun onBannerShown(banner: AdBanner)
}
interface AdNativeListener {
    fun onNativeLoaded(adNative: AdNative)
    fun onNativeFailed(adNative: AdNative, errorCode: AdErrorCode)
    fun onNativeClicked(adNative: AdNative)
    fun onNativeShown(adNative: AdNative)
    fun onNativeDestroy(adNative: AdNative)
}

