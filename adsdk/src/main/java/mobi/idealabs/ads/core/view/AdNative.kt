package mobi.idealabs.ads.core.view

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.mopub.nativeads.*
import mobi.idealabs.ads.R
import mobi.idealabs.ads.core.bean.AdErrorCode
import mobi.idealabs.ads.core.bean.AdNativeListener
import mobi.idealabs.ads.core.controller.AdNativeController
import mobi.idealabs.ads.core.controller.AdNativeController.adNativeListener

class NativeNetworkListenerWrapper(private val source: MoPubNative.MoPubNativeNetworkListener? = null) :
    MoPubNative.MoPubNativeNetworkListener {
    var injectListeners: MoPubNative.MoPubNativeNetworkListener? = null
    override fun onNativeLoad(p0: NativeAd?) {
        source?.onNativeLoad(p0)
        injectListeners?.onNativeLoad(p0)
    }

    override fun onNativeFail(p0: NativeErrorCode?) {
        source?.onNativeFail(p0)
        injectListeners?.onNativeFail(p0)

    }
}

class AdNative(
    context: Context,
    val adUnitId: String,
    mopubNativeListener: MoPubNativeNetworkListener
) : MoPubNative(context, adUnitId, NativeNetworkListenerWrapper(mopubNativeListener)) {
    private val context = if (context is Activity) context.application else context

    private var nativeAd: NativeAd? = null
    private val nativeNetWorkListener = object : MoPubNativeNetworkListener {

        override fun onNativeLoad(nativeAd: NativeAd?) {
            Log.d("AdNative", "onNativeLoad: $nativeAd")
            this@AdNative.nativeAd = nativeAd
            adNativeListener.onNativeLoaded(this@AdNative)
            nativeAd?.setMoPubNativeEventListener(nativeEventListener)
        }

        override fun onNativeFail(errorCode: NativeErrorCode?) {
            Log.d("AdNative", "onNativeFail: ")
            adNativeListener.onNativeFailed(
                this@AdNative,
                AdErrorCode(errorCode?.name, errorCode?.intCode)
            )
        }
    }

    init {
        val moPubNativeNetworkListener = moPubNativeNetworkListener
        if (moPubNativeNetworkListener is NativeNetworkListenerWrapper) {
            moPubNativeNetworkListener.injectListeners = nativeNetWorkListener
        }
    }

    private val nativeEventListener =
        object : NativeAd.MoPubNativeEventListener {
            override fun onImpression(view: View?) {
                Log.d("AdNative", "onImpression: $view")
                adNativeListener.onNativeShown(this@AdNative)
            }

            override fun onClick(view: View?) {
                Log.d("AdNative", "onClick: $view")
                adNativeListener.onNativeClicked(this@AdNative)
            }
        }
    override fun destroy() {
        adNativeListener.onNativeDestroy(this)
        super.destroy()
    }

}




