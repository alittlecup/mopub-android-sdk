package mobi.idealabs.ads.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal class MopubMethodVisitor(api: Int = Opcodes.ASM7, methodVisitor: MethodVisitor) :
    MethodVisitor(api, methodVisitor) {


    override fun visitMethodInsn(
        opcode: Int,
        owner: String,
        name: String,
        descriptor: String,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}