package mobi.idealabs.ads.core.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLevelResult(val state: String) : Parcelable {
    fun toIVTUserLevel(): IVTUserLevel {
        return when (state) {
            "0" -> SuperiorUser
            "1" -> ViciousUser
            "2" -> WorseUser
            else -> SuperiorUser
        }
    }

    fun toInt(): Int {
        return toIVTUserLevel().level
    }
}