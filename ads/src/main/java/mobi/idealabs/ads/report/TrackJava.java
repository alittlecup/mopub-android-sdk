package mobi.idealabs.ads.report;

import com.mopub.network.AdResponse;


class TrackJava {
    public void inject(AdResponse adResponse) {
        TrackEventManager.trackWaterFallItemStart(adResponse);
    }
}
