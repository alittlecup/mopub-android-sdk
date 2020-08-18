package mobi.idealabs.ads.report

import com.mopub.mobileads.MoPubErrorCode
import com.mopub.network.AdResponse

class TrackInject {
    private val mAdResponse: AdResponse? = null
    private val mLastDeliveredResponse: AdResponse? = null

    fun trackWaterFallItemStart(adResponse: AdResponse) {
        TrackEventManager.trackWaterFallItemStart(adResponse)
    }

    fun trackWaterFallItemFail(errorCode: MoPubErrorCode) {
        TrackEventManager.trackWaterFallItemFail(mAdResponse, errorCode)
    }

    fun trackWaterFallFail() {
       TrackEventManager.trackWaterFallFail(mAdResponse)
    }

    fun trackWaterFallSuccess() {
        TrackEventManager.trackWaterFallSuccess(mAdResponse)
    }

    fun trackClick() {
        TrackEventManager.trackClick(mLastDeliveredResponse)
    }

    fun trackImpression() {
        TrackEventManager.trackImpression(mLastDeliveredResponse)
    }
}