package mobi.idealabs.ads.inject

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*

class NativeAdAdapter(
    val className: String,
    api: Int = Opcodes.ASM7,
    classVisitor: ClassVisitor?
) : ClassVisitor(api, classVisitor) {
    init {
        println("NativeAd: $className")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        return NativeAdMethodVisitor(
            className = className,
            methodName = name!!, methodVisitor = visitMethod
        )
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
        return super.visitField(access, name, descriptor, signature, value)
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
        if (methodName == "handleClick" && name == "makeTrackingHttpRequest") {
            MopubInject.injectClick(className, mv)
        } else if (methodName == "recordImpression" && name == "makeTrackingHttpRequest") {
            MopubInject.injectImpression(className, mv)
        } else if (methodName == "<init>" && name == "getImpressionData") {
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

}
