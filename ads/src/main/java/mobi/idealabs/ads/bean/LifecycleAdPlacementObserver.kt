package mobi.idealabs.ads.bean

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import mobi.idealabs.ads.manage.AdManager


class LifecycleAdPlacementObserver(
    val lifecycle: Lifecycle,
    private val adPlacement: AdPlacement,
    public val adListener: AdListener
) : LifecycleEventObserver, AdListener by adListener {

    init {
        lifecycle.addObserver(this)
    }

    override fun equals(other: Any?): Boolean {
        return other is LifecycleAdPlacementObserver && this.lifecycle == other.lifecycle
    }

    override fun hashCode(): Int {
        return lifecycle.hashCode()
    }

    private fun shouldBeActive(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
    }

    private fun shouldBeCreate(): Boolean {
        return lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            removeListener()
            destroy()
        }
    }

    private fun removeListener() {
        adPlacement.removeLifecycleListener(this)
        AdManager.destroyAdPlacement(adPlacement)
    }

    fun destroy() {
        lifecycle.removeObserver(this)
    }

    internal fun shouldNotify(adPlacement: AdPlacement?): Boolean {
        return adPlacement == this.adPlacement && shouldBeActive()
    }

    internal fun shouldNotifyCreate(adPlacement: AdPlacement?): Boolean {
        return adPlacement == this.adPlacement && shouldBeCreate()
    }


}
