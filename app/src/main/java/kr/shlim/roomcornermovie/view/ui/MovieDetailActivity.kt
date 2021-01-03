package kr.shlim.roomcornermovie.view.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kr.shlim.roomcornermovie.R

class MovieDetailActivity : AppCompatActivity() {

    var mImageUrl : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val recvIntent = intent
        mImageUrl = recvIntent.getStringExtra("imgUrl")
        val drawable = recvIntent.getParcelableExtra<Bitmap>("drawable")

        progress.visibility = View.VISIBLE

        runOnUiThread(Runnable {
            Glide.with(this).load(mImageUrl).transform(
                FitCenter(), RoundedCorners(100)
            )
//                .addListener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return false
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    progress.visibility = View.GONE
////                    detail_imageview.setImageDrawable(resource)
//                    return false
//                }
//            })
//                .thumbnail(0.1f)
//                .skipMemoryCache(false)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.drawable.movie_slate_new)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .onlyRetrieveFromCache(true)
                .dontAnimate()
                .into(detail_imageview)
        })
    }
}