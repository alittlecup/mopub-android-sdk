package mobi.idealabs.ads.core.controller

import android.app.Application
import androidx.core.app.ComponentActivity
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.privacy.ConsentDialogListener
import com.mopub.mobileads.MoPubErrorCode
import mobi.idealabs.ads.core.bean.AdPlacement


data class AdSdkInitConfig(
    val sdkConfiguration: SdkConfiguration, val adPlacements: List<AdPlacement>,
    val logAble: Boolean = false,
    val canRetry: Boolean = true,
    val adPlacementFinder: mobi.idealabs.ads.core.bean.AdPlacementFinder
)

object AdSdk {
    internal var application: Application? = null
    internal var adPlacementFinder: mobi.idealabs.ads.core.bean.AdPlacementFinder? = null
    internal var logAble: Boolean = true
    internal var adPlacements: List<AdPlacement>? = null
    var canRetry: Boolean = true

    @JvmStatic
    @Synchronized
    fun initAdSdk(
        context: ComponentActivity,
        adSdkInitConfig: AdSdkInitConfig,
        adInitListener: mobi.idealabs.ads.core.bean.AdInitListener
    ) {


        if (MoPub.isSdkInitialized()) return
        ActivityLifeManager.initWithActivity(context)
        application = context.application
        logAble = adSdkInitConfig.logAble
        canRetry = adSdkInitConfig.canRetry
        MoPub.initializeSdk(
            context, adSdkInitConfig.sdkConfiguration
        ) {
            sdkInitConfig = adSdkInitConfig.sdkConfiguration
            AdManager.initWithActivity(context)
            adPlacementFinder = adSdkInitConfig.adPlacementFinder
            adPlacements = adSdkInitConfig.adPlacements
            adInitListener.invoke()
        }
    }

    fun findAdPlacement(adUnitId: String): AdPlacement? {
        return adPlacements?.find { it.adUnitId == adUnitId }
    }

    internal var sdkInitConfig: SdkConfiguration? = null

    fun showAdGPDR() {
        var personalInformationManager = MoPub.getPersonalInformationManager()
        personalInformationManager?.apply {
            if (isConsentDialogReady) {
                showConsentDialog()
            }
        }
    }

    fun shouldShowGDPR(): Boolean {
        var shouldShow = MoPub.getPersonalInformationManager()?.shouldShowConsentDialog() ?: false
        if (shouldShow) {
            MoPub.getPersonalInformationManager()
                ?.loadConsentDialog(object : ConsentDialogListener {
                    override fun onConsentDialogLoaded() {
                    }

                    override fun onConsentDialogLoadFailed(moPubErrorCode: MoPubErrorCode) {
                    }
                })
        }
        return shouldShow
    }
}

