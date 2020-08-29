// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package mobi.idealabs.ads.core.bean;

import androidx.annotation.Keep;

import com.mopub.nativeads.MoPubNativeAdLoadedListener;

@Keep
public interface AdsNativeAdFeedListener extends MoPubNativeAdLoadedListener {
    /**
     *
     * 当尝试绑定Native到Recycler View 时回调
     * @param position The removed ad position.
     */
    void onAdBindView(int position);

    /**
     *
     * 当能够绑定Native到Recycler View 时回调
     * @param position The removed ad position.
     */
    void onAdShown(int position);
}
