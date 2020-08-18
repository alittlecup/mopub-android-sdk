package mobi.idealabs.ads.asm


object MopubClassChecker {
    val mopubModules: List<AdsInjectPoint> by lazy {
        listOf<AdsInjectPoint>(
            AdsInjectPoint("com.mopub.mobileads.AdViewController", "loadFailUrl"),
            AdsInjectPoint("com.mopub.mobileads.MoPubView", ""),
            AdsInjectPoint("com.mopub.network.AdLoader", ""),
            AdsInjectPoint("com.mopub.nativeads.MoPubNative", ""),
            AdsInjectPoint("com.mopub.nativeads.MoPubRecyclerAdapter", ""),
            AdsInjectPoint("com.mopub.nativeads.MoPubStreamAdPlacer", ""),
            AdsInjectPoint("com.mopub.nativeads.NativeAd", ""),
            AdsInjectPoint("com.mopub.nativeads.NativeAdSource", ""),
            AdsInjectPoint("com.mopub.network.RequestRateTracker", "registerRateLimit")
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
//            if (methodName == moduleName.funcName) {
//                println("method $methodName")
//                return true
//            }
        }
        return false
    }

}