package mobi.idealabs.ads.inject

import com.squareup.javapoet.ClassName
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MoPubRecyclerAdapterAdapter(
    val className: String,
    api: Int = Opcodes.ASM7,
    classVisitor: ClassVisitor?
) : ClassVisitor(api, classVisitor) {
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
        return if (name == "onBindViewHolder") {
            MoPubRecyclerAdapterVisitor(
                className = className,
                methodName = name, methodVisitor = visitMethod
            )
        } else {
            visitMethod
        }
    }
}

class MoPubRecyclerAdapterVisitor(
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
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        if (methodName == "onBindViewHolder" && name == "bindAdView") {
            MopubInject.injectMoPubRecyclerViewShow(className, this)
        }
    }

    override fun visitCode() {
        MopubInject.injectMoPubRecyclerViewBind(className, this)
        super.visitCode()
    }
}