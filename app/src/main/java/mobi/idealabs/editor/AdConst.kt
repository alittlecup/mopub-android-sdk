package mobi.idealabs.editor

import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.AdType
import mobi.idealabs.ads.core.bean.BannerAdSize


val debug = true

object AdConst {
    /**
     *       private val Banner = "b195f8dd8ded45fe847ad89ed1d016da"
    private val BannerMRet = "2b76357951c14b30ac52d69ffd7f3ccb"
    private val Interstitial = "24534e1901884e398f1253216226017e"
    private val RewardVideo = "15173ac6d3e54c9389b9a5ddca69b34b"
    val editChangeName = "EditBanner"
    val collageChangeName = "CollageBanner"
    val editDoenlChangeName = "EditDownloadBanner"
    val saveChangeName = "SaveBanner"
    val normalChanceName = "normal"
    val luckyChangeName = "lucky"
    val unlock = "unlock"
    //        val nativeID = "11a17b188668469fb0412708c3d16813"
    val nativeID = "362711c73eab4f7eadb817c2ba9ef884"
    val googleNativeTest="ca-app-pub-3940256099942544/2247696110"
    val faceBookNativeTest="362711c73eab4f7eadb817c2ba9ef884"

    var ADS_MOPUB_ID_INTERSTITIAL = if (BuildConfig.DEBUG) TEST_MOPUB_ADS_UNIT_ID_INTERSTITIAL else "f1c7bb8e84b44edb8ef0b48be59ef65b"
    var ADS_MOPUB_ID_REWARD_VIDEO = if (BuildConfig.DEBUG) TEST_MOPUB_ADS_UNIT_ID_REWARD_VIDEO else "af9351301e104186b38f6f54776ade75"
    var ADS_MOPUB_ID_NATIVE = if (BuildConfig.DEBUG) TEST_MOPUB_ADS_UNIT_ID_NATIVE else "362711c73eab4f7eadb817c2ba9ef884"

    val
     */
    const val TestBannerID = "d87903f1752e440e960b2524a9dc1f8c"
    const val TestInterstitialID = "72adf39350884a1eaccf52022b129c28"
    const val TestNativeID = "11a17b188668469fb0412708c3d16813"
    const val TestRewardVideoID = "98a687d8ef894d07899e6a8fe3718bcf"

    val Banner =
        if (debug) "d87903f1752e440e960b2524a9dc1f8c" else "93b2bb4302df4467b63513b0fca4ccdb "
    private val BannerMRet =
        if (debug) "d87903f1752e440e960b2524a9dc1f8c" else "2b76357951c14b30ac52d69ffd7f3ccb"
    private val Interstitial =
        if (debug) "72adf39350884a1eaccf52022b129c28" else "c98af43c613442d7931baa8593a78697"
    private val RewardedPlayable =
        if (debug) "98a687d8ef894d07899e6a8fe3718bcf" else "fb3dc841b39846a4a0b477615c7bd4a6"
    private val Native =
        if (debug) "80df4331660e4579860ec10036997fb1" else "fd868fbb49ac4975b61f9001f4ad4fa1"


    val BannerAdPlacement =
        AdPlacement("banner", Banner, AdType.BANNER, adSize = BannerAdSize.HEIGHT_250)
    val InterstitialAdPlacement = AdPlacement(
        "intersititial",
        Interstitial,
        AdType.INTERSTITIAL
    )
    val NativeAdPlacement =
        AdPlacement("native", Native, AdType.NATIVE).apply {
            chanceName = "chanceName"
        }
    val RewardVideoAdPlacement = AdPlacement(
        "rewardVideo",
        RewardedPlayable,
        AdType.REWARDED_VIDEO
    )

    val adPlacements = listOf(
        BannerAdPlacement, InterstitialAdPlacement, NativeAdPlacement,
        RewardVideoAdPlacement
    )

}