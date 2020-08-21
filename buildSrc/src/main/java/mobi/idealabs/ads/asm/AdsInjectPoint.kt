package mobi.idealabs.ads.asm

data class AdsInjectPoint(val clazzName: String, val funcName:String) {
    val classPath = "${clazzName.replaceBeforeLast(".","").replace(".","")}.class"

    init {
        println("AdsInjectPoint: $classPath")
    }
}