package kr.shlim.roomcornermovie.view.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.movie_list_item.view.*
import kr.shlim.roomcornermovie.R
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO


class MovieRecyclerViewAdaper(
        context: Context,
        var movieInfos: ArrayList<NaverMovieListDTO>?,
        val itemClick: (v: View, imgUrl: String?) -> Unit
) : RecyclerView.Adapter<MovieRecyclerViewAdaper.MovieViewHolder>() {

    val mContext : Context by lazy { context }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false)

        return MovieViewHolder(v)
    }

    override fun getItemCount(): Int {
        return movieInfos?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
//        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        Logger.d("onBindViewHolder position : $position")
        val movieInfo = movieInfos?.get(position)
        val item = movieInfo?.items?.get(0)

        (mContext as Activity).runOnUiThread(Runnable {
            Logger.d("viewholder imageUrl : ${item?.image}")
            Logger.d("viewholder position : ${position} , holder.mImageView.width : ${holder.mImageView.width}")
            Logger.d("viewholder position : ${position} , holder.mImageView.height : ${holder.mImageView.height}")


            Glide.with(mContext as Activity).load(item?.image).transform(
                    FitCenter(), RoundedCorners(
                    100
            )
            ).placeholder(R.drawable.movie_slate_new)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                        ): Boolean {
//                        TODO("Not yet implemented")
                            return false
                        }

                        override fun onResourceReady(
                                resource: Drawable,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                        ): Boolean {
                            val bitMap = resource.toBitmap()


                            val params = holder.mImageView.layoutParams
                            params.width = 998
                            params.height = 1050
                            holder.mImageView.layoutParams = params

                            Logger.d("position : ${position} , bitMap.width : ${bitMap?.width}")
                            Logger.d("position : ${position} , bitMap.height : ${bitMap?.height}")
                            Logger.d("position : ${position} , bitMap.size before : ${holder.mImageView.width}")

                            if (bitMap.width > bitMap.height) {
//                                return false
                                if(bitMap.height < 500) {
                                    Logger.e("position : ${position} , bitMap.height < 500!!!!!!!!!!!!!!!!!")


                                    refreshAdapter(position)
                                    return false
                                }


                                val rate = 650 / bitMap.width.toFloat()
                                val newHeight = (bitMap.height * rate).toInt()
                                val newWidth = 300
                                val scaleBitmap = Bitmap.createScaledBitmap(bitMap, newWidth, newHeight, true)

                                Logger.d("position : ${position} , bitMap.width scale : ${newWidth}")
                                Logger.d("position : ${position} , bitMap.height scale : ${newHeight}")
                                Logger.d("position : ${position} , bitMap.rate rate : ${rate}")


                                holder.mImageView.setImageDrawable(resource)

                                val params = holder.mImageView.layoutParams
                                params.width = 998
                                params.height = newHeight
                                holder.mImageView.layoutParams = params


                                Logger.d("position : ${position} , bitMap.size after width: ${holder.mImageView.width}")
                                Logger.d("position : ${position} , bitMap.size after height: ${holder.mImageView.height}")
                            }else {
                                Logger.d("position : ${position} , bitMap.width < bitMap.height} 1111")

                                if(bitMap.height < 500) {
                                    Logger.e("position : ${position} , bitMap.height < 500!!!!!!!!!!!!!!!!!")
                                    refreshAdapter(position)
                                    return false
                                }

                                Logger.d("position : ${position} , bitMap.width < bitMap.height} 222")
                                holder.mImageView.setImageDrawable(resource)
                                holder.mImageView.refreshDrawableState()
//                                refreshAdapter(position)
                            }
//                        holder.mImageView.setImageDrawable(resource)
                            return false
                        }
                    }).into(holder.mImageView)

//                .apply(RequestOptions().override(950, 1357)).centerInside().into(holder.mImageView)
//                .override(950, Target.SIZE_ORIGINAL).into(holder.mImageView)
//                .into(object : CustomTarget<Drawable>() {
//                    override fun onLoadCleared(placeholder: Drawable?) {
//
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable,
//                        transition: Transition<in Drawable>?
//                    ) {
//                        holder.mImageView.setImageDrawable(resource)
//                        val params = holder.mImageView.layoutParams
//                        params.width = 950
//                        params.height = 1357
//
//                        holder.mImageView.layoutParams = params
//                    }
//                })

            holder.mTextView.visibility = View.VISIBLE
            holder.mTextView.text = item?.title

        })

        holder.mImageView.setOnClickListener{

            itemClick(it, item?.image)
        }
    }


    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView : ImageView = itemView.imageview
        val mTextView : TextView = itemView.textview

        init {

        }

    }

    fun setData(list: ArrayList<NaverMovieListDTO>) {
        movieInfos = list
        notifyDataSetChanged()
    }

    fun addData(data: NaverMovieListDTO) {
        movieInfos?.add(data)
        notifyItemChanged((itemCount - 1) ?: 0)
    }

    fun refreshAdapter(index: Int) {
        notifyItemChanged(index)
    }

    fun movieListRemvoeAll() {
        movieInfos?.removeAll(movieInfos!!)
    }
}

