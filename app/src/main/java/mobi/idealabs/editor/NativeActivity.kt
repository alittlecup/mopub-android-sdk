package mobi.idealabs.editor

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.mopub.nativeads.*
import com.smaato.sdk.nativead.NativeAd
import com.smaato.sdk.nativead.NativeAd.Listener
import com.smaato.sdk.nativead.NativeAdError
import com.smaato.sdk.nativead.NativeAdRenderer
import com.smaato.sdk.nativead.NativeAdRequest
import mobi.idealabs.ads.core.bean.AdErrorCode
import mobi.idealabs.ads.core.bean.AdListener
import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.controller.AdManager
import mobi.idealabs.editor.databinding.NativeActivityBinding

class NativeActivity() : AppCompatActivity() {
    lateinit var mBinding: NativeActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.native_activity)
        mBinding.model = this
        for (x in 0..100) {
            listenerDatas.add(ListenerData("Native List Ad ${x}"))
        }
        initRecyclerView()

    }

    private fun loadSmaatoNative() {
        var nativeAdRequest = NativeAdRequest.builder()
            .adSpaceId("130783664")
            .shouldReturnUrlsForImageAssets(false)
            .build()
        NativeAd.loadAd(this, nativeAdRequest, object : Listener {
            override fun onAdLoaded(p0: NativeAd, p1: NativeAdRenderer) {
                Log.d("NativeActivity", "onAdLoaded: ")
            }

            override fun onAdFailedToLoad(p0: NativeAd, p1: NativeAdError) {
                Log.d("NativeActivity", "onAdFailedToLoad: ")
            }

            override fun onAdImpressed(p0: NativeAd) {
                Log.d("NativeActivity", "onAdImpressed: ")
            }

            override fun onAdClicked(p0: NativeAd) {
                Log.d("NativeActivity", "onAdClicked: ")
            }

            override fun onTtlExpired(p0: NativeAd) {
                Log.d("NativeActivity", "onTtlExpired: ")
            }

        })
    }

    val listenerDatas = mutableListOf(
        ListenerData("onAdLoadSucceed"),
        ListenerData("onAdLoadFailed"),
        ListenerData("onAdShown"),
        ListenerData("onAdDismissed"),
        ListenerData("onAdClicked")
    )


    val adapter = SimpleTextItem(listenerDatas)

    fun load() {
        //加载Feed 广告
        AdManager.preloadAdPlacement(AdConst.NativeAdPlacement)
        moPubRecyclerAdapter?.registerAdRenderer(createFacebookAdRender(R.layout.native_layout))
        moPubRecyclerAdapter?.registerAdRenderer(createGoogleAdRender(R.layout.native_layout))
        moPubRecyclerAdapter?.registerAdRenderer(createSmaatoAdRender(R.layout.native_layout))
        moPubRecyclerAdapter?.registerAdRenderer(createMopubStaticAdRender(R.layout.native_layout))
//        loadSmaatoNative()

    }

    fun loadFeed() {
        moPubRecyclerAdapter?.loadAds(AdConst.TestNativeID)
    }

    fun show() {
        AdManager.showAdChange(
            this,
            AdConst.NativeAdPlacement.name,
            mBinding.adContainer,
            adListener = object : AdListener {
                override fun onAdStartLoad(adPlacement: AdPlacement?) {
                    Log.d("NativeActivity", "onAdStartLoad: ")
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

            }
        )
    }

    private fun notifyPos(pos: Int) {
        listenerDatas[pos].invoked = true
        adapter.notifyItemChanged(pos)
    }

    private fun createMopubStaticAdRender(@LayoutRes layoutRes: Int): MoPubStaticNativeAdRenderer {
        val viewBinder = ViewBinder.Builder(layoutRes)
            .mainImageId(R.id.native_ad_main_image)
            .iconImageId(R.id.native_ad_icon_image)
            .titleId(R.id.native_ad_title)
            .callToActionId(R.id.native_ad_call_to_action)
            .textId(R.id.native_ad_text)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build()
        return MoPubStaticNativeAdRenderer(viewBinder)
    }

    private fun createGoogleAdRender(@LayoutRes layoutRes: Int): GooglePlayServicesAdRenderer {
        val viewBinder = MediaViewBinder.Builder(layoutRes)
            .mediaLayoutId(R.id.native_ad_media_layout) // bind to your `com.mopub.nativeads.MediaLayout` element
            .iconImageId(R.id.native_ad_icon_image)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .callToActionId(R.id.native_ad_call_to_action)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build()
        return GooglePlayServicesAdRenderer(viewBinder)
    }

    private fun createSmaatoAdRender(@LayoutRes layoutRes: Int): SmaatoMoPubNativeRenderer {
        val viewBinder = MediaViewBinder.Builder(layoutRes)
            .mediaLayoutId(R.id.native_ad_main_image) // bind to your `com.mopub.nativeads.MediaLayout` element
            .iconImageId(R.id.native_ad_icon_image)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .callToActionId(R.id.native_ad_call_to_action)
            .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
            .build()
        return SmaatoMoPubNativeRenderer(viewBinder)
    }

    private fun createFacebookAdRender(@LayoutRes layoutRes: Int): FacebookAdRenderer {
        val facebookViewBinder = FacebookAdRenderer.FacebookViewBinder.Builder(layoutRes)
            .titleId(R.id.native_ad_title)
            .textId(R.id.native_ad_text)
            .mediaViewId(R.id.native_ad_fb_media)
            .adIconViewId(R.id.native_ad_fb_icon_image)
            .adChoicesRelativeLayoutId(R.id.native_ad_choices_relative_layout)
            .callToActionId(R.id.native_ad_call_to_action)
            .build()
        return FacebookAdRenderer(facebookViewBinder)
    }

    private var moPubRecyclerAdapter: MoPubRecyclerAdapter? = null

    private fun initRecyclerView() {
        //先创建三个视图渲染器
        val facebookAdRenderer = createFacebookAdRender(R.layout.native_layout)
        val mopubAdRender = createMopubStaticAdRender(R.layout.native_layout)
        val googleAdRenderer = createGoogleAdRender(R.layout.native_layout)
        val smaatoAdRender = createSmaatoAdRender(R.layout.native_layout)
        //设置广告展示位置
        val moPubServerPositioning = MoPubNativeAdPositioning.MoPubClientPositioning()
        for (x in 0..100) {
            if (x % 5 == 0) {
                moPubServerPositioning.addFixedPosition(x)
            }
        }

        //创建adapter
        moPubRecyclerAdapter =
            MoPubRecyclerAdapter(
                this,
                adapter,
                moPubServerPositioning
            )
        moPubRecyclerAdapter?.setAdLoadedListener(object : MoPubNativeAdLoadedListener {
            override fun onAdRemoved(position: Int) {
                Log.d("NativeActivity", "onAdRemoved: $position")

            }

            override fun onAdLoaded(position: Int) {
                Log.d("NativeActivity", "onAdLoaded: $position")
            }
        })
        //注册视图渲染器
        moPubRecyclerAdapter?.registerAdRenderer(mopubAdRender)
        moPubRecyclerAdapter?.registerAdRenderer(facebookAdRenderer as MoPubAdRenderer<*>)
        moPubRecyclerAdapter?.registerAdRenderer(googleAdRenderer as MoPubAdRenderer<*>)
        moPubRecyclerAdapter?.registerAdRenderer(smaatoAdRender as MoPubAdRenderer<*>)

        mBinding.recyclerView.adapter = moPubRecyclerAdapter

        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }
}