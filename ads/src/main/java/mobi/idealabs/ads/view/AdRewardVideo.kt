package mobi.idealabs.ads.view

import com.mopub.mobileads.MoPubRewardedVideos


class AdRewardVideo(val adUnitId: String) {

    fun load() {
        MoPubRewardedVideos.loadRewardedVideo(adUnitId)
    }

    fun isReady(): Boolean {
        return MoPubRewardedVideos.hasRewardedVideo(adUnitId)
    }

}
