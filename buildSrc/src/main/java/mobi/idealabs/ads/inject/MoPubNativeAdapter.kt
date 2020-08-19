package mobi.idealabs.ads.inject

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MoPubNativeAdapter(
    val className: String,
    api: Int = Opcodes.ASM7,
    classVisitor: ClassVisitor?
) : ClassVisitor(api, classVisitor) {
    init {
        println("moPubNative: $className")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        return MoPubNativeMethodVisitor(
            className = className,
            methodName = name!!, methodVisitor = visitMethod
        )
    }
}

class MoPubNativeMethodVisitor(
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
        if (methodName == "onAdLoad" && name == "loadNativeAd") {
        } else if (methodName == "onNativeAdLoaded" && name == "onNativeLoad") {
        } else if (methodName == "onNativeAdFailed" && name == "requestNativeAd") {
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}
