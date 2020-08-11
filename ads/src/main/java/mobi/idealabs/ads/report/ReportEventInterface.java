package mobi.idealabs.ads.report;

import androidx.annotation.Keep;

import com.mopub.network.AdResponse;

/**
 * 上报事件回调接口
 */
@Keep
public interface ReportEventInterface {

    void reportRequestSummary();

    void reportClick(AdResponse adResponse);

    void reportImpression(AdResponse adResponse);

    void reportChance();

    void reportReward(String rewardCustomClassName);

}
