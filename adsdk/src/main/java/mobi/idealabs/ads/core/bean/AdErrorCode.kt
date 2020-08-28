package mobi.idealabs.ads.core.bean

import com.mopub.mobileads.MoPubErrorCode

data class AdErrorCode(val message: String?, val code: Int?) {
    companion object {
        fun convertMopubError(moPubErrorCode: MoPubErrorCode?): AdErrorCode {
            return AdErrorCode(
                moPubErrorCode?.name
                    ?: "Mopub not found Error", moPubErrorCode?.intCode ?: -1
            )
        }
    }
}