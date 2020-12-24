package mobi.idealabs.ads.asm


object MopubClassChecker {
    val mopubModules: List<AdsInjectPoint> by lazy {
        listOf<AdsInjectPoint>(
            AdsInjectPoint("com.mopub.mobileads.AdViewController", "loadFailUrl"),
            AdsInjectPoint("com.mopub.nativeads.MoPubNative", ""),
            AdsInjectPoint("com.mopub.mobileads.MoPubView\$BannerAdListener", ""),
            AdsInjectPoint("com.mopub.mobileads.DefaultBannerAdListener", ""),
//            AdsInjectPoint("com.mopub.nativeads.MoPubRecyclerAdapter", ""),
            AdsInjectPoint("com.mopub.nativeads.NativeAdSource", ""),
            AdsInjectPoint("com.mopub.network.AdLoader", ""),
            AdsInjectPoint("com.mopub.mobileads.AdLoaderRewardedVideo", ""),
            AdsInjectPoint("com.mopub.mobileads.MoPubRewardedVideoManager", ""),
            AdsInjectPoint("com.mopub.mobileads.CustomEventBannerAdapter", ""),
            AdsInjectPoint("com.mopub.mobileads.RewardedAdsLoaders", ""),
            AdsInjectPoint("com.mopub.network.RequestRateTracker", "registerRateLimit"),
            AdsInjectPoint("com.mopub.nativeads.NativeAd", "")
//            AdsInjectPoint("com.mopub.nativeads.MoPubRecyclerAdapter", "")
//            AdsInjectPoint("com.mopub.nativeads.NativeAdSource", ""),
        )
    }

    fun isModifyClass(classPathName: String): Boolean {
        for (moduleName in mopubModules) {
            if (classPathName == moduleName.classPath) {
                println("class $classPathName")
                return true
            }
        }
        return false
    }

    fun getPackagePath(classPathName: String): String {
        for (moduleName in mopubModules) {
            if (classPathName == moduleName.classPath) {
                println("class $classPathName")
                return moduleName.packagePath
            }
        }
        return classPathName
    }


    fun isModifyClassMethod(methodName: String?): Boolean {
        return false
    }

}