package mobi.idealabs.ads.report.network

import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.JsonObject
import mobi.idealabs.ads.report.utils.SystemUtil
@Keep
class DeviceInfoGenerator {

  private val GeApiVersion = "GeApiVersionIL"
  private val GdprIL = "GdprIL"
  private val GoogleAdvertisingIdIL = "GoogleAdvertisingIdIL"
  private val CpuModelIL = "CpuModelIL"
  private val FingerPrintIL = "FingerPrintIL"
  private val IpIL = "IpIL"
  private val RamIL = "RamIL"
  private val ScreenSizeIL = "ScreenSizeIL"
  private val OsVersion3IL = "OsVersion3IL"
  private val CountryIL = "CountryIL"
  private val CountrySourceIL = "CountrySourceIL"
  private val PlatformIL = "PlatformIL"
  private val DeviceTypeIL = "DeviceTypeIL"
  private val DeviceBrandIL = "DeviceBrandIL"
  private val DeviceModelIL = "DeviceModelIL"
  private val DeviceLanguageIL = "DeviceLanguageIL"
  private val AndroidSystemVersionIL = "AndroidSystemVersionIL"
  private val UniversallyUniqueIdIL = "UniversallyUniqueIdIL"
  private val TimeZoneIL = "TimeZoneIL"
  var deviceMap = mutableMapOf<String, Any>()

  fun createDeviceInfo(): JsonObject {
    addParams(GeApiVersion, 1)
    addParams(GdprIL, SystemUtil.loadGDPRStatus())
    addParams(GoogleAdvertisingIdIL, 1)
    addParams(CpuModelIL, SystemUtil.loadCpuNumber())
    addParams(FingerPrintIL, SystemUtil.loadFingerPrint())
    addParams(IpIL, SystemUtil.loadDevicesIP())
    addParams(RamIL, SystemUtil.loadRamSize())
    addParams(ScreenSizeIL, SystemUtil.loadScreenSize())
    addParams(OsVersion3IL, SystemUtil.loadSystemVersion())
    addParams(CountryIL, SystemUtil.loadCountryNumber())
    addParams(CountrySourceIL, SystemUtil.loadCountrySource())
    addParams(PlatformIL, SystemUtil.loadPlatform())
    addParams(DeviceTypeIL, SystemUtil.loadDeviceType())
    addParams(DeviceBrandIL, SystemUtil.loadDeviceBand())
    addParams(DeviceModelIL, SystemUtil.loadDeviceModel())
    addParams(DeviceLanguageIL, SystemUtil.loadLanguage())
    addParams(AndroidSystemVersionIL, SystemUtil.loadAndroidSdkVersion())
    addParams(UniversallyUniqueIdIL, SystemUtil.loadUUID())
    addParams(TimeZoneIL, SystemUtil.loadTimeZone())
    var gson = Gson()

    return gson.fromJson(gson.toJson(deviceMap), JsonObject::class.java)
  }


  private fun addParams(key: String, value: Any) {
    deviceMap[key] = value
  }

}