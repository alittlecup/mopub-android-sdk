package mobi.idealabs.ads.inject

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class NativeAdSourceNativeAdSourceAdapterAdapter(
    val className: String,
    classVisitor: ClassVisitor?
) : ClassVisitor(Opcodes.ASM7, classVisitor) {
    init {
        println("adViewController: $className")
    }
    
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(
            version,
            Opcodes.ACC_PUBLIC or access,
            name,
            signature,
            superName,
            interfaces
        )
    }
    
}
