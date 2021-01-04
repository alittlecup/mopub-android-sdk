package mobi.idealabs.ads.core.controller

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import com.mopub.common.VisibilityTracker
import com.mopub.nativeads.*
import org.jetbrains.annotations.NotNull


public class NativeAdRecyclerAdapter : MoPubRecyclerAdapter {
    private var adUnitId: String = ""

    constructor(
        @NotNull adUnitId: String,
        activity: Activity,
        originalAdapter: RecyclerView.Adapter<*>,
        adPositioning: MoPubNativeAdPositioning.MoPubClientPositioning
    ) : super(
        MoPubStreamAdPlacer(
            activity,
            NativeAdSourceManager.getNativeAdSource(adUnitId),
            ClientPositioningSource(adPositioning)
        ), originalAdapter,
        VisibilityTracker(activity)
    ) {
        this.adUnitId = adUnitId
    }

    public override fun loadAds(adUnitId: String) {
        if (adUnitId == this.adUnitId) {
            super.loadAds(adUnitId)
        }
    }

    public override fun refreshAds(adUnitId: String) {
        if (adUnitId == this.adUnitId) {
            super.refreshAds(adUnitId)
        }
    }

    override fun loadAds(adUnitId: String, requestParameters: RequestParameters?) {
        if (adUnitId == this.adUnitId) {
            super.loadAds(adUnitId, requestParameters)
        }
    }

    public override fun refreshAds(adUnitId: String, requestParameters: RequestParameters?) {
        if (adUnitId == this.adUnitId) {
            super.refreshAds(adUnitId, requestParameters)
        }
    }
}