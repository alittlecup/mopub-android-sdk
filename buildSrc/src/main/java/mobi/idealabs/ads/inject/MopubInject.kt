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

    fun injectWaterFallItemFail(clazzName:String, mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            "mAdResponse",
            "Lcom/mopub/network/AdResponse;"
        );
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "mobi/idealabs/ads/report/TrackEventManager",
            "trackWaterFallItemFail",
            "(Lcom/mopub/network/AdResponse;Lcom/mopub/mobileads/MoPubErrorCode;)V",
            false
        );
    }

    fun injectWaterFallFail(clazzName:String,mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            "mAdResponse",
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

    fun injectWaterFallSuccess(clazzName:String,mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            "mAdResponse",
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

    fun injectClick(clazzName:String,mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            "mAdResponse",
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

    fun injectImpression(clazzName:String,mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            clazzName,
            "mAdResponse",
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
    /**
     * *injectWaterFallItemStart*
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
