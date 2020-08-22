package mobi.idealabs.ads.core.utils

import android.net.Uri
import android.util.Log
import androidx.annotation.Keep
import com.mopub.network.AdResponse
import com.mopub.network.ImpressionData
import mobi.idealabs.ads.core.controller.AdSdk

object VendorUtil {
    private val vendorIdStrategyList = mutableListOf(
        GoogleAdmobVendorStrategy,
        FacebookVendorStrategy,
        UnityVendorStrategy,
        ApplovinVendorStrategy,
        IronSourceVendorStrategy,
        TapjoyVendorStrategy,
        MoPubVendorStrategy
    )

    @Keep
    public fun addVendorIdStrategy(vendorIdStrategy: VendorIdStrategy) {
        vendorIdStrategyList.add(0, vendorIdStrategy)
    }

    fun changeVendorName(adVendorNameIL: String): String {
        if (adVendorNameIL.isEmpty()) return ""
        val vendorIdStrategy = vendorIdStrategyList.find { it.isVendorCustomEventClass(adVendorNameIL) }
        return vendorIdStrategy?.vendorName() ?: adVendorNameIL
    }

    fun findIDFromServerExtras(adResponse: AdResponse): String {
        val serverExtras = adResponse.serverExtras
        Log.d("VendorUtil", "findIDFromServerExtras: $serverExtras")

        if (serverExtras.isNullOrEmpty()) return ""
        val customEventClassName = adResponse.customEventClassName
        if (customEventClassName.isNullOrEmpty()) return ""
        return vendorIdStrategyList.find { it.isVendorCustomEventClass(customEventClassName) }
            ?.getVendorId(adResponse) ?: return ""
    }
}