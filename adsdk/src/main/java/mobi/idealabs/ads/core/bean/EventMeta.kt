package mobi.idealabs.ads.core.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EventMeta constructor(

    val requestID: String,

    val placementName: String,

    @Expose
    @field:SerializedName("AdChanceNameIL")
    val adChanceName: String? = null,
    @Expose
    @field:SerializedName("AdTypeIL")
    val adTypeIL: String? = null,
    @Expose
    @field:SerializedName("AdItemIdIL")
    val adItemIdIL: String = "",
    @Expose
    @field:SerializedName("AdVendorNameIL")
    var adVendorNameIL: String? = null,
    @Expose
    @field:SerializedName("StartTimeIL")
    var startTimeIL: Long? = null,
    @Expose
    @field:SerializedName("EndTimeIL")
    var endTimeIL: Long? = null,
    @Expose
    @field:SerializedName("RequestResultIL")
    var requestResultIL: String? = null,
    @Expose
    @field:SerializedName("AdDurationIL")
    var duration: Int? = null,
    @Expose
    @field:SerializedName("AdFinishIL")
    var finish: Int? = null
)