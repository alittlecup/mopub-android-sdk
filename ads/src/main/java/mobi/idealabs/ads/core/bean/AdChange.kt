package mobi.idealabs.ads.core.bean


typealias AdInitListener = () -> Unit

typealias AdPlacementFinder = (adChanceName: String) -> mobi.idealabs.ads.core.bean.AdPlacement?
