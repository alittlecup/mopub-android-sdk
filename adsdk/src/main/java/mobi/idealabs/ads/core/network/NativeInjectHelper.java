package mobi.idealabs.ads.core.network;

import com.mopub.nativeads.NativeAdSource;

import java.util.HashMap;

public class NativeInjectHelper {
    public static final HashMap<String, NativeAdSource> nativeAdSources = new HashMap<>();

    public static NativeAdSource getNativeAdSource(String key) {
        NativeAdSource nativeAdSource = nativeAdSources.get(key);
        if (nativeAdSource == null) {
            nativeAdSource = NativeAdSource.getInstance();
            nativeAdSources.put(key, nativeAdSource);
        }
        return nativeAdSource;
    }
}
