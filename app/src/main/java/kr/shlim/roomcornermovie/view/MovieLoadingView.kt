package kr.shlim.roomcornermovie.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.movie_loading.view.*
import kr.shlim.roomcornermovie.R
import java.util.jar.Attributes

class MovieLoadingView: ConstraintLayout{

    constructor(context: Context) : super(context) {
        val view = inflate(context, R.layout.movie_loading, this)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val view = inflate(context, R.layout.movie_loading, this)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val view = inflate(context, R.layout.movie_loading, this)
    }

    fun startLoadingAnimation() {
        loading.visibility = View.VISIBLE
        val animationDrawable = loading.drawable as AnimationDrawable
        animationDrawable.start()
    }

    fun stopLoadingAnimation() {
        val animationDrawable = loading.drawable as AnimationDrawable
        animationDrawable.stop()
        loading.visibility = View.GONE
    }
}