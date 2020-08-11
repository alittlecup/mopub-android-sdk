package mobi.idealabs.ads.bean

enum class AdType constructor(val type: String) {
    BANNER("banner"),
    INTERSTITIAL("interstitial"),
    NATIVE("native"),
    REWARDED_VIDEO("rewardedvideo")
}
