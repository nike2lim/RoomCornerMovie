package kr.shlim.roomcornermovie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerViewAdaper(context : Context, names : ArrayList<String>) : RecyclerView.Adapter<MyRecyclerViewAdaper.MyViewHolder>() {

    var movieName : ArrayList<String>
    var mContext : Context? = null

    init {
        this.movieName = names
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return movieName?.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        movieName?.get(position).let {
////            holder.mImageView.background = mContext?.getDrawable(R.drawable.iphone)
////            holder.mTextView.text = it
////        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val mImageView : ImageView = itemView.imageview
//        val mTextView : TextView = itemView.textview

    }
}