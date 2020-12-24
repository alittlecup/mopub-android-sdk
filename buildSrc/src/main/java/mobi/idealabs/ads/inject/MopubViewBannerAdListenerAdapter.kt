package mobi.idealabs.ads.inject

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MopubViewBannerAdListenerAdapter(
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
        println("MopubViewBannerAdListenerAdapter :: $name")
        val visitMethod = super.visitMethod(access, name, descriptor, signature, exceptions)
        if(name == "onBannerLoaded"){
            super.visitMethod(access, "onBannerLoadStart", descriptor, signature, exceptions)
        }
        return visitMethod
    }
}
