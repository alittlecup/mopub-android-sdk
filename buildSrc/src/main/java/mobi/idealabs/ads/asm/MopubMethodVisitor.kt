package mobi.idealabs.ads.asm

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Opcodes


internal class MopubMethodVisitor(api: Int = Opcodes.ASM7, methodVisitor: MethodVisitor) :
    MethodVisitor(api, methodVisitor) {


    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        println("MopubMethodVisitor-> visitMethodInsn:$name")
        if (name == "loadCustomEvent") {
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "mobi/idealabs/ads/report/TrackEventManager",
                "trackWaterFallItemStart",
                "(Lcom/mopub/network/AdResponse;)V",
                false
            )
        }
        if (name == "loadNonJavascript") {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(
                GETFIELD,
                "com/mopub/mobileads/AdViewController",
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
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}