package kr.shlim.roomcornermovie.view.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SnapHelper
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kr.shlim.roomcornermovie.BaseActivity
import kr.shlim.roomcornermovie.R
import kr.shlim.roomcornermovie.databinding.ActivityMainBinding
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.utils.DateUtil
import kr.shlim.roomcornermovie.view.dialog.DatePickerDialogFragment
import kr.shlim.roomcornermovie.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main, MainViewModel::class) {

    private val TAG = MainActivity::class.java.simpleName

    private val viewMdoel: MainViewModel by viewModels()                       // activity-ktx

    private lateinit var mMoviePagerAdapter: MovieRecyclerViewAdaper

    var updateIndex = 0                                                         // viewpager update Index

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d("[timecheck] MainActivity onCrete Start")
        super.onCreate(savedInstanceState)

        // 영화 리스트 업데이트 시 View Update
        viewMdoel.movieList.observe(this, androidx.lifecycle.Observer { data ->
            adapterUpdate(data as ArrayList<NaverMovieListDTO>)

            val msg = data.toString()

            val title = data.get(0).items.get(0).title
            Logger.log(4, TAG, "onCreate() observe called with: data = [$title], {${data.size}", null)
        })

        // 영화 아이템 업데이트 시 View Update
        viewMdoel.movieItemList.observe(this, androidx.lifecycle.Observer {
            val title = it.get(0).title
            Logger.log(4, TAG, "onCreate() observe movieItemList : data = [$title], {${it.size - 1}, updateIndex : {$updateIndex}", null)
            mMoviePagerAdapter.refreshAdapter(updateIndex)
            updateIndex++

        })


        // 로딩 이벤트 Observe
        viewModel.loadingStatus.observe(this, androidx.lifecycle.Observer {
            when (it) {
                MainViewModel.LoadingStatus.LOADING -> {
                    movie_loading.visibility = View.VISIBLE
                    movie_loading.startLoadingAnimation()
                }
                MainViewModel.LoadingStatus.DONE -> {
                    movie_loading.visibility = View.GONE
                    movie_loading.stopLoadingAnimation()
                }
                MainViewModel.LoadingStatus.ERROR-> {
                    Log.d(TAG, "MainViewModel.LoadingStatus.ERROR ")
                }
            }
        })

        initView()
        initViewPager()
//        initRecyclerView()

        Logger.d("[timecheck] MainActivity onCrete end")
    }

    fun initView() {

        // date_edit setting
        val expDate = DateUtil.getYesterDay("yyyy-MM-dd")
        date_edit.setText(expDate)
        date_edit.setOnClickListener {
            dateClickEvent()
        }

        // request movie list
        val targetDt = DateUtil.getYesterDay("yyyyMMdd")
        viewMdoel.getMovieList(targetDt)
    }

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var snapHelper: SnapHelper

    fun initViewPager() {
        // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)

        // You need to retain one page on each side so that the next and previous items are visible
        viewpager.offscreenPageLimit = 3
        viewpager.clipChildren = false
        viewpager.clipToPadding = false

        mMoviePagerAdapter = MovieRecyclerViewAdaper(this, arrayListOf<NaverMovieListDTO>()) { view, url ->
            val pair_thumb = Pair(view, view.transitionName)
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, pair_thumb)

            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("imgUrl", url)
            startActivity(intent, optionsCompat.toBundle())
        }
        mMoviePagerAdapter.setHasStableIds(true)
        viewpager.adapter = mMoviePagerAdapter


// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->

//            Logger.d("page.width : ${page.width}")

            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.15f * Math.abs(position))
            // If you want a fading effect uncomment the next line:
            page.alpha = 0.25f + (1 - Math.abs(position))
        }

        viewpager.setPageTransformer(pageTransformer)
    }

    fun MainOnClick(v: View) {
        when (v.id) {
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

            R.id.refresh_button->{
                val position = viewpager.currentItem

//                val data = mMoviePagerAdapter.movieInfos?.get(position)
                mMoviePagerAdapter.refreshAdapter(position)
            }
        }
    }

    /**
     *
     * @param list
     */
    fun adapterUpdate(list: ArrayList<NaverMovieListDTO>) {
//        mMovieAdapter.addData(list.get(0))
        mMoviePagerAdapter.addData(list.get(0))
    }

    /**
     * 현재 날짜 리턴
     */
    fun getCurrentYYMMDD(): String {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1);                           // 오늘날짜는 팅아직 영화진응위원회에서 값이 안오기 때문에 전날짜 셋팅
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val result = simpleDateFormat.format(calendar.time)
        return result
    }


    /**
     * date click 시 팝업을
     */
    fun dateClickEvent() {
        mMoviePagerAdapter.movieListRemvoeAll()

        updateIndex = 0

        val editDate = date_edit.text.toString()
        val cal = Calendar.getInstance()
        val t = editDate.split("-")
        cal.set(t.get(0).toInt(), t.get(1).toInt(), t.get(2).toInt())

        val callback = fun(year: Int, month: Int, day: Int) {
            val cal = Calendar.getInstance()
            cal.set(year, month, day)

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val simpleDateFormatRequest = SimpleDateFormat("yyyyMMdd")

            val result = simpleDateFormat.format(cal.time)
            val targetDt = simpleDateFormatRequest.format(cal.time)
            date_edit.setText(result)

            viewMdoel.getMovieList(targetDt)
        }
        DatePickerDialogFragment(cal.year, cal.month - 1, cal.dayOfMonth, callback).show(supportFragmentManager, "")
    }
}