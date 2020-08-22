package mobi.idealabs.editor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import androidx.core.util.Preconditions
import com.mopub.common.BaseAdapterConfiguration
import com.mopub.common.OnNetworkInitializationFinishedListener
import com.mopub.common.SharedPreferencesHelper
import com.mopub.mobileads.MoPubErrorCode
import com.smaato.sdk.core.Config
import com.smaato.sdk.core.SmaatoSdk
import com.smaato.sdk.core.log.LogLevel
import mobi.idealabs.ads.core.utils.SystemUtil

@Keep
@SuppressLint("RestrictedApi")
class SmaatoAdapterConfiguration : BaseAdapterConfiguration() {
    override fun initializeNetwork(
        context: Context,
        configuration: MutableMap<String, String>?,
        listener: OnNetworkInitializationFinishedListener
    ) {
        Preconditions.checkNotNull(context)
        Preconditions.checkNotNull(listener)
        val publishId = configuration?.get("publishId")
        if (publishId.isNullOrEmpty()) {
            listener.onNetworkInitializationFinished(
                SmaatoAdapterConfiguration::class.java,
                MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR
            )
        } else {
            var application = if (context is Activity) {
                context.application
            } else if (context is Application) context else null
            if (application == null) {
                listener.onNetworkInitializationFinished(
                    SmaatoAdapterConfiguration::class.java,
                    MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR
                )
            } else {
                val config = Config.builder()
                    .setLogLevel(if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.ERROR)
                    .setHttpsOnly(false)
                    .build()
                SmaatoSdk.init(application, config, publishId)
                var editor = SharedPreferencesHelper.getSharedPreferences(context).edit()
                editor.putBoolean("IABConsent_CMPPresent", false)
                editor.putString(
                    "IABConsent_SubjectToGDPR",
                    if (SystemUtil.loadGDPRStatus() == "0") "1" else if (SystemUtil.loadGDPRStatus() == "1") "0" else "2"
                )
                listener.onNetworkInitializationFinished(
                    SmaatoAdapterConfiguration::class.java,
                    MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS
                )
            }
        }
    }

    override fun getAdapterVersion(): String {
        return "1.0.0"
    }

    override fun getMoPubNetworkName(): String {
        return "Smaato"
    }

    override fun getBiddingToken(p0: Context): String? {
        return null
    }

    override fun getNetworkSdkVersion(): String {
        return SmaatoSdk.getVersion()
    }
}