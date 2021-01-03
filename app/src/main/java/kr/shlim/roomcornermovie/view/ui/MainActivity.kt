package kr.shlim.roomcornermovie.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.orhanobut.logger.Logger
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kr.shlim.roomcornermovie.BaseActivity
import kr.shlim.roomcornermovie.R
import kr.shlim.roomcornermovie.databinding.ActivityMainBinding
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.view.dialog.DatePickerDialogFragment
import kr.shlim.roomcornermovie.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main, MainViewModel::class) {
    
    private val TAG = MainActivity::class.java.simpleName
    
    val compositeDisposable : CompositeDisposable = CompositeDisposable()

    private val viewMdoel : MainViewModel by viewModels()                       // activity-ktx

    private lateinit var mMovieAdapter : MovieRecyclerViewAdaper

    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d("[timecheck] MainActivity onCrete Start")
        super.onCreate(savedInstanceState)

        // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)

        // You need to retain one page on each side so that the next and previous items are visible
        viewpager.offscreenPageLimit = 3
        viewpager.clipChildren = false
        viewpager.clipToPadding = false



        mMovieAdapter =  MovieRecyclerViewAdaper(this, arrayListOf<NaverMovieListDTO>()) { view, url ->
            val pair_thumb = Pair(view, view.transitionName)
            val optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, pair_thumb)

            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("imgUrl", url)
            startActivity(intent, optionsCompat.toBundle())
        }
        viewpager.adapter = mMovieAdapter


// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.15f * Math.abs(position))
            // If you want a fading effect uncomment the next line:
            page.alpha = 0.25f + (1 - Math.abs(position))
        }

        viewpager.setPageTransformer(pageTransformer)

        val currentDate = getCurrentYYMMDD()
        date_edit.setText(currentDate)
        date_edit.setOnClickListener {

            mMovieAdapter.movieListRemvoeAll()
            index = 0

            val editDate = date_edit.text.toString()
            val cal = Calendar.getInstance()
            val t = editDate.split("-")
            cal.set(t.get(0).toInt(), t.get(1).toInt(), t.get(2).toInt())

            val callback = fun (year : Int, month : Int, day : Int){
                val cal = Calendar.getInstance()
                cal.set(year, month, day)

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val simpleDateFormatRequest = SimpleDateFormat("yyyyMMdd")

                val result = simpleDateFormat.format(cal.time)
                val targetDt = simpleDateFormatRequest.format(cal.time)
                date_edit.setText(result)

                viewMdoel.getMovieList(targetDt)
            }

            DatePickerDialogFragment(cal.year, cal.month-1, cal.dayOfMonth, callback).show(supportFragmentManager, "")
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE , -1);                           // 오늘날짜는 팅아직 영화진응위원회에서 값이 안오기 때문에 전날짜 셋팅
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        val targetDt = simpleDateFormat.format(calendar.time)

        viewMdoel.movieList.observe(this, androidx.lifecycle.Observer { data ->
            adapterUpdate(data as ArrayList<NaverMovieListDTO>)

            val msg = data.toString()

            val title = data.get(0).items.get(0).title
            Logger.log(4, TAG, "onCreate() observe called with: data = [$title], {${data.size}", null)
            }
        )


        viewMdoel.movieItemList.observe(this, androidx.lifecycle.Observer {

            val title = it.get(0).title
            Logger.log(4, TAG, "onCreate() observe movieItemList : data = [$title]", null)
            Logger.log(4, TAG, "onCreate() observe movieItemList : data = [$title], {${it.size-1}", null)

            mMovieAdapter.refreshAdapter(index)
            index++
        })

        viewMdoel.getMovieList(targetDt)
        Logger.d("[timecheck] MainActivity onCrete end")
    }

    fun MainOnClick(v: View) {
        when(v.id) {
            R.id.naver_api -> {
            }
            R.id.kmdb_api -> {
            }
            R.id.boxoffice_api -> {
            }

            R.id.combine_api -> {
            }

            R.id.flatmap_api -> {
            }
        }
    }

    /**
     *
     * @param list
     */
    fun adapterUpdate(list : ArrayList<NaverMovieListDTO>) {
        mMovieAdapter.addData(list.get(0))
    }

    /**
     * 현재 날짜 리턴
     */
    fun getCurrentYYMMDD() : String {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE , -1);                           // 오늘날짜는 팅아직 영화진응위원회에서 값이 안오기 때문에 전날짜 셋팅
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val result = simpleDateFormat.format(calendar.time)
        return result
    }

}