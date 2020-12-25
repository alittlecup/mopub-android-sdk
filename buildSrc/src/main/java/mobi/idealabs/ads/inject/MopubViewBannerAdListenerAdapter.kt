package mobi.idealabs.ads.inject

import com.sun.org.apache.bcel.internal.generic.RETURN
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class MopubViewBannerAdListenerAdapter(
    val className: String,
    classVisitor: ClassVisitor?
) : ClassVisitor(Opcodes.ASM7, classVisitor) {
    init {
        println("adLoader: $className")
    }
    
    override fun visitEnd() {
        var methodVisitor = cv.visitMethod(
            Opcodes.ACC_PUBLIC,
            "onBannerLoadStart",
            "(Lcom/mopub/mobileads/MoPubView;)V",
            null,
            null
        )
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLocalVariable(
            "this",
            "Lcom/mopub/mobileads/DefaultBannerAdListener;",
            null,
            label0,
            label1,
            0
        )
        methodVisitor.visitLocalVariable(
            "banner",
            "Lcom/mopub/mobileads/MoPubView;",
            null,
            label0,
            label1,
            1
        )
        methodVisitor.visitMaxs(0, 2)
        methodVisitor.visitEnd()
        super.visitEnd()
    }
}
