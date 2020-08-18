package mobi.idealabs.ads.inject

import com.squareup.javapoet.ClassName
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AdViewControllerAdapter(
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
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        return AdViewControllerMethodVisitor(
            className = className,
            methodName = name!!, methodVisitor = visitMethod
        )
    }
}

class AdViewControllerMethodVisitor(
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
        if (methodName == "onAdLoadSuccess" && name == "loadCustomEvent") {
            MopubInject.injectWaterFallItemStart(mv)
        } else if (methodName == "creativeDownloadSuccess" && name == "creativeDownloadSuccess") {
            MopubInject.injectWaterFallSuccess(className, mv)

        } else if (methodName == "loadFailUrl" && name == "loadNonJavascript") {
            MopubInject.injectWaterFallItemFail(className, mv)
        } else if (methodName == "onAdLoadError" && name == "adDidFail") {
            MopubInject.injectWaterFallFail(className, mv)
        } else if (methodName == "registerClick" && name == "makeTrackingHttpRequest") {
            MopubInject.injectClick(className, mv)

        } else if (methodName == "trackImpression" && name == "makeTrackingHttpRequest") {
            MopubInject.injectImpression(className, mv)
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}