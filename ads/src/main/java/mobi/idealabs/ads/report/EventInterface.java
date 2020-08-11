package mobi.idealabs.ads.report;

import androidx.annotation.Keep;

import com.mopub.mobileads.MoPubError;
import com.mopub.network.AdResponse;

/**
 * MoPub 请求回调接口
 */
@Keep
public interface EventInterface extends ReportEventInterface {
    /**
     * MoPub开始请求时的回调
     */
    void trackMoPubRequestStart();

    /**
     * MoPub 请求成功的回调
     */
    void trackMoPubRequestSuccess();

    /**
     * 当一个广告商的请求开始时 回调
     *
     * @param adUnitId
     * @param eventName
     * @param adResponse
     */
    void trackEventStart(String adUnitId, String eventName, AdResponse adResponse);

    /**
     * WaterFall 其中一个广告商成功时的回调
     *
     * @param key
     */
    void trackWaterFallSuccess(String key);

    /**
     * WaterFall 其中一个广告商失败时的回调
     *
     * @param key
     * @param errorCode
     */
    void trackWaterFallItemFail(String key, MoPubError errorCode);

    /**
     * WaterFall 最终失败的回调
     */

    void trackWaterFallFail();

    String getCurrentRequestId();
}
