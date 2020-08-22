package mobi.idealabs.ads;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.RequestParameters;


class TestInject {
    @NonNull
    private final MoPubNative.MoPubNativeNetworkListener mMoPubNativeNetworkListener = null;

    void loadAds(@NonNull final Activity activity,
                 @NonNull final String adUnitId,
                 final RequestParameters requestParameters) {
        loadAds(requestParameters, new MoPubNative(activity, adUnitId, mMoPubNativeNetworkListener));
    }

    void loadAds(final RequestParameters requestParameters,
                 final MoPubNative moPubNative) {

    }
}
