package mobi.idealabs.ads.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*

/**
 * 使用ASM的ClassReader类读取.class的字节数据，并加载类，
 * 然后用自定义的ClassVisitor，进行修改符合特定条件的方法，
 * 最后返回修改后的字节数组
 */
open class MopubMethodAdapter constructor(
    classVisitor: ClassVisitor?
) :
    ClassVisitor(Opcodes.ASM7, classVisitor), Opcodes {


    /**
     * 这里可以拿到关于.class的所有信息，比如当前类所实现的接口类表等
     *
     * @param version    表示jdk的版本
     * @param access     当前类的修饰符 （这个和ASM 和 java有些差异，比如public 在这里就是ACC_PUBLIC）
     * @param name       当前类名
     * @param signature  泛型信息
     * @param superName  当前类的父类
     * @param interfaces 当前类实现的接口列表
     */
    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String?,
        interfaces: Array<String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
    }

    /**
     * 这里可以拿到关于method的所有信息，比如方法名，方法的参数描述等
     *
     * @param access     方法的修饰符
     * @param name       方法名
     * @param descriptor 方法签名（就是（参数列表）返回值类型拼接）
     * @param signature  泛型相关信息
     * @param exceptions 方法抛出的异常信息
     * @return
     */
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String?,
        signature: String?,
        exceptions: Array<String>?
    ): MethodVisitor? {
        
        return super.visitMethod(access, name, descriptor, signature, exceptions)

    }
}