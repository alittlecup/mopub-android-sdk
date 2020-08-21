package mobi.idealabs.ads.inject

import com.squareup.javapoet.ClassName
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AdLoaderRewardedVideoAdapter(
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
        val visitMethod = super.visitMethod( Opcodes.ACC_PUBLIC, name, descriptor, signature, exceptions)
        return AdLoaderRewardedVideoAdapterMethodVisitor(
            className = className,
            methodName = name!!, methodVisitor = visitMethod
        )
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, Opcodes.ACC_PUBLIC, name, signature, superName, interfaces)
    }
}

class AdLoaderRewardedVideoAdapterMethodVisitor(
    val className: String,
    val methodName: String,
    api: Int = Opcodes.ASM7,
    methodVisitor: MethodVisitor
) :
    MethodVisitor(api, methodVisitor) {
    override fun visitCode() {
        println("methodName: $methodName")
        if (methodName == "trackClick") {
            MopubInject.injectClick(className, mv,"mLastDeliveredResponse")
        } else if (methodName == "trackImpression") {
            MopubInject.injectImpression(className, mv,"mLastDeliveredResponse")
        }
        super.visitCode()
    }
}