package mobi.idealabs.editor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.simpleadsdemo.AdUnitDataSource
import com.mopub.simpleadsdemo.MoPubSampleActivity
import com.mopub.simpleadsdemo.MoPubSampleAdUnit
import mobi.idealabs.ads.bean.AdErrorCode
import mobi.idealabs.ads.bean.AdListener
import mobi.idealabs.ads.bean.AdPlacement
import mobi.idealabs.ads.bean.AdType
import mobi.idealabs.ads.manage.AdManager
import mobi.idealabs.ads.manage.AdSdk
import mobi.idealabs.ads.manage.AdSdkInitConfig
import mobi.idealabs.ads.report.utils.SystemUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main1)
        initAd()

    }

    private fun initAd() {

        val sdkConfig = AdSdkInitConfig(
            createSdkConfig(true, AdConst.TestNativeID),
            adPlacements = AdConst.adPlacements,
            logAble = true
        ) { chance ->
            AdConst.InterstitialAdPlacement
        }
        val loadCustomUserId = SystemUtil.loadCustomUserId(this)
        Log.d("MainActivity", "initAd: $loadCustomUserId")
        AdSdk.initAdSdk(this, sdkConfig) {
            toast(" Ads initSuccess ")
            insertAdsAdPlacement()
            startActivity(Intent(this, MoPubSampleActivity::class.java))
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
       return when(adType){
            AdType.BANNER->MoPubSampleAdUnit.AdType.BANNER
            AdType.INTERSTITIAL->MoPubSampleAdUnit.AdType.INTERSTITIAL
            AdType.NATIVE->MoPubSampleAdUnit.AdType.MANUAL_NATIVE
            AdType.REWARDED_VIDEO->MoPubSampleAdUnit.AdType.REWARDED_VIDEO
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
            .build()
    }
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String) {
    Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show()
}

