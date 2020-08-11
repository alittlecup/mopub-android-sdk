package mobi.idealabs.ads.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.mopub.mobileads.MoPubView
import mobi.idealabs.ads.bean.MopubBannerAdListener
import mobi.idealabs.ads.manage.controller.AdBannerController

class AdBanner(context: Context, attributeSet: AttributeSet? = null) :
    MoPubView(context, attributeSet) {



    init {
        bannerAdListener = AdBannerController.defaultBannerAdListener
    }


    private var hasListenBannerLoadStartByLoad = false
    override fun loadAd() {
//        if (getmContext() == null) return
        if (bannerAdListener is MopubBannerAdListener) {
            (bannerAdListener as MopubBannerAdListener).onBannerLoadStart(this)
            hasListenBannerLoadStartByLoad = true
        }
        super.loadAd()
    }

    override fun setAdContentView(view: View) {
        super.setAdContentView(view)
        AdBannerController.defaultAdBannerListener.onBannerShown(this)
    }

    override fun resolveAdSize(): Point {
        return super.resolveAdSize().apply { x = 0 }
    }


//    override fun onMoPubRequestRealStart() {
//        Log.d("AdBanner", "onMoPubRequestRealStart: ")
//        if (bannerAdListener is MopubBannerAdListener) {
//            if (!hasListenBannerLoadStartByLoad) {
//                (bannerAdListener as MopubBannerAdListener).onBannerLoadStart(this)
//            } else {
//                hasListenBannerLoadStartByLoad = false
//            }
//        }
//    }
}