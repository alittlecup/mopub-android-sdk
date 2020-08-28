package mobi.idealabs.ads.core.utils

import android.net.Uri
import com.mopub.network.AdResponse
import com.mopub.network.ImpressionData

public abstract class VendorIdStrategy {
    abstract fun vendorName(): String
    open fun isVendorCustomEventClass(customEventClassName: String): Boolean {
        return customEventClassName.contains(vendorName(), true)
    }

    abstract fun getVendorId(adResponse: AdResponse): String
}

object GoogleAdmobVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "admob"
    }

    override fun isVendorCustomEventClass(customEventClassName: String): Boolean {
        return customEventClassName.contains("google", true)
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        var adUnitID = serverExtras["adUnitID"]
        if (adUnitID.isNullOrEmpty()) {
            adUnitID = serverExtras["adunit"]
        }
        return adUnitID ?: ""
    }
}

object UnityVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "unity"
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        var gameId = serverExtras["gameId"]
        var placementId = when {
            serverExtras.containsKey("placementId") -> serverExtras["placementId"]
            serverExtras.containsKey("zoneId") -> serverExtras["zoneId"]
            else -> ""
        }
        return "${placementId}_${gameId}"
    }

}

object FacebookVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "facebook"
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        return serverExtras["placement_id"] ?: ""
    }

}

object ApplovinVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "applovin"
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        return serverExtras["zone_id"] ?: ""
    }

}

object TapjoyVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "tapjoy"
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        return serverExtras["name"] ?: ""
    }

}


object MoPubVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "mopub"
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val mopubId = adResponse.adUnitId ?: ""
        val moPubGroupId = getMoPubGroupId(adResponse)
        return if (moPubGroupId.isEmpty()) {
            mopubId
        } else {
            "${mopubId}_${moPubGroupId}"
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

}

object IronSourceVendorStrategy : VendorIdStrategy() {
    override fun vendorName(): String {
        return "ironsource"
    }

    override fun getVendorId(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        return serverExtras["instanceId"] ?: ""
    }
}

