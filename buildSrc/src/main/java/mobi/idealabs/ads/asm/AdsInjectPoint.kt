package mobi.idealabs.ads.asm

data class AdsInjectPoint(val clazzName: String, val funcName: List<String>) {
    val classPath = clazzName.replace(".", "/") + ".class"

    init {
        println(classPath)
    }
}