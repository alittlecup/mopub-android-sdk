package mobi.idealabs.ads.core.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.View
import com.mopub.mobileads.MoPubView
import mobi.idealabs.ads.core.controller.AdBannerController

class AdBanner(context: Context, attributeSet: AttributeSet? = null) :
    MoPubView(context, attributeSet) {



    init {
        bannerAdListener = AdBannerController.defaultBannerAdListener
    }

    override fun setAdContentView(view: View) {
        super.setAdContentView(view)
        AdBannerController.defaultAdBannerListener.onBannerShown(this)
    }

    override fun resolveAdSize(): Point {
        return super.resolveAdSize().apply { x = 0 }
    }
}