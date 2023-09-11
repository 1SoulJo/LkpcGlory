package com.lkpc.android.app.glory.ui.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lkpc.android.app.glory.R
import com.lkpc.android.app.glory.api_client.BasicRequestClient
import com.lkpc.android.app.glory.constants.WebUrls
import com.lkpc.android.app.glory.databinding.HomeAdItemBinding
import com.lkpc.android.app.glory.entity.AdContent
import com.lkpc.android.app.glory.ui.basic_webview.BasicWebviewActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeSlidePagerAdapter(f: Fragment) : FragmentStateAdapter(f) {
    var adContents: MutableList<AdContent> = mutableListOf()
    var fragments: MutableList<Fragment> = mutableListOf()
    private val ids: MutableList<Long> = mutableListOf()

    override fun getItemCount(): Int {
        return adContents.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemId(position: Int): Long {
        return ids[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return ids.contains(itemId)
    }

    fun setData(ads : MutableList<AdContent>) {
        adContents = ads.filter { it.forMobile == "t" }.toMutableList()
        for (ad in adContents) {
            val f = AdItemFragment(ad)
            val client = BasicRequestClient()
            client.getImage(WebUrls.IMG_ASSET.format(ad.linkImg!!),
                cb = object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, res: Response<ResponseBody>) {
                        if (res.isSuccessful && res.body() != null) {
                            f.img = BitmapFactory.decodeStream(res.body()!!.byteStream())
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            fragments.add(f)
            ids.add(f.hashCode().toLong())
        }
        notifyDataSetChanged()
    }
}

class AdItemFragment(val content: AdContent) : Fragment() {
    private var _binding: HomeAdItemBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    var img : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeAdItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (img != null) {
            binding.homeAdItemImg.setImageBitmap(img)

            TransitionManager.beginDelayedTransition(
                binding.homeAdItemLayout as ViewGroup, Fade(Fade.IN))
            binding.homeAdItemImg.visibility = View.VISIBLE
        }
        binding.homeAdItemLayout.setOnClickListener {
            val i = Intent(view.context, BasicWebviewActivity::class.java)
            i.putExtra("title", R.string.banner_title)
            i.putExtra("url", WebUrls.LPC_HOMEPAGE + content.linkUrl)
            view.context.startActivity(i)
        }
    }

    fun refresh() {
        if (img != null) {
            binding.homeAdItemImg.setImageBitmap(img)

            TransitionManager.beginDelayedTransition(
                binding.homeAdItemLayout as ViewGroup, Fade(Fade.IN))
            binding.homeAdItemImg.visibility = View.VISIBLE
        }
    }
}
