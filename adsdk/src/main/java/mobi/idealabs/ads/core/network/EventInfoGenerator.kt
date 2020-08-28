package mobi.idealabs.ads.core.network

import com.google.gson.Gson
import com.google.gson.JsonObject
import mobi.idealabs.ads.core.bean.EventMeta
import mobi.idealabs.ads.core.bean.EventType
import mobi.idealabs.ads.core.utils.SystemUtil
import mobi.idealabs.ads.core.utils.VendorUtil

class EventInfoGenerator {
    val eventInfoMap = mutableMapOf<String, Any>()

    private val AppVersionNameIL = "AppVersionNameIL"
    private val AppVersionCodeIL = "AppVersionCodeIL"
    private val GeApiVersionIL = "GeApiVersionIL"
    private val AppBundleIdIL = "AppBundleIdIL"
    private val PlatformIL = "PlatformIL"
    private val CountryIL = "CountryIL"
    private val IpIL = "IpIL"
    private val GdprIL = "GdprIL"
    private val GoogleAdvertisingIdIL = "GoogleAdvertisingIdIL"
    private val UniversallyUniqueIdIL = "UniversallyUniqueIdIL"
    //---以上一次赋值不改变--//

    private val NetworkTypeIL = "NetworkTypeIL"
    private val PlacementNameIL = "PlacementNameIL"
    private val EventNameIL = "EventNameIL"
    private val EventTypeIL = "EventTypeIL"
    private val EventTimeIL = "EventTimeIL"
    private val RequestIdIL = "RequestIdIL"
    private val event_meta = "event_meta"
    private val request_list = "request_list"

    fun createEventInfo(@EventType name: String, vararg body: EventMeta): JsonObject {
        iniCommonInfo()
        val gson = Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create()

        addParams(NetworkTypeIL, SystemUtil.loadNetworkState())
        addParams(PlacementNameIL, body[0].placementName)
        addParams(RequestIdIL, body[0].requestID)
        addParams(EventNameIL, name)
        addParams(EventTimeIL, System.currentTimeMillis())
        var type = loadEventType(name)
        addParams(EventTypeIL, type)
        if (name == EventType.REQUEST_SUMMARY) {
            addParams(event_meta, mapOf(request_list to body.toList().apply {
                this.map {
                    it.adVendorNameIL = VendorUtil.changeVendorName(it.adVendorNameIL?:"")
                }
            }))
        } else {
            addParams(
                event_meta,
                body[0].apply { this.adVendorNameIL = VendorUtil.changeVendorName(adVendorNameIL?:"") })
        }
        return gson.fromJson(gson.toJson(eventInfoMap), JsonObject::class.java)

    }


    private fun loadEventType(@EventType type: String): Int {
        return when (type) {
            EventType.REQUEST_SUMMARY -> 0
            EventType.AD_CHANCE -> 1
            EventType.AD_IMPRESSION -> 2
            EventType.AD_CLICK -> 3
            EventType.AD_RETURN -> 4
            EventType.AD_REWARD -> 5
            else -> -1
        }
    }

    private fun iniCommonInfo() {
        if (eventInfoMap.isEmpty()) {
            addParams(AppVersionNameIL, SystemUtil.loadAppVersionName())
            addParams(AppVersionCodeIL, SystemUtil.loadAppVersionCode())
            addParams(GeApiVersionIL, SystemUtil.loadApiVersion())
            addParams(AppBundleIdIL, SystemUtil.loadAppBundleId())
            addParams(IpIL, SystemUtil.loadDevicesIP())
            addParams(GdprIL, SystemUtil.loadGDPRStatus())
            addParams(GoogleAdvertisingIdIL, SystemUtil.loadGoogleAdId())
            addParams(UniversallyUniqueIdIL, SystemUtil.loadUUID())
            addParams(PlatformIL, SystemUtil.loadPlatform())
            addParams(CountryIL, SystemUtil.loadCountryNumber())
        }
    }

    private fun addParams(key: String, value: Any) {
        eventInfoMap[key] = value
    }


}