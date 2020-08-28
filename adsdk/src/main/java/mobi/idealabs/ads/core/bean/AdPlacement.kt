package mobi.idealabs.ads.core.bean

@Suppress("UNCHECKED_CAST")
data class AdPlacement(
    val name: String,
    val adUnitId: String,
    val adType: AdType,
    val adSize: BannerAdSize = BannerAdSize.MATCH_VIEW,
    var mode: PlacementMode = PlacementMode.DESTROY
) {
    val params = mutableMapOf<String, String>()

    init {
        require(adType != AdType.BANNER || adSize != BannerAdSize.MATCH_VIEW) {
            "The BannerType Placement must have  AdSize"
        }
        params.clear()
    }

    var chanceName: String = ""

    private val lifecycleListeners = mutableSetOf<LifecycleAdPlacementObserver>()

    internal fun addLifecycleListener(lifecycleAdPlacementObserver: LifecycleAdPlacementObserver) {
        lifecycleListeners.add(lifecycleAdPlacementObserver)
    }

    internal fun containsLifecycleListener(lifecycleAdPlacementObserver: LifecycleAdPlacementObserver): Boolean {
        return lifecycleListeners.contains(lifecycleAdPlacementObserver)
    }

    internal fun removeLifecycleListener(lifecycleAdPlacementObserver: LifecycleAdPlacementObserver) {
        lifecycleListeners.remove(lifecycleAdPlacementObserver)
    }

    internal fun findActiveListeners(adPlacement: AdPlacement): List<AdListener> {
        return lifecycleListeners.filter { it.shouldNotify(adPlacement) }
    }

    internal fun findCreateListeners(adPlacement: AdPlacement): List<AdListener> {
        return lifecycleListeners.filter { it.shouldNotifyCreate(adPlacement) }
    }

    internal fun allListeners(): List<AdListener> {
        return lifecycleListeners.toList()
    }


    fun clearListeners() {
        if (lifecycleListeners.isNotEmpty()) {
            lifecycleListeners.forEach {
                it.destroy()
            }
            lifecycleListeners.clear()
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is AdPlacement && name == other.name && adUnitId == other.adUnitId && adType == other.adType && adSize == other.adSize
    }
}


