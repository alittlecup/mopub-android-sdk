package mobi.idealabs.ads.report

import android.util.Log
import androidx.annotation.Keep
import com.mopub.mobileads.MoPubError
import com.mopub.network.AdResponse
import mobi.idealabs.ads.bean.EventMeta
import mobi.idealabs.ads.manage.AdManager
import mobi.idealabs.ads.manage.AdSdk.findAdPlacement
import mobi.idealabs.ads.report.utils.LogUtil
import mobi.idealabs.ads.report.utils.VendorUtil

/**
 * 统一的数据收集和上报类
 */
@Keep
class TrackEvent(private val adUnitId: String, private val requestId: String) : EventInterface {
    private val requestMetas = mutableMapOf<String, EventMeta>()
    private val showTimeMap = mutableMapOf<Int, Long>()
    private val clickTimeMap = mutableMapOf<Int, Long>()

    init {
        Log.d("TrackEvent", "init: $adUnitId-$requestId")
    }

    override fun trackMoPubRequestStart() {
        Log.d("TrackEvent", "trackMoPubRequestStart: ")
        findAdPlacement(adUnitId)?.apply {
            AdManager.mGlobalAdListener?.onAdStartLoad(this)
            this.findActiveListeners(this).forEach { it.onAdStartLoad(this) }
        }
    }


    private fun reportRequestSummaryInfo() {
        AdTracking.reportRequestSummary(requestMetas)
    }

    override fun trackEventStart(
        adUnitId: String,
        eventName: String,
        adResponse: AdResponse
    ) {
        Log.d("TrackEvent", "trackEventStart:$requestId- $adUnitId-$eventName-$adResponse")
        findAdPlacement(this.adUnitId)?.apply {
            requestMetas[eventName] = EventMeta(
                requestID = requestId,
                placementName = name,
                adTypeIL = this.adType.type,
                adItemIdIL = VendorUtil.findIDFromServerExtras(
                    adResponse,
                    eventName,
                    adUnitId
                ),
                startTimeIL = System.currentTimeMillis(),
                adVendorNameIL = eventName
            )
        }

    }

    override fun getCurrentRequestId(): String {
        return requestId
    }


    override fun trackWaterFallItemFail(
        key: String?,
        errorCode: MoPubError
    ) {
        Log.d("TrackEvent", "trackWaterFallItemFail: $key")
        var containsKey = requestMetas.containsKey(key)
        if (containsKey) {
            requestMetas[key]?.also {
                it.requestResultIL = "${errorCode}-${errorCode.intCode}"
                it.endTimeIL = System.currentTimeMillis()
            }
        }
    }

    override fun trackWaterFallSuccess(key: String) {
        Log.d("TrackEvent", "trackWaterFallSuccess: $key - $requestId")
        var containsKey = requestMetas.containsKey(key)
        if (containsKey) {
            requestMetas[key]?.apply {
                this.requestResultIL = "match"
                this.endTimeIL = System.currentTimeMillis()
            }
        }
        reportRequestSummaryInfo()
    }

    override fun trackWaterFallFail() {
        Log.d("TrackEvent", "trackWaterFallFail: $requestId")
        reportRequestSummaryInfo()
    }

    override fun trackMoPubRequestSuccess() {
        Log.d("TrackEvent", "trackMoPubRequestResponseSuccess: $requestId")
    }

    override fun reportChance() {
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

    override fun reportRequestSummary() {
        Log.d("TrackEvent", "reportRequestSummary: ")
        reportRequestSummaryInfo()
    }

    override fun reportReward(customRewardClassName: String) {
        Log.d(
            "TrackEvent",
            "reportReward: $customRewardClassName : ${customRewardClassName.isNullOrEmpty()}"
        )
        findAdPlacement(adUnitId)?.apply {
            var duration = 0
            if (showTimeMap.isNotEmpty()) {
                duration = (System.currentTimeMillis() - showTimeMap.entries.first().value).toInt()
            }
            val eventMeta = requestMetas[customRewardClassName]
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
                        finish = if (customRewardClassName.isNullOrEmpty()) 1 else 0
                    )
                )
            }
        }


    }

    override fun reportClick(adResponse: AdResponse) {
        val hashCode = adResponse.hashCode()
        LogUtil.d("TrackEvent", "reportClick: $hashCode")
        val showTime = showTimeMap[hashCode]
        if (showTime != null) {
            trackClick(
                (System.currentTimeMillis() - showTime).toInt()
                , adResponse
            )
        } else {
            if (!clickTimeMap.containsKey(hashCode)) {
                clickTimeMap[hashCode] = System.currentTimeMillis()
            }
            trackClick(-1, adResponse)
        }
    }

    override fun reportImpression(adResponse: AdResponse) {
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
                    customEventClassName,
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
                    customEventClassName,
                    adUnitId
                ),
                customEventClassName,
                duration = duration
            )
            AdTracking.reportAdImpression(eventMeta)
        }
    }

}