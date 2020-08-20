package mobi.idealabs.ads.inject

import com.squareup.javapoet.ClassName
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Opcodes

class RewardedAdsLoadersAdapter(
    val className: String,
    api: Int = Opcodes.ASM7,
    classVisitor: ClassVisitor?
) : ClassVisitor(api, classVisitor) {
    init {
        println("adLoader: $className")
    }

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, ACC_PUBLIC, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(ACC_PUBLIC, name, descriptor, signature, exceptions)
    }
}

