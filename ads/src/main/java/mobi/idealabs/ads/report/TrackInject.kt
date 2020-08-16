package mobi.idealabs.ads.report

import com.mopub.mobileads.MoPubErrorCode
import com.mopub.network.AdResponse

class TrackInject {
    private val mAdResponse: AdResponse? = null

    fun injectStart(adResponse: AdResponse) {
        TrackEventManager.trackWaterFallItemStart(adResponse)
    }

    fun injectFail(errorCode: MoPubErrorCode) {
        TrackEventManager.trackWaterFallItemFail(mAdResponse, errorCode)
    }
}