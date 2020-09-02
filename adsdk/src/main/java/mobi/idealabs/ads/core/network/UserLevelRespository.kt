package mobi.idealabs.ads.core.network

import android.annotation.SuppressLint
import android.content.Context
import com.mopub.common.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mobi.idealabs.ads.core.bean.*
import mobi.idealabs.ads.core.controller.AdSdk
import mobi.idealabs.ads.core.utils.SystemUtil

object UserLevelRepository {
    private const val userLevelLocalKey = "ad_user_level_key"
    private const val userLevelTodayUpdate = "ad_user_level_today_update"

    fun userLevel(context: Context, type: String): IVTUserLevel {
        val localUserLevel = loadLocalUserLevel(context)
        val todayUserLevelUpdate =
            SharedPreferencesHelper.getSharedPreferences(context, SystemUtil.SP_AD_NAME)
                .getBoolean(userLevelTodayUpdate, false)
        if (AdTracking.isDailyFirst(context) || !todayUserLevelUpdate) {
            loadUserLevelRemote(context, type) {
                SharedPreferencesHelper.getSharedPreferences(context, SystemUtil.SP_AD_NAME)
                    .edit().putBoolean(userLevelTodayUpdate, true).apply()
                AdSdk.ivtUserLevelListener?.onRemoteLoadSuccess(localUserLevel, it)
                saveUserLevel(context, it)
            }
        }
        return localUserLevel

    }

    @SuppressLint("CheckResult")
    private fun loadUserLevelRemote(
        context: Context, type: String,
        dealUserLevel: (IVTUserLevel) -> Unit
    ) {
        val packageName = context.packageName
        val geID =
            if (packageName.contains("cartoon")) "391" else if (packageName.contains("photoeditor")) "383" else return

        RemoteRepository.service.loadCurrentUserAdLevel(
            packageName, SystemUtil.loadCustomUserId(context), geID, type
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dealUserLevel.invoke(it.toIVTUserLevel())
            }, {})
    }

    private fun saveUserLevel(context: Context, userLevel: IVTUserLevel) {
        SharedPreferencesHelper.getSharedPreferences(context, SystemUtil.SP_AD_NAME)
            .edit().putInt(userLevelLocalKey, userLevel.level).apply()
    }

    private fun loadLocalUserLevel(context: Context): IVTUserLevel {
        val useLevel = SharedPreferencesHelper.getSharedPreferences(context, SystemUtil.SP_AD_NAME)
            .getInt(userLevelLocalKey, -1)
        return getUserLevelByLevel(useLevel)
    }


    private fun getUserLevelByLevel(level: Int): IVTUserLevel {
        return when (level) {
            SuperiorUser.level -> SuperiorUser
            ViciousUser.level -> ViciousUser
            WorseUser.level -> WorseUser
            else -> UndefinedUser
        }
    }
}