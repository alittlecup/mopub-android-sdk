package mobi.idealabs.ads.core.utils

import android.net.Uri
import android.util.Log
import com.mopub.network.AdResponse
import com.mopub.network.ImpressionData
import mobi.idealabs.ads.core.controller.AdSdk

object VendorUtil {
    val vendorNames = listOf("unity", "facebook", "google", "smaato", "applovin", "tapjoy", "mopub")

    fun changeVendorName(adVendorNameIL: String?): String {
        if (adVendorNameIL.isNullOrEmpty()) return ""
        var vendorName = vendorNames.find { adVendorNameIL.contains(it, true) }
        if (vendorName == "google") {
            vendorName = "admob"
        }
        return vendorName ?: adVendorNameIL
    }

    fun findIDFromServerExtras(
        adResponse: AdResponse,
        mopubId: String
    ): String {
        var id = ""
        val serverExtras = adResponse.serverExtras
        if (serverExtras.isNullOrEmpty()) return id

        val simpleVendorName = changeVendorName(adResponse.customEventClassName)
        val vendorAdTypeId = getVendorAdTypeId(simpleVendorName, adResponse, mopubId)

        Log.d("VendorUtil", "findIDFromServerExtras: $serverExtras")

        return vendorAdTypeId
    }

    private fun getVendorAdTypeId(
        simpleVendorName: String,
        adResponse: AdResponse,
        mopubId: String
    ): String {
        val serverExtras = adResponse.serverExtras
        return when (simpleVendorName) {
            "admob" -> {
                var adUnitID = serverExtras["adUnitID"]
                if (adUnitID.isNullOrEmpty()) {
                    adUnitID = serverExtras["adunit"]
                }
                adUnitID ?: ""
            }
            "unity" -> {
                var gameId = serverExtras["gameId"]
                var placementId = when {
                    serverExtras.containsKey("placementId") -> serverExtras["placementId"]
                    serverExtras.containsKey("zoneId") -> serverExtras["zoneId"]
                    else -> ""
                }
                "${placementId}_${gameId}"
            }
            "facebook" -> {
                val placement_id = serverExtras["placement_id"]
                placement_id ?: ""
            }
            "smaato" -> {
                var adSpaceId =
                    serverExtras.get("adSpaceId") ?: serverExtras["adspaceId"] ?: ""
                val localParams = loadVendorLocalParams("Smaato")
                if (localParams.containsKey("publishId")) {
                    adSpaceId += "_${localParams["publishId"]}"
                }
                return adSpaceId
            }
            "applovin" -> {
                val zone_id = serverExtras.get("zone_id")
                zone_id ?: ""
            }
            "mopub" -> {
                val moPubGroupId = getMoPubGroupId(adResponse)
                return if (moPubGroupId.isEmpty()) {
                    mopubId
                } else {
                    "${mopubId}_${moPubGroupId}"
                }
            }
            else -> ""
        }

    }


    private fun getMoPubGroupId(adResponse: AdResponse): String {

        val impressionData = adResponse.impressionData
        if (impressionData != null && !impressionData.adGroupId.isNullOrEmpty()) {
            return impressionData.adGroupId!!
        } else {
            val adGroupId = loadAdGroupFromUrl(adResponse.clickTrackingUrl)
            if (adGroupId.isNullOrEmpty()) {
                val impressionTrackingUrls = adResponse.impressionTrackingUrls
                impressionTrackingUrls.forEach {
                    val groupId = loadAdGroupFromUrl(it)
                    if (!groupId.isNullOrEmpty()) {
                        return groupId
                    }
                }

            } else {
                return adGroupId
            }
        }
        return ""
    }

    private fun loadAdGroupFromUrl(url: String?): String? {
        return try {
            Uri.parse(url).getQueryParameter(ImpressionData.ADGROUP_ID)
        } catch (e: Exception) {
            ""
        }
    }

    private fun loadVendorLocalParams(vendorName: String): Map<String, String> {
        val vendorKey = AdSdk.sdkInitConfig?.mediatedNetworkConfigurations?.keys?.find {
            it.contains(
                vendorName,
                true
            )
        }
        val map = AdSdk.sdkInitConfig?.mediatedNetworkConfigurations?.get(vendorKey)
        return map ?: mapOf()
    }
}