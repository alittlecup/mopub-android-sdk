package mobi.idealabs.ads.core.controller

import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.SuperiorUser
import mobi.idealabs.ads.core.bean.UndefinedUser
import mobi.idealabs.ads.core.bean.ViciousUser
import mobi.idealabs.ads.core.network.UserLevelRepository

open abstract class AdSdkInitStrategy(val logAble: Boolean, val canRetry: Boolean) {

    abstract fun findAdPlacementByAdUnitId(adUnityId: String): AdPlacement?
    abstract fun findAdPlacementByChanceName(chanceName: String): AdPlacement?
}

abstract class DefaultAdSdkInitStrategy(
    val adPlacements: List<AdPlacement>,
    logAble: Boolean,
    canRetry: Boolean
) : AdSdkInitStrategy(logAble, canRetry) {
    override fun findAdPlacementByAdUnitId(adUnityId: String): AdPlacement? {
        return adPlacements.find { it.adUnitId == adUnityId }
    }


}

abstract class IVTAdSdkInitStrategy(
    val superiorAdPlacement: List<AdPlacement>,
    val viciousAdPlacement: List<AdPlacement>,
    val worseAdPlacement: List<AdPlacement>,
    val type: String,
    logAble: Boolean,
    canRetry: Boolean
) : AdSdkInitStrategy(logAble, canRetry) {
    override fun findAdPlacementByAdUnitId(adUnityId: String): AdPlacement? {
        val userLevel = UserLevelRepository.userLevel(AdSdk.application!!,type)
        return when (userLevel) {
            ViciousUser -> viciousAdPlacement
            worseAdPlacement -> worseAdPlacement
            UndefinedUser, SuperiorUser -> superiorAdPlacement
            else -> superiorAdPlacement
        }.find { it.adUnitId == adUnityId }
    }
}