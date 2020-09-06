package mobi.idealabs.ads.core.controller

import mobi.idealabs.ads.core.bean.*
import mobi.idealabs.ads.core.network.UserLevelRepository

open abstract class AdSdkInitStrategy(val logAble: Boolean, val canRetry: Boolean) {

    abstract fun findAdPlacementByAdUnitId(adUnityId: String): AdPlacement?
    abstract fun findAdPlacementByChanceName(chanceName: String): AdPlacement?
    abstract fun findAdPlacementByName(placementName: String): AdPlacement?
}

abstract class DefaultAdSdkInitStrategy(
    val adPlacements: List<AdPlacement>,
    logAble: Boolean,
    canRetry: Boolean
) : AdSdkInitStrategy(logAble, canRetry) {
    override fun findAdPlacementByAdUnitId(adUnityId: String): AdPlacement? {
        return adPlacements.find { it.adUnitId == adUnityId }
    }

    override fun findAdPlacementByName(placementName: String): AdPlacement? {
        return adPlacements.find { it.name == placementName }
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
        return getCurLevelAdPlacements().find { it.adUnitId == adUnityId }
    }

    private fun getCurLevelAdPlacements(): List<AdPlacement> {
        val userLevel = UserLevelRepository.userLevel(AdSdk.application!!, type)
        return when (userLevel) {
            ViciousUser -> viciousAdPlacement
            worseAdPlacement -> worseAdPlacement
            UndefinedUser, SuperiorUser -> superiorAdPlacement
            else -> superiorAdPlacement
        }
    }

    override fun findAdPlacementByName(placementName: String): AdPlacement? {
        return getCurLevelAdPlacements().find { it.name == placementName }
    }
}