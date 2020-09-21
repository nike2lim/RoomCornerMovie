package kr.shlim.roomcornermovie

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kr.shlim.roomcornermovie.data.dto.naver.NaverMovieItem
import kr.shlim.roomcornermovie.data.dto.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.network.APIClient
import org.jsoup.Jsoup

class MyRecyclerViewAdaper(context : Context, val movieInfos : ArrayList<NaverMovieListDTO>) : RecyclerView.Adapter<MyRecyclerViewAdaper.MyViewHolder>() {

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


        Thread(Runnable {
            var doc = Jsoup.connect(url).get()
            val element = doc.select("img")
            imageUrl = element.get(0).absUrl("src")

            (mContext as Activity).runOnUiThread(Runnable {
                Glide.with(mContext as Activity).load(imageUrl).thumbnail(0.6f).into(holder.mImageView)
            })

        }).start()

        holder.mTextView.text = item.title

//        movieName?.get(position).let {
////            holder.mImageView.background = mContext?.getDrawable(R.drawable.iphone)
////            holder.mTextView.text = it
////        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView : ImageView = itemView.imageview
        val mTextView : TextView = itemView.textview

    }


}

