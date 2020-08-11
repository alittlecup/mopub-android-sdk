package mobi.idealabs.ads.report.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.mopub.common.ClientMetadata
import com.mopub.common.ClientMetadata.MoPubNetworkType.*
import com.mopub.common.MoPub
import com.mopub.common.SharedPreferencesHelper
import com.mopub.common.privacy.ConsentStatus
import mobi.idealabs.ads.manage.AdSdk
import java.util.*
import java.util.concurrent.TimeUnit


object SystemUtil {
    const val SP_AD_NAME = "idealabsAdSetting"
    const val SP_AD_UUID_KEY = "uuid"
    const val SP_AD_DAY_KEY = "today"
    var context = AdSdk.application
        get() {
            return AdSdk.application
        }

    private var clientMetadata: ClientMetadata? = ClientMetadata.getInstance()
        get() {
            return if (context != null) {
                Log.d("SystemUtil", ": clientMetadata")
                ClientMetadata.getInstance(context!!)
            } else ClientMetadata.getInstance()
        }


    /**
     * 0-> 允许
     * 1-> 不允许
     * 2-> 未知
     */
    fun loadGDPRStatus(): String {
        return when (MoPub.getPersonalInformationManager()?.personalInfoConsentStatus) {
            ConsentStatus.EXPLICIT_YES, ConsentStatus.POTENTIAL_WHITELIST -> "0"
            ConsentStatus.DNT, ConsentStatus.EXPLICIT_NO -> "1"
            else -> "2"
        }
    }

    /**
     * API 版本
     */
    fun loadApiVersion(): Int {
        return 1
    }


    /**
     * CPU 型号
     */
    fun loadCpuNumber(): String {
        return Build.HARDWARE
    }

    /**
     * 设备编译指纹
     */
    fun loadFingerPrint(): String {
        return Build.FINGERPRINT
    }


    /**
     * 手机RAM
     */
    fun loadRamSize(): String {
        val activityManager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return "${memoryInfo.totalMem / 1024} KB"
    }

    /**
     * 手机屏幕 size
     */
    fun loadScreenSize(): String {
        if (clientMetadata == null) return "'"
        return "${clientMetadata!!.deviceDimensions.x}x${clientMetadata!!.deviceDimensions.y}"
    }


    /**
     * 3位系统版本
     */
    fun loadSystemVersion(): String {
        if (clientMetadata == null) return "'"
        return clientMetadata!!.deviceOsVersion
    }

    /**
     * 2位国家代码
     */
    private var countryFromSIM = true

    fun loadCountryNumber(): String {
        if (clientMetadata == null) return "'"
        var isoCountryCode = clientMetadata!!.isoCountryCode
        countryFromSIM = isoCountryCode.isNotEmpty()
        if (!countryFromSIM) {
            var locale: Locale? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context?.resources?.configuration?.locales?.get(0)
            } else {
                context?.resources?.configuration?.locale
            }
            isoCountryCode = locale?.country
        }

        return isoCountryCode
    }

    /**
     * 国家数据来源
     * APP 获取国家信息来源于 SIM卡或 手机设置的 setting
     * SIM 卡信息准确，
     * Setting 较不准确，该字段用于服务端二次验证位置的准确性
     */
    fun loadCountrySource(): String {
        return if (countryFromSIM) "SIM" else "Setting"
    }

    /**
     * 平台
     */
    fun loadPlatform(): String {
        return "Android"
    }

    /**
     * 设备类型：Phone、Pad
     */
    fun loadDeviceType(): String {
        return "Phone"
    }

    /**
     * 设备品牌:Apple, Huawei, HTC, Samsung, Vivo……
     */
    fun loadDeviceBand(): String {
        return Build.BRAND
    }

    /**
     * 运营商
     */
    fun loadNetworkOperator(): String {
        if (clientMetadata == null) return "'"
        return clientMetadata!!.networkOperator
    }

    /**
     *
     * app Bundle id
     */
    fun loadAppBundleId(): String {
        if (clientMetadata == null) return "'"
        return clientMetadata!!.appPackageName
    }

    /**
     * 设备型号
     */
    fun loadDeviceModel(): String {
        if (clientMetadata == null) return "'"
        return clientMetadata!!.deviceModel
    }

    /**
     *设备系统语言 不同品牌手机对于同一语言的上报结果可能会不同，读取结果为"地区-语言"形式
     */
    fun loadLanguage(): String {
        return if (context == null) "US" else ClientMetadata.getCurrentLanguage(context!!)
    }

    /**
     * Android 系统版本
     */
    fun loadAndroidSdkVersion(): String {
        if (clientMetadata == null) return "'"
        return clientMetadata!!.sdkVersion
    }

    /**
     *  用户唯一标示  UUID
     *  用户的唯一标识，即每个用户唯一，如果重装了就会换一个
     */
    fun loadUUID(curContext: Context? = context): String {
        if (curContext == null) return ""
        var sharedPreferences = SharedPreferencesHelper.getSharedPreferences(
            curContext,
            SP_AD_NAME
        )
        var uuid = sharedPreferences.getString(SP_AD_UUID_KEY, "")
        if (uuid.isNullOrEmpty()) {
            uuid = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(SP_AD_UUID_KEY, uuid).apply()
        }
        return uuid
    }

    /**
     * 时区
     */
    fun loadTimeZone(): Long {
        val mCalendar = GregorianCalendar()
        val mTimeZone = mCalendar.timeZone
        val mGMTOffset = mTimeZone.rawOffset.toLong()
        System.out.printf(
            "GMT offset is %s hours",
            TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)
        )
        return mGMTOffset
    }

    /**
     * IP 地址
     */

    fun loadDevicesIP(): String {
        return "10.0.0.232"
    }

    /**
     * 获取网络状态 3g 4g wifi
     */

    fun loadNetworkState(): String {
        if (clientMetadata == null) return "'"
        return when (clientMetadata!!.activeNetworkType) {
            WIFI -> "wifi"
            GG -> "2g"
            GGG -> "3g"
            GGGG -> "4g"
            else -> ""
        }
    }

    fun loadGoogleAdId(): String {
        if (clientMetadata == null) return ""
        return if (MoPub.canCollectPersonalInformation()) {
            clientMetadata!!.moPubIdentifier.advertisingInfo.getIdentifier(true)
        } else {
            ""
        }

    }

    fun loadCurrentDay(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-$month-$day"
    }

    fun loadPreviewDay(context: Context): String {
        return SharedPreferencesHelper.getSharedPreferences(
            context,
            SP_AD_DAY_KEY
        ).getString(
            SP_AD_DAY_KEY, ""
        )
            ?: ""
    }

    fun saveToday() {
        if (context == null) return
        SharedPreferencesHelper.getSharedPreferences(
            context!!,
            SP_AD_DAY_KEY
        ).edit().putString(
            SP_AD_DAY_KEY,
            loadCurrentDay()
        ).apply()
    }

    fun loadAppVersionCode(): Int {
        return try {
            val packageName = context?.packageName
            val packageInfo = context?.packageManager?.getPackageInfo(packageName, 0)
            packageInfo?.versionCode ?: 0
        } catch (exception: Exception) {
            -1
        }

    }

    fun loadAppVersionName(): String {
        return try {
            val packageName = context?.packageName
            val packageInfo = context?.packageManager?.getPackageInfo(packageName, 0)
            packageInfo?.versionName ?: ""
        } catch (exception: Exception) {
            ""
        }
    }

    fun loadCustomUserId(context: Context): String {
        var googleAdId = loadGoogleAdId()
        if (clientMetadata != null && MoPub.canCollectPersonalInformation()) {
            if (googleAdId.isEmpty()) {
                googleAdId = loadUUID(context)
            }
        }
        return googleAdId
    }


}