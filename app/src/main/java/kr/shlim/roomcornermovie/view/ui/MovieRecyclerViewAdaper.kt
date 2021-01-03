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

class MovieRecyclerViewAdaper(context: Context, var movieInfos: ArrayList<NaverMovieListDTO>? , val itemClick : (v :View, imgUrl : String?) -> Unit) : RecyclerView.Adapter<MovieRecyclerViewAdaper.MyViewHolder>() {

    val mContext : Context by lazy { context }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false)

        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return movieInfos?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Logger.d("onBindViewHolder position : $position")
        val movieInfo = movieInfos?.get(position)
        val item = movieInfo?.items?.get(0)

        (mContext as Activity).runOnUiThread(Runnable {
            Logger.d("imageUrl : ${item?.image}")

            Glide.with(mContext as Activity).load(item?.image).transform(
                FitCenter(), RoundedCorners(
                    100
                )
            ).placeholder(R.drawable.movie_slate_new)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .override(holder.mImageView.width, holder.mImageView.height).into(holder.mImageView)

            holder.mTextView.visibility = View.VISIBLE
            holder.mTextView.text = item?.title

        })

        holder.mImageView.setOnClickListener{

            itemClick(it, item?.image)
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView : ImageView = itemView.imageview
        val mTextView : TextView = itemView.textview

        init {

        }

    }

    fun setData(list : ArrayList<NaverMovieListDTO>) {
        movieInfos = list
        notifyDataSetChanged()
    }

    fun addData(data : NaverMovieListDTO) {
        movieInfos?.add(data)
        notifyItemChanged((itemCount - 1) ?: 0)
    }

    fun refreshAdapter(index : Int) {
        notifyItemChanged(index)
    }

    fun movieListRemvoeAll() {
        movieInfos?.removeAll(movieInfos!!)
    }
}

