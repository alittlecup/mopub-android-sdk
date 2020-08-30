package mobi.idealabs.editor

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import mobi.idealabs.ads.core.bean.AdErrorCode
import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.BannerAdListener
import mobi.idealabs.ads.core.controller.AdManager
import mobi.idealabs.editor.databinding.BannerActivityBinding

class BannerActivity : AppCompatActivity() {

    lateinit var mBinding: BannerActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.banner_activity)
        initRecyclerView()
        mBinding.model = this
    }

    val listenerDatas = listOf(
        ListenerData("onAdLoadSucceed"),
        ListenerData("onAdLoadFailed"),
        ListenerData("onAdShown"),
        ListenerData("onAdDismissed"),
        ListenerData("onAdClicked")
    )
    val adapter = SimpleTextItem(listenerDatas)

    fun load() {
        AdManager.preloadAdPlacement(AdConst.BannerAdPlacement)
    }

    fun show() {
        AdManager.showAdChange(
            this,
            AdConst.BannerAdPlacement.name,
            mBinding.adContainer,
            adListener = object : BannerAdListener {
                override fun onBannerAdStartLoad(adPlacement: AdPlacement?) {
                    Log.d("BannerActivity", "onAdLoadStart: $adPlacement")
                }

                override fun onAdStartLoad(adPlacement: AdPlacement?) {
                    Log.d("BannerActivity", "onAdStartLoad: ")
                }

                override fun onAdLoaded(adPlacement: AdPlacement?) {
                    notifyPos(0)
                }

                override fun onAdFailed(adPlacement: AdPlacement?, adErrorCode: AdErrorCode) {
                    notifyPos(1)

                }

                override fun onAdShown(adPlacement: AdPlacement?) {
                    notifyPos(2)

                }

                override fun onAdDismissed(adPlacement: AdPlacement?) {
                    notifyPos(3)

                }

                override fun onAdClicked(adPlacement: AdPlacement?) {
                    notifyPos(4)

                }

            })
    }

    private fun notifyPos(pos: Int) {
        listenerDatas[pos].invoked = true
        adapter.notifyItemChanged(pos)
    }

    private fun initRecyclerView() {
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}