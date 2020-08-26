package mobi.idealabs.ads.core.view

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.idealabs.adlib.R
import com.mopub.nativeads.*
import mobi.idealabs.ads.core.bean.AdErrorCode
import mobi.idealabs.ads.core.bean.AdNativeListener

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
    val context: Context,
    val adUnitId: String,
    mopubNativeListener: MoPubNativeNetworkListener
) : MoPubNative(context, adUnitId, NativeNetworkListenerWrapper(mopubNativeListener)) {


    private var nativeAd: NativeAd? = null
    private val nativeNetWorkListener = object : MoPubNativeNetworkListener {

        override fun onNativeLoad(nativeAd: NativeAd?) {
            Log.d("AdNative", "onNativeLoad: $nativeAd")
            isLoading = false
            this@AdNative.nativeAd = nativeAd
            adNativeListener?.onNativeLoaded(this@AdNative)
            if (tempFrameLayout != null) {
                var showAd = showAd()
                if (showAd != tempFrameLayout) {
                    tempFrameLayout!!.addView(showAd, FrameLayout.LayoutParams(-1, -1))
                }
            }
        }

        override fun onNativeFail(errorCode: NativeErrorCode?) {
            Log.d("AdNative", "onNativeFail: ")
            isLoading = false
            adNativeListener?.onNativeFailed(
                this@AdNative,
                AdErrorCode(errorCode?.name, errorCode?.intCode)
            )
        }
    }
    private val renderList = mutableListOf<MoPubAdRenderer<*>>()

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
                adNativeListener?.onNativeShown(this@AdNative)
            }

            override fun onClick(view: View?) {
                Log.d("AdNative", "onClick: $view")
                adNativeListener?.onNativeClicked(this@AdNative)
            }
        }


    private var isLoading = false


    fun loadAd(@LayoutRes layoutRes: Int) {
        if (!isReady() && !isLoading) {
            renderList.clear()
            val mopubAdRender = createMopubStaticAdRender(layoutRes)
            val facebookAdRender = createFacebookAdRender(layoutRes)
            val googleAdRender = createGoogleAdRender(layoutRes)
            registerAdRenderer(mopubAdRender)
            registerAdRenderer(facebookAdRender as MoPubAdRenderer<*>)
            registerAdRenderer(googleAdRender as MoPubAdRenderer<*>)
            renderList.add(mopubAdRender)
            renderList.add(facebookAdRender)
            renderList.add(googleAdRender)
            makeRequest()
            isLoading = true
        }
    }

    private fun createMopubStaticAdRender(@LayoutRes layoutRes: Int): MoPubStaticNativeAdRenderer {
        val viewBinder = ViewBinder.Builder(layoutRes)
            .mainImageId(R.id.native_ad_main_image)
            .iconImageId(R.id.native_ad_icon_image)
            .titleId(R.id.native_ad_title)
            .callToActionId(R.id.native_ad_call_to_action)
            .textId(R.id.native_ad_text)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build()
        return MoPubStaticNativeAdRenderer(viewBinder)
    }

    private fun createGoogleAdRender(@LayoutRes layoutRes: Int): GooglePlayServicesAdRenderer {
        val viewBinder = MediaViewBinder.Builder(layoutRes)
            .mediaLayoutId(R.id.native_ad_media_layout) // bind to your `com.mopub.nativeads.MediaLayout` element
            .iconImageId(R.id.native_ad_icon_image)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .callToActionId(R.id.native_ad_call_to_action)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build()
        return GooglePlayServicesAdRenderer(viewBinder)
    }

    private fun createFacebookAdRender(@LayoutRes layoutRes: Int): FacebookAdRenderer {
        val facebookViewBinder = FacebookAdRenderer.FacebookViewBinder.Builder(layoutRes)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .mediaViewId(R.id.native_ad_fb_media)
            .adIconViewId(R.id.native_ad_fb_icon_image)
            .adChoicesRelativeLayoutId(R.id.native_ad_choices_relative_layout)
            .callToActionId(R.id.native_ad_call_to_action)
            .build()
        return FacebookAdRenderer(facebookViewBinder)
    }

    var tempFrameLayout: FrameLayout? = null

    fun showAd(): View? {
        return if (isReady()) {
            val adapterHelper = AdapterHelper(context, 0, 3)
            val adView = adapterHelper.getAdView(
                null,
                null,
                nativeAd
            )
            nativeAd?.let { nativeAd ->
                val mopubRender = renderList.find { it.supports(nativeAd.baseNativeAd) }
                if (mopubRender !is GooglePlayServicesAdRenderer) {
                    adView.findViewById<View>(R.id.native_ad_media_layout).visibility = View.GONE
                } else {
                    adView.findViewById<View>(R.id.native_ad_media_layout).visibility = View.VISIBLE
                }
                nativeAd.setMoPubNativeEventListener(nativeEventListener)
            }
            adView
        } else {
            if (tempFrameLayout == null) {
                tempFrameLayout = FrameLayout(context)
            }
            tempFrameLayout
        }
    }

    fun isReady(): Boolean {
        return nativeAd != null && !nativeAd?.isDestroyed!!
    }

    fun destroyAd() {
        tempFrameLayout = null
        destroy()
        nativeAd?.destroy()
        adNativeListener?.onNativeDestroy(this)
    }

    var adNativeListener: AdNativeListener? = null
}




