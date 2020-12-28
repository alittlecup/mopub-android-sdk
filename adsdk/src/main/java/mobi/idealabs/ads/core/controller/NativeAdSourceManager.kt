package mobi.idealabs.ads.core.controller

import com.mopub.nativeads.NativeAdSource

public object NativeAdSourceManager {

    private val nativeAdSources = HashMap<String, NativeAdSource>()
    private val secondNativeAdSources = HashMap<String, NativeAdSource>()
    public fun getNativeAdSource(adUnitId: String): NativeAdSource {
        var nativeAdSource = nativeAdSources[adUnitId]
        if (nativeAdSource == null) {
            nativeAdSource = NativeAdSource()
            nativeAdSources[adUnitId] = nativeAdSource
        }
        return nativeAdSource
    }

    internal fun enableSecondNativeAdSource(adUnitId: String) {
        var nativeAdSource = secondNativeAdSources[adUnitId]
        if (nativeAdSource == null) {
            nativeAdSource = NativeAdSource()
            secondNativeAdSources[adUnitId] = nativeAdSource
        }
    }

    internal fun isEnableSecondCache(adUnitId: String): Boolean {
        return secondNativeAdSources.containsKey(adUnitId)
    }

    internal fun getSecondNativeAdSource(adUnitId: String): NativeAdSource? {
        return secondNativeAdSources[adUnitId]
    }
}
