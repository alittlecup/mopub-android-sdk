package mobi.idealabs.ads.report;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.network.AdResponse;

import java.util.HashMap;
import java.util.Map;


public final class TrackEventManager {
    private static Map<String, TrackEvent> trackEventMap = new HashMap<String, TrackEvent>();

    /**
     * 当一个广告商请求开始时
     *
     * @param adResponse 广告商的请求内容
     */
    public static void trackWaterFallItemStart(AdResponse adResponse) {
        String requestId = adResponse.getRequestId();
        TrackEvent trackEvent = trackEventMap.get(requestId);
        if (trackEvent == null) {
            trackEvent = new TrackEvent(adResponse.getAdUnitId(), requestId);
            trackEventMap.put(requestId, trackEvent);
        }
        trackEvent.trackEventStart(adResponse);
    }

    /**
     * 当全部广告商请求结束时
     *
     * @param
     */
    public static void trackWaterFallFail(AdResponse adResponse) {
        TrackEvent trackEvent = trackEventMap.get(adResponse.getRequestId());
        if (trackEvent != null) {
            trackEvent.trackWaterFallFail();
        }

    }

    /**
     * 当某一个广告商请求成功时
     */
    public static void trackWaterFallSuccess(AdResponse adResponse) {
        TrackEvent trackEvent = trackEventMap.get(adResponse.getRequestId());
        if (trackEvent != null) {
            trackEvent.trackWaterFallSuccess(adResponse);
        }
    }

    /**
     * 当某一个广告商请求失败时
     */
    public static void trackWaterFallItemFail(AdResponse adResponse, MoPubErrorCode errorCode) {
        TrackEvent trackEvent = trackEventMap.get(adResponse.getRequestId());
        if (trackEvent != null) {
            trackEvent.trackWaterFallItemFail(adResponse, errorCode);
        }
    }

    /**
     * 单击事件跟踪
     */
    public static void trackClick(AdResponse adResponse) {
        TrackEvent trackEvent = trackEventMap.get(adResponse.getRequestId());
        if (trackEvent != null) {
            trackEvent.reportClick(adResponse);
        }
    }

    /**
     * 曝光事件跟踪
     */
    public static void trackImpression(AdResponse adResponse) {
        TrackEvent trackEvent = trackEventMap.get(adResponse.getRequestId());
        if (trackEvent != null) {
            trackEvent.reportImpression(adResponse);
        }
    }

    /**
     * 激励事件跟踪
     */
    public static void trackReward() {

    }

    /**
     * 点击广告之后返回事件跟踪
     */
    public static void trackReturn() {

    }

}