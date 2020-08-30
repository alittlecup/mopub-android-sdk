package mobi.idealabs.ads.core.network

import android.annotation.SuppressLint
import android.content.Context
import com.mopub.common.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mobi.idealabs.ads.core.bean.*
import mobi.idealabs.ads.core.utils.SystemUtil

object UserLevelRepository {
    private const val userLevelLocalKey = "ad_user_level_key"

    fun userLevel(context: Context): IVTUserLevel {
        val localUserLevel = loadLocalUserLevel(context)
        if (AdTracking.isDailyFirst(context) || localUserLevel.level == -1) {
            loadUserLevelRemote(context) {
                saveUserLevel(context, it)
            }
        }
        return localUserLevel

    }

    @SuppressLint("CheckResult")
    private fun loadUserLevelRemote(
        context: Context,
        dealUserLevel: (IVTUserLevel) -> Unit
    ) {
        val packageName = context.packageName
        val geID =
            if (packageName.contains("cartoon")) "391" else if (packageName.contains("photoeditor")) "383" else return

        RemoteRepository.service.loadCurrentUserAdLevel(
            packageName, SystemUtil.loadCustomUserId(context), geID
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