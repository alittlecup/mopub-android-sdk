package mobi.idealabs.ads.core.controller

import android.app.Application
import androidx.core.app.ComponentActivity
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.privacy.ConsentDialogListener
import com.mopub.mobileads.MoPubErrorCode
import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.IVTUserLevel


typealias AdInitListener = () -> Unit

object AdSdk {
    internal var application: Application? = null
    internal var logAble: Boolean = true
    var canRetry: Boolean = true
    var ivtUserLevelListener: ((IVTUserLevel) -> Unit)? = null

    @JvmStatic
    @Synchronized
    fun initAdSdk(
        context: ComponentActivity,
        sdkConfiguration: SdkConfiguration,
        adSdkInitStrategy: AdSdkInitStrategy,
        adInitListener: AdInitListener
    ) {
        if (MoPub.isSdkInitialized()) return
        ActivityLifeManager.initWithActivity(context)
        application = context.application
        logAble = adSdkInitStrategy.logAble
        canRetry = adSdkInitStrategy.canRetry
        MoPub.initializeSdk(context, sdkConfiguration) {
            sdkInitConfig = sdkConfiguration
            AdManager.initWithActivity(context)
            this.adSdkInitStrategy=adSdkInitStrategy
            adInitListener.invoke()
        }
    }

    private var adSdkInitStrategy: AdSdkInitStrategy? = null


    fun findAdPlacementByChanceName(chanceName:String):AdPlacement?{
        return adSdkInitStrategy?.findAdPlacementByChanceName(chanceName)
    }
    fun findAdPlacement(adUnitId: String): AdPlacement? {
        return adSdkInitStrategy?.findAdPlacementByAdUnitId(adUnitId)
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

