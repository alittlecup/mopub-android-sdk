package mobi.idealabs.ads.asm

import kotlin.reflect.KClass

data class AdsInjectPoint<T : org.objectweb.asm.ClassVisitor>(
    val clazzName: String,
    var injectClass: KClass<T>
) {
    val classPath = "${clazzName.replaceBeforeLast(".", "").replace(".", "")}.class"
    val packagePath = "${clazzName.replace(".", "/")}.class"
    override fun toString(): String {
        return "AdsInjectPoint(clazzName='$clazzName', injectClass=$injectClass, classPath='$classPath', packagePath='$packagePath')"
    }

}   