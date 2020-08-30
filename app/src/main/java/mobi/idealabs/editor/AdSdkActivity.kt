package mobi.idealabs.editor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import mobi.idealabs.ads.core.bean.AdPlacement
import mobi.idealabs.ads.core.bean.AdType
import mobi.idealabs.ads.core.controller.AdManager
import mobi.idealabs.ads.core.controller.AdSdk
import mobi.idealabs.editor.databinding.MainActivityBinding

class AdSdkActivity : AppCompatActivity() {
    lateinit var mBinding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.main_activity)
        mBinding.retry.setOnClickListener {
            AdSdk.canRetry = !AdSdk.canRetry
            mBinding.retry.text = "Retry: ${AdSdk.canRetry}"
        }
        mBinding.retry.text = "Retry: ${AdSdk.canRetry}"
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapter = MainItemAdapter(AdConst.adPlacements, this)
        adapter.onClickListener = object : MainItemAdapter.onAdapterItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val adPlacement = AdConst.adPlacements[pos]
                when (adPlacement.adType) {
                    AdType.NATIVE -> {
                        startActivity(Intent(this@AdSdkActivity, NativeActivity::class.java))

                    }
                    AdType.BANNER -> {
                        startActivity(Intent(this@AdSdkActivity, BannerActivity::class.java))

                    }
                    AdType.REWARDED_VIDEO -> {
                        startActivity(Intent(this@AdSdkActivity, RewardActivity::class.java))

                    }
                    AdType.INTERSTITIAL -> {
                        startActivity(Intent(this@AdSdkActivity, IntersititalActivity::class.java))

                    }
                }

            }

        }
        mBinding.recyclerView.adapter = adapter
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        AdManager.preloadAdPlacement(AdConst.InterstitialAdPlacement)

    }
}

class MainItemAdapter(val adPlacements: List<AdPlacement>, val context: Context) :
    RecyclerView.Adapter<MainItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle = view.findViewById<TextView>(R.id.title)
        val itemContent = view.findViewById<TextView>(R.id.content)
        val itemContentId = view.findViewById<TextView>(R.id.content_id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_main_adplacement, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adPlacements.size
    }

    var onClickListener: onAdapterItemClickListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val adPlacement = adPlacements[position]
        holder.itemTitle.setText(adPlacement.adType.name.toUpperCase())
        holder.itemContent.setText("Ads ${adPlacement.adType.name.toUpperCase()} Sample")
        holder.itemContentId.setText("${adPlacement.adUnitId}")

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(it, position)
        }
    }

    interface onAdapterItemClickListener {
        fun onClick(view: View, pos: Int)
    }
}