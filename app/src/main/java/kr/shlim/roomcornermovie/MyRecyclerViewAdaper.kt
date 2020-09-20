package kr.shlim.roomcornermovie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kr.shlim.roomcornermovie.data.dto.naver.NaverMovieItem
import kr.shlim.roomcornermovie.data.dto.naver.NaverMovieListDTO

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

        Glide.with(mContext).load(item.image).into(holder.mImageView)
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