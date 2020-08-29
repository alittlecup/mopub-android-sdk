package mobi.idealabs.ads.inject

import groovyjarjarasm.asm.Opcodes.*
import org.objectweb.asm.MethodVisitor

object MopubInject {
    fun injectWaterFallItemStart(mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackWaterFallItemStart",
            "(Lcom/mopub/network/AdResponse;)V",
            false
        );
    }

    fun injectWaterFallItemFail(clazzName:String, mv: MethodVisitor,adResponseName:String="mAdResponse") {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            adResponseName,
            "Lcom/mopub/network/AdResponse;"
        );
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackWaterFallItemFail",
            "(Lcom/mopub/network/AdResponse;Lcom/mopub/mobileads/MoPubError;)V",
            false
        );
    }

    fun injectWaterFallFail(clazzName:String,mv: MethodVisitor,adResponseName:String="mAdResponse") {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            adResponseName,
            "Lcom/mopub/network/AdResponse;"
        );
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackWaterFallFail",
            "(Lcom/mopub/network/AdResponse;)V",
            false
        );

    }

    fun injectWaterFallSuccess(clazzName:String,mv: MethodVisitor,adResponseName:String="mAdResponse") {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            adResponseName,
            "Lcom/mopub/network/AdResponse;"
        );
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackWaterFallSuccess",
            "(Lcom/mopub/network/AdResponse;)V",
            false
        );

    }

    fun injectClick(clazzName:String,mv: MethodVisitor,adResponseName:String="mAdResponse") {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            adResponseName,
            "Lcom/mopub/network/AdResponse;"
        );
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackClick",
            "(Lcom/mopub/network/AdResponse;)V",
            false
        );

    }

    fun injectImpression(clazzName:String,mv: MethodVisitor,adResponseName:String="mAdResponse") {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            adResponseName,
            "Lcom/mopub/network/AdResponse;"
        );
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackImpression",
            "(Lcom/mopub/network/AdResponse;)V",
            false
        );
    }
    fun injectReward(mv: MethodVisitor){
        mv.visitFieldInsn(GETSTATIC, "com/mopub/mobileads/MoPubRewardedVideoManager", "sInstance", "Lcom/mopub/mobileads/MoPubRewardedVideoManager;");
        mv.visitFieldInsn(GETFIELD, "com/mopub/mobileads/MoPubRewardedVideoManager", "rewardedAdsLoaders", "Lcom/mopub/mobileads/RewardedAdsLoaders;");
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn("_");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKESTATIC, "mobi/idealabs/ads/report/TrackEventManager", "trackReward", "(Lcom/mopub/mobileads/RewardedAdsLoaders;Ljava/lang/String;Ljava/lang/String;)V", false);

    }
    fun injectMoPubRecyclerViewBind(clazzName: String,mv:MethodVisitor){
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "com/mopub/nativeads/MoPubRecyclerAdapter", "mAdLoadedListener", "Lcom/mopub/nativeads/MoPubNativeAdLoadedListener;");
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackMoPubRecyclerViewListenerBind",
            "(Lcom/mopub/nativeads/MoPubNativeAdLoadedListener;I)V",
            false
        );
    }
    fun injectMoPubRecyclerViewShow(clazzName: String,mv:MethodVisitor){
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, "com/mopub/nativeads/MoPubRecyclerAdapter", "mAdLoadedListener", "Lcom/mopub/nativeads/MoPubNativeAdLoadedListener;");
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackMoPubRecyclerViewListenerShow",
            "(Lcom/mopub/nativeads/MoPubNativeAdLoadedListener;I)V",
            false
        );
    }
    /**
     * *injectWaterFallItemStart*
     *
     * AdLoader-> deliverResponse(onSuccess)
     *
     *
     * AdViewController -> onAdLoadSuccess (loadCustomEvent)
     * MoPubNative -> onAdLoad (loadNativeAd)
     * MoPubRewardedVideoManager -> onAdSuccess (updateAdUnitCustomEventMapping)
     */

    /**
     * *injectWaterFallSuccess*
     * AdViewController-> creativeDownloadSuccess(creativeDownloadSuccess)
     * MoPubNative-> onAdLoad(creativeDownloadSuccess)
     * MoPubRewardedVideoManager -> onRewardedVideoLoadSuccess (postToInstance)
     */

    /**
     * *injectWaterFallItemFail*
     * AdViewController-> loadFailUrl(loadNonJavascript)
     * MoPubNative->onAdLoad(requestNativeAd)
     * MoPubRewardedVideoManager->onRewardedVideoLoadFailure(postToInstance)
     */

    /**
     * injectWaterFallFail
     *
     * AdLoader - > deliverError(checkNotNull)
     *
     * AdViewController->onAdLoadError(adDidFail)
     * MoPubNative->onAdError()
     * MoPubRewardedVideoManager->onAdError()
     */

    /**
     * injectClick
     * AdViewController->registerClick(makeTrackingHttpRequest)
     * NativeAd->handleClick(makeTrackingHttpRequest)
     * AdLoaderRewardedVideo->trackClick()
     */
    /**
     * injectImpression
     * AdViewController->trackImpression(makeTrackingHttpRequest)
     * NativeAd->recordImpression(makeTrackingHttpRequest)
     * AdLoaderRewardedVideo->trackImpression()
     */

}
