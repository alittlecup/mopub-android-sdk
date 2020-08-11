package mobi.idealabs.ads


object MopubClassChecker {
    var mopubModules = listOf(
        "mopub-sdk-banner",
        "mopub-sdk-base",
        "mopub-sdk-fullscreen",
        "mopub-sdk-native-static",
        "mopub-sdk-native-video"
    )


    fun isModifyClass(classPathName: String): Boolean {
        for (moduleName in mopubModules) {
            if (classPathName.contains("BaseAd.java")) {
                println("class $classPathName")
            }
        }
        return true
    }

    fun isModifyClassMethod(methodName: String?): Boolean {
        return true
    }

}