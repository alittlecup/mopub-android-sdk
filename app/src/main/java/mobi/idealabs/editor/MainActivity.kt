package mobi.idealabs.editor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.SmaatoAdapterConfiguration
import com.mopub.simpleadsdemo.AdUnitDataSource
import com.mopub.simpleadsdemo.MoPubSampleActivity
import com.mopub.simpleadsdemo.MoPubSampleAdUnit
import mobi.idealabs.ads.core.bean.AdErrorCode
import mobi.idealabs.ads.core.bean.AdListener
import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.AdType
import mobi.idealabs.ads.core.controller.AdManager
import mobi.idealabs.ads.core.controller.AdSdk
import mobi.idealabs.ads.core.controller.DefaultAdSdkInitStrategy
import mobi.idealabs.ads.core.controller.IVTAdSdkInitStrategy
import mobi.idealabs.ads.core.utils.SystemUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        initAd()
        findViewById<Button>(R.id.btn_mopub).setOnClickListener {
            startActivity(Intent(this, MoPubSampleActivity::class.java))
        }
        findViewById<Button>(R.id.btn_sdk).setOnClickListener {
            startActivity(Intent(this, AdSdkActivity::class.java))
        }

    }

    private fun initAd() {
        val loadCustomUserId = SystemUtil.loadCustomUserId(this)
        val sdkConfiguration = createSdkConfig(true, AdConst.TestNativeID)

        Log.d("MainActivity", "initAd: $loadCustomUserId")
        AdSdk.initAdSdk(this, sdkConfiguration, createDefaultAdSdkInitStrategy()) {
            toast(" Ads initSuccess ")
            insertAdsAdPlacement()
            AdManager.enableSecondNativeCache(AdConst.NativeAdPlacement.adUnitId)
            AdManager.mGlobalAdListener = object : AdListener {
                override fun onAdStartLoad(adPlacement: AdPlacement?) {
                    toast("START_LOAD $adPlacement")
                    Log.d("MainActivity", "onAdStartLoad: $adPlacement")
                }

                override fun onAdLoaded(adPlacement: AdPlacement?) {
                    toast("SUCCEED $adPlacement")
                    Log.d("MainActivity", "onAdLoadSucceed: $adPlacement")
                }


                override fun onAdFailed(adPlacement: AdPlacement?, adErrorCode: AdErrorCode) {
                    toast("FAILED $adPlacement ")
                    Log.d("MainActivity", "onAdLoadFailed: $adPlacement")
                }

                override fun onAdShown(adPlacement: AdPlacement?) {
                    toast("SHOW $adPlacement ")

                    Log.d("MainActivity", "onAdShown: $adPlacement")
                }

                override fun onAdDismissed(adPlacement: AdPlacement?) {
                    toast("DISMISS $adPlacement")

                    Log.d("MainActivity", "onAdDismissed: $adPlacement")
                }

                override fun onAdClicked(adPlacement: AdPlacement?) {
                    toast("CLICK $adPlacement ")

                    Log.d("MainActivity", "onAdClicked: $adPlacement")
                }

            }
        }
    }

    private fun insertAdsAdPlacement() {
        val adUnitDataSource = AdUnitDataSource(this)
        AdConst.adPlacements.forEach {
            val build = MoPubSampleAdUnit.Builder(it.adUnitId, chanageType(it.adType))
                .description(it.name)
                .isUserDefined(true)
                .build()
            adUnitDataSource.createSampleAdUnit(build)
        }
    }

    private fun chanageType(adType: AdType): MoPubSampleAdUnit.AdType? {
        return when (adType) {
            AdType.BANNER -> MoPubSampleAdUnit.AdType.BANNER
            AdType.INTERSTITIAL -> MoPubSampleAdUnit.AdType.INTERSTITIAL
            AdType.NATIVE -> MoPubSampleAdUnit.AdType.MANUAL_NATIVE
            AdType.REWARDED_VIDEO -> MoPubSampleAdUnit.AdType.REWARDED_VIDEO
        }
    }

    private fun createSdkConfig(logAble: Boolean, adUnitId: String): SdkConfiguration {
        return SdkConfiguration.Builder(adUnitId)
            .withLogLevel(if (logAble) MoPubLog.LogLevel.DEBUG else MoPubLog.LogLevel.NONE)
            .withMediatedNetworkConfiguration(
                "com.mopub.mobileads.TapjoyAdapterConfiguration",
                mapOf("sdkKey" to "Jzdbf1-iRZqIX-607AK9AwECZxoknGyUqdaXDPuNPu75c7XltL5BUvOxqBAX")
            ).withMediatedNetworkConfiguration(
                "com.mopub.mobileads.UnityAdsAdapterConfiguration",
                mapOf("gameId" to "f8f6cd4f-4745-445a-bc18-a9b8ff89d36f")
            ).withMediatedNetworkConfiguration(
                "com.mopub.mobileads.IronSourceAdapterConfiguration",
                mapOf("applicationKey" to "d01ee1b5")
            )
            .withAdditionalNetwork("mobi.idealabs.editor.SmaatoAdapterConfiguration")
            .withMediatedNetworkConfiguration(
                "mobi.idealabs.editor.SmaatoAdapterConfiguration",
                mapOf("publishId" to "1100044852")
            )
            .withAdditionalNetwork("com.mopub.mobileads.InMobiAdapterConfiguration")
            .withMediatedNetworkConfiguration("com.mopub.mobileads.InMobiAdapterConfiguration",
                mapOf("accountId" to "6e6f3efe3756430a9d40136f5779c287"))
            .build()
    }

    private fun createDefaultAdSdkInitStrategy(): DefaultAdSdkInitStrategy {
        return object :
            DefaultAdSdkInitStrategy(AdConst.adPlacements, logAble = true, canRetry = true) {
            override fun findAdPlacementByChanceName(chanceName: String): AdPlacement? {
                return adPlacements.find { it.name == chanceName }
            }

        }
    }
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show()
}

