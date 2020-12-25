package mobi.idealabs.ads.inject

import com.squareup.javapoet.ClassName
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AdLoaderAdapter(
    val className: String,
    classVisitor: ClassVisitor?
) : ClassVisitor(Opcodes.ASM7, classVisitor) {
    init {
        println("adLoader: $className")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        return AdLoaderMethodVisitor(
            className = className,
            methodName = name!!, methodVisitor = visitMethod
        )
    }
}

class AdLoaderMethodVisitor(
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
        if (methodName == "deliverResponse" && name == "onSuccess") {
            MopubInject.injectWaterFallItemStart(mv)
        } else if (methodName == "deliverError" && name == "checkNotNull") {
            MopubInject.injectWaterFallFail(className, mv, "mLastDeliveredResponse")
        }
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitCode() {
        if (methodName == "creativeDownloadSuccess") {
            MopubInject.injectWaterFallSuccess(className,mv,"mLastDeliveredResponse")

        }else if(methodName=="creativeDownloadFailed"){
            MopubInject.injectWaterFallItemFail(className,mv,"mLastDeliveredResponse")

        }
    }
}