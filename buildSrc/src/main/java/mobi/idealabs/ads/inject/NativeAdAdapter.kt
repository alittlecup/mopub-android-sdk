package mobi.idealabs.ads.inject

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*


class NativeAdAdapter(
    val className: String,
    classVisitor: ClassVisitor?
) : ClassVisitor(Opcodes.ASM7, classVisitor) {
    init {
        println("adViewController: $className")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (name == "handleClick" || name == "recordImpression" || name == "<init>") {
            NativeAdMethodVisitor(
                className = className,
                methodName = name, methodVisitor = visitMethod
            )
        } else {
            visitMethod
        }
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        if (name == "mContext") {
            super.visitField(
                ACC_PRIVATE,
                "mAdResponse",
                "Lcom/mopub/network/AdResponse;",
                null,
                null
            )
        }
        return if (name == "mMoPubAdRenderer") {
            super.visitField(ACC_PUBLIC, name, descriptor, signature, value)
        } else {
            super.visitField(access, name, descriptor, signature, value)
        }
    }

    override fun visitEnd() {
      var  methodVisitor = cv.visitMethod(
            ACC_PUBLIC,
            "setMoPubAdRenderer",
            "(Lcom/mopub/nativeads/MoPubAdRenderer;)V",
            null,
            null
        )
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(ALOAD, 0)
        methodVisitor.visitVarInsn(ALOAD, 1)
        methodVisitor.visitFieldInsn(
            PUTFIELD,
            "com/mopub/nativeads/NativeAd",
            "mMoPubAdRenderer",
            "Lcom/mopub/nativeads/MoPubAdRenderer;"
        )
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitInsn(RETURN)
        val label2 = Label()
        methodVisitor.visitLabel(label2)
        methodVisitor.visitLocalVariable(
            "this",
            "Lcom/mopub/nativeads/NativeAd;",
            null,
            label0,
            label2,
            0
        )
        methodVisitor.visitLocalVariable(
            "adRenderer",
            "Lcom/mopub/nativeads/MoPubAdRenderer;",
            null,
            label0,
            label2,
            1
        )
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
        super.visitEnd()
    }
}

class NativeAdMethodVisitor(
    val className: String,
    val methodName: String,
    api: Int = Opcodes.ASM7,
    methodVisitor: MethodVisitor
) :
    MethodVisitor(api, methodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        if (methodName == "<init>" && name == "getImpressionData") {
            mv.visitVarInsn(ALOAD, 0)
            mv.visitVarInsn(ALOAD, 2)
            mv.visitFieldInsn(
                PUTFIELD,
                "com/mopub/nativeads/NativeAd",
                "mAdResponse",
                "Lcom/mopub/network/AdResponse;"
            )
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

    }

    override fun visitCode() {
        if (methodName == "handleClick") {
            MopubInject.injectClick(className, mv)
        } else if (methodName == "recordImpression") {
            MopubInject.injectImpression(className, mv)
        }
        super.visitCode()
    }

}
