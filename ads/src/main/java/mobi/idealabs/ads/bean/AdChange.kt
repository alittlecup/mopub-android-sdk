package mobi.idealabs.ads.bean


typealias AdInitListener = () -> Unit

typealias AdPlacementFinder = (adChanceName: String) -> AdPlacement?
