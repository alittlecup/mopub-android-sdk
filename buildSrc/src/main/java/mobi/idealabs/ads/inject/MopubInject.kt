package mobi.idealabs.ads.inject

import groovyjarjarasm.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.MethodVisitor

class MopubInject {
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

    fun injectWaterFallItemFail(mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            "mobi/idealabs/ads/report/TrackInject",
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

    fun injectWaterFallFail(mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            "mobi/idealabs/ads/report/TrackInject",
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

    fun injectWaterFallSuccess(mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            "mobi/idealabs/ads/report/TrackInject",
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

    fun injectClick(mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            "mobi/idealabs/ads/report/TrackInject",
            "mLastDeliveredResponse",
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

    fun injectImpression(mv: MethodVisitor) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(
            GETFIELD,
            "mobi/idealabs/ads/report/TrackInject",
            "mLastDeliveredResponse",
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

}