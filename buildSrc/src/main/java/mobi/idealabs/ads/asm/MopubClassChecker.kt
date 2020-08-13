package mobi.idealabs.ads.asm


object MopubClassChecker {
    val mopubModules by lazy {
        listOf(
            AdsInjectPoint("com.mopub.mobileads.AdViewController", ""),
            AdsInjectPoint("com.mopub.mobileads.MoPubView", ""),
            AdsInjectPoint("com.mopub.network.AdLoader", ""),
            AdsInjectPoint("com.mopub.nativeads.MoPubNative", ""),
            AdsInjectPoint("com.mopub.nativeads.MoPubRecyclerAdapter", ""),
            AdsInjectPoint("com.mopub.nativeads.MoPubStreamAdPlacer", ""),
            AdsInjectPoint("com.mopub.nativeads.NativeAd", ""),
            AdsInjectPoint("com.mopub.nativeads.NativeAdSource", "")
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

    fun isModifyClassMethod(methodName: String?): Boolean {
        return true
    }

}