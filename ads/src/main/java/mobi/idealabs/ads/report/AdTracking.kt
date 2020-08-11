package mobi.idealabs.ads.report

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.JsonObject
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import mobi.idealabs.ads.bean.DeviceInfo
import mobi.idealabs.ads.bean.EventMeta
import mobi.idealabs.ads.bean.EventType
import mobi.idealabs.ads.report.utils.LogUtil
import mobi.idealabs.ads.report.utils.SystemUtil
import mobi.idealabs.ads.report.network.EventInfoGenerator
import mobi.idealabs.ads.report.network.RemoteRepository

object AdTracking {
    /**
     * request_summary
     * ad_chance
     * ad_impression
     * ad_click
     * ad_return
     * ad_reward
     */
    val service = RemoteRepository.service
    var googleAdId: String = ""
    private var deviceInfo: DeviceInfo? = null

    @SuppressLint("CheckResult")
    fun reportDeviceInfo(context: Context) {
        deviceInfo =
            createDeviceInfo(context)
        if (deviceInfo == null) return
        if (isDailyFirst(context)) {
            service.postDeviceInfo(
                deviceInfo!!)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    LogUtil.d("AdTracking", "reportDeviceInfo: ${it.string()}")
                    SystemUtil.saveToday()
                },
                    { LogUtil.d("AdTracking", "reportDeviceInfo: ${it.message}") })
        }
    }

    private fun isDailyFirst(context: Context): Boolean {
        val day = SystemUtil.loadCurrentDay()
        return day != SystemUtil.loadPreviewDay(context)
    }


    private fun createDeviceInfo(context: Context): DeviceInfo {
        return DeviceInfo(
            SystemUtil.loadApiVersion(),
            SystemUtil.loadGDPRStatus(),
            SystemUtil.loadGoogleAdId(),
            SystemUtil.loadCpuNumber(),
            SystemUtil.loadFingerPrint(),
            SystemUtil.loadDevicesIP(),
            SystemUtil.loadRamSize(),
            SystemUtil.loadScreenSize(),
            SystemUtil.loadSystemVersion(),
            SystemUtil.loadCountryNumber(),
            SystemUtil.loadCountrySource(),
            SystemUtil.loadPlatform(),
            SystemUtil.loadDeviceType(),
            SystemUtil.loadDeviceBand(),
            SystemUtil.loadDeviceModel(),
            SystemUtil.loadLanguage(),
            SystemUtil.loadAndroidSdkVersion(),
            SystemUtil.loadUUID(),
            SystemUtil.loadTimeZone(),
            SystemUtil.loadAppBundleId(),
            SystemUtil.loadAppVersionCode(),
            SystemUtil.loadAppVersionName()
        )
    }

    fun reportRequestSummary(eventMetas: Map<String, EventMeta>) {
        if (eventMetas.isEmpty() || eventMetas.values.toList()[0].placementName.isEmpty()) return
        reportEventBody(
            Flowable.just(
                eventMetas
            ).filter { eventMetas.isNotEmpty() }
                .map { eventMetas.values.toList() }
                .map {
                    var metas = Array(it.size) { index ->
                        it[index]
                    }
                    val createEventInfo =
                        EventInfoGenerator()
                            .createEventInfo(EventType.REQUEST_SUMMARY, *metas)
                    LogUtil.d("AdTracking", "reportRequestSummary: $createEventInfo")
                    createEventInfo
                })

    }

    fun reportAdChance(body: EventMeta) {
        if (body.placementName.isEmpty()) return
        reportEventBody(
            Flowable.just(body).map {
                val createEventInfo =
                    EventInfoGenerator()
                        .createEventInfo(EventType.AD_CHANCE, body)
                LogUtil.d("AdTracking", "reportAdChance: $createEventInfo")
                createEventInfo
            })
    }

    fun reportAdImpression(body: EventMeta) {
        if (body.placementName.isEmpty()) return
        reportEventBody(
            Flowable.just(body).map {
                val createEventInfo =
                    EventInfoGenerator()
                        .createEventInfo(EventType.AD_IMPRESSION, body)
                LogUtil.d("AdTracking", "reportAdImpression: $createEventInfo")
                createEventInfo
            })
    }

    fun reportAdClick(body: EventMeta) {
        if (body.placementName.isEmpty()) return
        clickTime = System.currentTimeMillis()
        clickMeta = body
        observerTopStackActivity()
        reportEventBody(
            Flowable.just(body).map {
                val createEventInfo =
                    EventInfoGenerator()
                        .createEventInfo(EventType.AD_CLICK, body)
                LogUtil.d("AdTracking", "reportAdClick: $createEventInfo")
                createEventInfo
            })
    }

    fun reportAdReturn(body: EventMeta) {
        if (body.placementName.isEmpty()) return
        reportEventBody(
            Flowable.just(body).map {
                val createEventInfo =
                    EventInfoGenerator()
                        .createEventInfo(EventType.AD_RETURN, body)
                LogUtil.d("AdTracking", "reportAdReturn: $createEventInfo")
                createEventInfo
            })
    }

    fun reportAdReward(body: EventMeta) {
        if (body.placementName.isEmpty()) return
        reportEventBody(
            Flowable.just(body).map {
                val createEventInfo =
                    EventInfoGenerator()
                        .createEventInfo(EventType.AD_REWARD, body)
                LogUtil.d("AdTracking", "reportAdReward: $createEventInfo")
                createEventInfo
            })
    }


    @SuppressLint("CheckResult")
    private fun reportEventBody(flowable: Flowable<JsonObject>) {
        flowable.flatMap {
            service.postEventInfo(it)

        }.subscribeOn(Schedulers.io())
            .subscribe({ LogUtil.d("AdTracking", "reportEventResponse: ${it.string()}") },
                { LogUtil.d("AdTracking", "reportEventResponse: ${it.message}") })
    }

    private var clickTime = -1L
    private var clickMeta: EventMeta? = null
    private var inBackground = false

    private val topActivityObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            Log.d(
                "AdTracking",
                "onStateChanged: $event ${source.lifecycle.currentState}  ${ActivityLifeManager.clickActivityName}"
            )
            when (source.lifecycle.currentState) {
                Lifecycle.State.CREATED -> {
                    if (clickTime != -1L && !inBackground) {
                        inBackground = true
                        LogUtil.d("AdTracking", "onStateChanged: click current Activity dismiss")
                    }
                }
                Lifecycle.State.RESUMED -> {
                    if (inBackground) {
                        Log.d("AdTracking", "onStateChanged: current Activity show")
                        tryReportReturn()
                        source.lifecycle.removeObserver(this)
                        ActivityLifeManager.clickActivityName=""
                        inBackground = false
                    }
                }
                Lifecycle.State.DESTROYED->{
                    if (inBackground) {
                        Log.d("AdTracking", "onStateChanged: current Activity destroy")
                        tryReportReturn()
                        source.lifecycle.removeObserver(this)
                        ActivityLifeManager.clickActivityName=""
                        inBackground = false
                    }
                }
            }
        }

    }

    private fun tryReportReturn() {
        if (clickTime != -1L) {
            clickMeta?.apply {
                this.duration = (System.currentTimeMillis() - clickTime).toInt()
                reportAdReturn(this)
            }
            clickTime = -1
            clickMeta = null
        }
    }

    private fun observerTopStackActivity() {
        val topActivity = ActivityLifeManager.topActivity.get()
        if (topActivity != null) {
            if (topActivity is LifecycleOwner) {
                topActivity.lifecycle.addObserver(topActivityObserver)
            } else {
                val name = topActivity::class.java.name
                if (ActivityLifeManager.clickActivityName != name) {
                    ActivityLifeManager.clickActivityName = name
                    ActivityLifeManager.lifecycle.addObserver(topActivityObserver)
                }
            }
        }
    }
}
