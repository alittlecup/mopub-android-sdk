package mobi.idealabs.ads.asm


object MopubClassChecker {
    val mopubModules: List<AdsInjectPoint> by lazy {
        listOf<AdsInjectPoint>(
            AdsInjectPoint("com.mopub.mobileads.AdViewController", "loadFailUrl"),
//            AdsInjectPoint("com.mopub.mobileads.MoPubView", ""),
            AdsInjectPoint("com.mopub.network.AdLoader", ""),
            AdsInjectPoint("com.mopub.mobileads.AdLoaderRewardedVideo", ""),
            AdsInjectPoint("com.mopub.mobileads.MoPubRewardedVideoManager", ""),
            AdsInjectPoint("com.mopub.mobileads.RewardedAdsLoaders", ""),
            AdsInjectPoint("com.mopub.nativeads.NativeAd", "")
//            AdsInjectPoint("com.mopub.nativeads.NativeAdSource", ""),
//            AdsInjectPoint("com.mopub.network.RequestRateTracker", "registerRateLimit")
        )
    }

    fun isModifyClass(classPathName: String): Boolean {
        for (moduleName in mopubModules) {
            if (classPathName.contains(moduleName.classPath)) {
                println("class $classPathName")
                return true
            }
        }
        return false
    }

    fun isModifyClassMethod(methodName: String?): Boolean {
        for (moduleName in mopubModules) {
        }
        return false
    }

}