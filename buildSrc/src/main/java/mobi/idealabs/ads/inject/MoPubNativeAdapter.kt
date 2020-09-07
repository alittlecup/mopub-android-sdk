package mobi.idealabs.ads.inject

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MoPubNativeAdapter(
    val className: String,
    api: Int = Opcodes.ASM7,
    classVisitor: ClassVisitor?
) : ClassVisitor(api, classVisitor) {
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
        val methodVisitor = if (name == "getMoPubNativeNetworkListener") {
            super.visitMethod(Opcodes.ACC_PUBLIC, name, descriptor, signature, exceptions)
        } else {
            super.visitMethod(access, name, descriptor, signature, exceptions)
        }
        return if (name == "loadAds") {
            NativeAdSourceMethodVisitor(className, name, methodVisitor)
        } else {
            methodVisitor
        }
    }


}

class NativeAdSourceMethodVisitor(
    val className: String,
    val methodName: String,
    methodVisitor: MethodVisitor
) :
    MethodVisitor(Opcodes.ASM7, methodVisitor) {
    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        println("name: -> $name--$owner")
        if (methodName == "loadAds" && name == "<init>" && owner?.contains("MoPubNative") == true) {
            super.visitMethodInsn(
                opcode,
                "mobi/idealabs/ads/core/view/AdNative",
                name,
                descriptor,
                isInterface
            )
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)

        }
    }

    override fun visitTypeInsn(opcode: Int, type: String?) {
        println("type in: $type")
        if (type == "com/mopub/nativeads/MoPubNative") {
            super.visitTypeInsn(opcode, "mobi/idealabs/ads/core/view/AdNative")
        } else {
            super.visitTypeInsn(opcode, type)

        }
    }

}