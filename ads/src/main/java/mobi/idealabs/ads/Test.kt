package mobi.idealabs.ads

import android.app.Activity
import android.content.Context
import com.mopub.nativeads.MoPubNative
import com.mopub.nativeads.MoPubNative.MoPubNativeNetworkListener
import com.mopub.nativeads.NativeAd
import com.mopub.nativeads.NativeErrorCode
import com.mopub.nativeads.RequestParameters

class Test {
    class AdNative(
        context: Context,
        adUnitId: String,
        moPubNativeNetworkListener: MoPubNativeNetworkListener
    ) : MoPubNative(context, adUnitId, moPubNativeNetworkListener) {

    }

    private val mMoPubNativeNetworkListener: MoPubNativeNetworkListener =
        object : MoPubNativeNetworkListener {
            override fun onNativeLoad(p0: NativeAd?) {
            }

            override fun onNativeFail(p0: NativeErrorCode?) {
            }

        }

    fun loadAds(
        activity: Activity,
        adUnitId: String,
        requestParameters: RequestParameters?
    ) {
        loadAds(requestParameters, AdNative(activity, adUnitId, mMoPubNativeNetworkListener))
    }

    fun loadAds(
        requestParameters: RequestParameters?,
        moPubNative: MoPubNative?
    ) {
    }
}