package mobi.idealabs.ads.core.network

import android.util.Log
import androidx.annotation.Keep
import com.mopub.mobileads.MoPubError
import com.mopub.network.AdResponse
import mobi.idealabs.ads.core.bean.EventMeta
import mobi.idealabs.ads.core.controller.AdSdk.findAdPlacement
import mobi.idealabs.ads.core.utils.LogUtil
import mobi.idealabs.ads.core.utils.VendorUtil

/**
 * 统一的数据收集和上报类
 */
@Keep
class TrackEvent(private val adUnitId: String, private val requestId: String) {
    private val requestMetas = mutableMapOf<String, EventMeta>()
    private val showTimeMap = mutableMapOf<Int, Long>()
    private val clickTimeMap = mutableMapOf<Int, Long>()


    private fun reportRequestSummaryInfo() {
        AdTracking.reportRequestSummary(requestMetas)
    }

    fun trackEventStart(adResponse: AdResponse) {
        findAdPlacement(this.adUnitId)?.apply {
            val key = generateAdResponseKey(adResponse)
            requestMetas[key] = EventMeta(
                requestID = requestId,
                placementName = name,
                adTypeIL = this.adType.type,
                adItemIdIL = VendorUtil.findIDFromServerExtras(
                    adResponse,
                    adUnitId
                ),
                startTimeIL = System.currentTimeMillis(),
                adVendorNameIL = adResponse.customEventClassName
            )
        }

    }

    fun trackWaterFallItemFail(
        adResponse: AdResponse,
        errorCode: MoPubError
    ) {
        Log.d("TrackEvent", "trackWaterFallItemFail: $adResponse")
        var containsKey = requestMetas.containsKey(generateAdResponseKey(adResponse))
        if (containsKey) {
            requestMetas[generateAdResponseKey(adResponse)]?.also {
                it.requestResultIL = "${errorCode}-${errorCode.intCode}"
                it.endTimeIL = System.currentTimeMillis()
            }
        }
    }

    fun trackWaterFallSuccess(adResponse: AdResponse) {
        Log.d("TrackEvent", "trackWaterFallSuccess: $adResponse")
        var containsKey = requestMetas.containsKey(generateAdResponseKey(adResponse))
        if (containsKey) {
            requestMetas[generateAdResponseKey(adResponse)]?.apply {
                this.requestResultIL = "match"
                this.endTimeIL = System.currentTimeMillis()
            }
        }
        reportRequestSummaryInfo()
    }

    fun trackWaterFallFail() {
        Log.d("TrackEvent", "trackWaterFallFail: $requestId")
        reportRequestSummaryInfo()
    }


    fun reportChance() {
        findAdPlacement(adUnitId)?.apply {
            var eventMeta = EventMeta(
                requestId,
                name,
                chanceName,
                this.adType.type,
                startTimeIL = System.currentTimeMillis()
            )
            AdTracking.reportAdChance(eventMeta)
        }
    }


    fun reportReward(rewardKey: String, currentUnitId: String?) {
        Log.d("TrackEvent", "reportReward: $rewardKey,$currentUnitId")
        findAdPlacement(adUnitId)?.apply {
            var duration = 0
            if (showTimeMap.isNotEmpty()) {
                duration = (System.currentTimeMillis() - showTimeMap.entries.first().value).toInt()
            }
            val key = requestMetas.keys.find { it.contains(rewardKey) }
            val eventMeta = requestMetas[key]
            if (eventMeta != null) {
                AdTracking.reportAdReward(
                    EventMeta(
                        requestId,
                        name,
                        chanceName,
                        this.adType.type,
                        eventMeta.adItemIdIL,
                        eventMeta.adVendorNameIL,
                        duration = duration,
                        finish = if (currentUnitId.isNullOrEmpty()) 1 else 0
                    )
                )
            }
        }


    }

    fun reportClick(adResponse: AdResponse) {
        val hashCode = adResponse.hashCode()
        LogUtil.d("TrackEvent", "reportClick: $hashCode")
        val showTime = showTimeMap[hashCode]
        if (showTime != null) {
            trackClick(
                (System.currentTimeMillis() - showTime).toInt(), adResponse
            )
        } else {
            if (!clickTimeMap.containsKey(hashCode)) {
                clickTimeMap[hashCode] = System.currentTimeMillis()
            }
            trackClick(-1, adResponse)
        }
    }

    fun reportImpression(adResponse: AdResponse) {
        val hashCode = adResponse.hashCode()
        LogUtil.d("TrackEvent", "reportImpression: $hashCode")
        showTimeMap[hashCode] = System.currentTimeMillis()
        val clickTime = clickTimeMap[hashCode]
        if (clickTime != null) {
            trackImpression((showTimeMap[hashCode]!! - clickTime).toInt(), adResponse)
        } else {
            trackImpression(0, adResponse)
        }
    }


    private fun trackClick(duration: Int, adResponse: AdResponse) {
        Log.d("TrackEvent", "trackClick: $duration")
        val customEventClassName = adResponse.customEventClassName
        if (customEventClassName.isNullOrEmpty()) return
        findAdPlacement(adUnitId)?.apply {
            var eventMeta = EventMeta(
                if (adResponse.requestId.isNullOrEmpty()) requestId else adResponse.requestId!!,
                name,
                chanceName,
                this.adType.type,
                VendorUtil.findIDFromServerExtras(
                    adResponse,
                    adUnitId
                ),
                customEventClassName,
                duration = duration
            )
            AdTracking.reportAdClick(eventMeta)
        }

    }


    private fun trackImpression(duration: Int, adResponse: AdResponse) {
        Log.d("TrackEvent", "trackImpression: $duration")
        val customEventClassName = adResponse.customEventClassName
        if (customEventClassName.isNullOrEmpty()) return
        findAdPlacement(adUnitId)?.apply {
            var eventMeta = EventMeta(
                if (adResponse.requestId.isNullOrEmpty()) requestId else adResponse.requestId!!,
                name,
                chanceName,
                this.adType.type,
                VendorUtil.findIDFromServerExtras(
                    adResponse,
                    adUnitId
                ),
                customEventClassName,
                duration = duration
            )
            AdTracking.reportAdImpression(eventMeta)
        }
    }

    private fun generateAdResponseKey(adResponse: AdResponse): String {
        return "${adResponse.customEventClassName}_${
            VendorUtil.findIDFromServerExtras(
                adResponse,
                adResponse.adUnitId!!
            )
        }"
    }

}