package mobi.idealabs.ads.core.bean

import androidx.annotation.StringDef

@StringDef(value = [EventType.REQUEST_SUMMARY, EventType.AD_CHANCE, EventType.AD_CLICK,
    EventType.AD_IMPRESSION, EventType.AD_RETURN, EventType.AD_REWARD])
internal annotation class EventType {
    companion object {
        const val REQUEST_SUMMARY = "request_summary"
        const val AD_CHANCE = "ad_chance"
        const val AD_IMPRESSION = "ad_impression"
        const val AD_CLICK = "ad_click"
        const val AD_RETURN = "ad_return"
        const val AD_REWARD = "ad_reward"
    }
}
