package kr.shlim.roomcornermovie.view.ui

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kr.shlim.roomcornermovie.R
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO

class MyRecyclerViewAdaper(context: Context, val movieInfos: ArrayList<NaverMovieListDTO>, val itemClick : (v :View, imgUrl : String) -> Unit) : RecyclerView.Adapter<MyRecyclerViewAdaper.MyViewHolder>() {

    val mContext : Context by lazy { context }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return movieInfos?.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Logger.d("onBindViewHolder position : $position")
        val movieInfo = movieInfos.get(position)
        val item = movieInfo.items.get(0)

        val code = item.link.substringAfterLast("=")
        val url = "https://movie.naver.com/movie/bi/mi/photoViewPopup.nhn?movieCode=" + code


        var imageUrl = ""


//        Thread(Runnable {
//            var doc = Jsoup.connect(url).get()
//            val element = doc.select("img")
//
//            Logger.d("element : ${element.toString()}")
//
//            imageUrl = element.get(0).absUrl("src")
//
//            Logger.d("element imageUrl : ${imageUrl}")
//
//            (mContext as Activity).runOnUiThread(Runnable {
//                Glide.with(mContext as Activity).load(imageUrl).transform(FitCenter(), RoundedCorners(100)).into(holder.mImageView)
//
//                holder.mTextView.visibility = View.VISIBLE
//                holder.mTextView.text = item.title
//
//            })
//
//        }).start()



        (mContext as Activity).runOnUiThread(Runnable {
            Logger.d("imageUrl : ${item.image}")

//            holder.mImageView.setBackgroundResource(R.drawable.movie_slate)

            val t = Glide.get(mContext as Activity).setMemoryCategory(MemoryCategory.HIGH)
            val requestBuilder = Glide.with(mContext as Activity).load(item.image).transform(
                FitCenter(), RoundedCorners(
                    100
                )
            ).placeholder(R.drawable.movie_slate_new)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
//                .onlyRetrieveFromCache(true)
                .override(holder.mImageView.width, holder.mImageView.height).into(holder.mImageView)

            holder.mTextView.visibility = View.VISIBLE
            holder.mTextView.text = item.title

        })

        holder.mImageView.setOnClickListener{

            itemClick(it, item.image)
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView : ImageView = itemView.imageview
        val mTextView : TextView = itemView.textview

        init {

        }

    }


}

