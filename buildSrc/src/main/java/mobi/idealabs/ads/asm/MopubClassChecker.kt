package mobi.idealabs.ads.asm

import mobi.idealabs.ads.inject.*
import org.gradle.wrapper.Logger
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.Console


object MopubClassChecker {

    val mopubModules: List<AdsInjectPoint<*>> by lazy {
        listOf<AdsInjectPoint<*>>(
            AdsInjectPoint("com.mopub.mobileads.AdViewController", AdViewControllerAdapter::class),
            AdsInjectPoint("com.mopub.nativeads.MoPubNative", MoPubNativeAdapter::class),
            AdsInjectPoint("com.mopub.nativeads.MoPubRecyclerAdapter",MoPubRecyclerAdapterAdapter::class),
            AdsInjectPoint("com.mopub.nativeads.NativeAdSource", NativeAdSourceAdapter::class),
            AdsInjectPoint("com.mopub.nativeads.NativeAdSource\$AdSourceListener", NativeAdSourceNativeAdSourceAdapterAdapter::class),
            AdsInjectPoint("com.mopub.network.AdLoader", AdLoaderAdapter::class),
            AdsInjectPoint(
                "com.mopub.mobileads.AdLoaderRewardedVideo",
                AdLoaderRewardedVideoAdapter::class
            ),
            AdsInjectPoint(
                "com.mopub.mobileads.MoPubRewardedVideoManager",
                MoPubRewardVideoManagerAdapter::class
            ),
            AdsInjectPoint(
                "com.mopub.mobileads.CustomEventBannerAdapter",
                CustomEventBannerAdapterAdapter::class
            ),
            AdsInjectPoint(
                "com.mopub.mobileads.RewardedAdsLoaders",
                RewardedAdsLoadersAdapter::class
            ),
            AdsInjectPoint(
                "com.mopub.network.RequestRateTracker",
                RequestRateTrackerAdapter::class
            ),
            AdsInjectPoint("com.mopub.nativeads.NativeAd", NativeAdAdapter::class)
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

    fun getFileClassVisitor(fileName: String, classWriter: ClassWriter): ClassVisitor {
        var adsInjectPoint = mopubModules.find {
            fileName == it.packagePath
        }
        var classVisitor = if (adsInjectPoint == null) {
            MopubMethodAdapter(classWriter)
        } else {
            var injectClass = adsInjectPoint.injectClass
            var forName = Class.forName(injectClass.qualifiedName)
            forName.constructors[0].newInstance(fileName.removeSuffix(".class"), classWriter) as ClassVisitor
        }
        println("file : $fileName -----> ClassVisitor: ${classVisitor.javaClass.simpleName}") 
        return classVisitor;
    }
}