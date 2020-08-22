package mobi.idealabs.ads.core.bean


enum class BannerAdSize(val adSize: Int) {
    MATCH_VIEW(-1),
    HEIGHT_50(50),
    HEIGHT_90(90),
    HEIGHT_250(250),
    HEIGHT_280(280);

    companion object {
        fun valueOf(adSize: Int): BannerAdSize {
            return when (adSize) {
                50 -> HEIGHT_50
                90 -> HEIGHT_90
                250 -> HEIGHT_250
                280 -> HEIGHT_280
                else -> MATCH_VIEW
            }
        }
    }

}
