package kr.shlim.roomcornermovie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kr.shlim.roomcornermovie.data.dto.kobis.DailyBoxOffice
import kr.shlim.roomcornermovie.data.dto.kobis.KobisDailyBoxOfficeListDTO
import kr.shlim.roomcornermovie.data.dto.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.ext.base
import kr.shlim.roomcornermovie.ext.plusAssign
import kr.shlim.roomcornermovie.network.APIClient
import kr.shlim.roomcornermovie.view.HorizontalMarginItemDecoration
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val compositeDisposable : CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)

        // You need to retain one page on each side so that the next and previous items are visible
        viewpager.offscreenPageLimit = 3
        viewpager.clipChildren = false
        viewpager.clipToPadding = false

// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.25f * Math.abs(position))
            // If you want a fading effect uncomment the next line:
            // page.alpha = 0.25f + (1 - abs(position))
        }
        viewpager.setPageTransformer(pageTransformer)

// The ItemDecoration gives the current (centered) item horizontal margin so that
// it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration =
            HorizontalMarginItemDecoration(
                this,
                R.dimen.viewpager_current_item_horizontal_margin
            )
        viewpager.addItemDecoration(itemDecoration)
    }

    val movieList = arrayListOf<NaverMovieListDTO>()

    fun MainOnClick(v: View) {
        when(v.id) {
            R.id.naver_api -> {
//                val query = URLEncoder.encode("주식", "UTF-8")
                val query = "주식"
                compositeDisposable += APIClient.naverApi.getMovieList(
                    "qzbpCZyFMh8t4evQM71u",
                    "4kJ0rub2ub",
                    query,
                    100
                )
                    .base()
                    .subscribe(
                        {
                            Logger.d("it : ${it.toString()}")

                        },
                        {
                            Logger.e("naver getMovieList Error : ${it.message}")
                        }
                    )
            }
            R.id.kmdb_api -> {
                val title = "주식"
                compositeDisposable += APIClient.kmdbApi.getMovieList(
                    "kmdb_new2",
                    "N",
                    "EL0V4K1532U77G39NPKY",
                    title
                )
                    .base()
                    .subscribe(
                        {
                            Logger.d("kmdb it : ${it.toString()}")

                        },
                        {
                            Logger.e("kmdb getMovieList Error : ${it.localizedMessage}")
                        }
                    )

            }
            R.id.boxoffice_api -> {
                val title = "주식"
                compositeDisposable += APIClient.kobisApi.getDailyBoxOffice(
                    "0aaff6d6268952be706a0332880380af",
                    "20200830",
                    10
                )
                    .base()
                    .subscribe(
                        {
                            Logger.d("kobis it : ${it.toString()}")
                        },
                        {
                            Logger.e("kobis getDailyBoxOffice Error : ${it.message}")
                        }
                    )

            }

            R.id.combine_api -> {
                val t1 = getBoxOffice()
                val t2 = getNaverAPI()
//                val t3 = Single.just(1)
//                val t4 = Single.just(2)
//                Single.zip(t3, t4, BiFunction<Int, Int, String> { t1, t2 -> " $t1 $t2"})

                Logger.d("Combine API Test!!!!!!!")

//                Single.zip(t1, t2, BiFunction<KobisDailyBoxOfficeListDTO, NaverMovieListDTO, String>
//                { t1, t2 -> " ${t1.toString()} ${t2.toString()}"})
//                    .base()
//                    .subscribe(
//                        {
//                            Logger.d("Combine : $it")
//                        }
//                        ,{
//                            Logger.e("Combine Error : ${it.message}")
//                        }
//                    )
            }

            R.id.flatmap_api -> {
                movieList.clear()

                getBoxOffice().base().map { it -> it.boxOfficeResult.dailyBoxOfficeList }
                    .subscribe({
                        for (i in 0..it.size - 1) {
                            val data = it.get(i)
                            Logger.d("rnumb : ${data.rnum}, rank :  ${data.rank}, movieNm :  ${data.movieNm}")
                        }

                        it.forEach {

                            getNaverAPI(it.movieNm).base().subscribe({
                                Logger.d("naverAPI : ${it.items.get(0).title}")
                                movieList.add(it)
                                updateList()

                            }, {
                                Logger.e("NaverAPI Exception : ${it.localizedMessage}")

                            })
                        }

                    }, {
                        Logger.e("getBoxOffice fail : ${it.message}")
                    })
            }
        }
    }

    fun getBoxOffice() : Single<KobisDailyBoxOfficeListDTO>{
        return APIClient.kobisApi.getDailyBoxOffice(
            "0aaff6d6268952be706a0332880380af",
            "20200830",
            10
        )
    }

    fun getNaverAPI() : Single<NaverMovieListDTO> {
        val query = "주식"
        return APIClient.naverApi.getMovieList("qzbpCZyFMh8t4evQM71u", "4kJ0rub2ub", query, 100)
    }

    fun getNaverAPI(title: String) : Single<NaverMovieListDTO> {
        return APIClient.naverApi.getMovieList("qzbpCZyFMh8t4evQM71u", "4kJ0rub2ub", title, 100)

    }

    fun getNaverAPI(tt: KobisDailyBoxOfficeListDTO) : Single<NaverMovieListDTO> {
        val query = "주식"

        val obser1 = Observable.just("1", "2", "3", "4")
        val obser2 = Observable.just("5", "6", "7", "8")


        val temp = tt.boxOfficeResult
        for(item in temp.dailyBoxOfficeList) {

            return APIClient.naverApi.getMovieList(
                "qzbpCZyFMh8t4evQM71u",
                "4kJ0rub2ub",
                item.movieNm,
                100
            )
        }
        return APIClient.naverApi.getMovieList("qzbpCZyFMh8t4evQM71u", "4kJ0rub2ub", query, 100)

    }

    fun getNaverAPI(t: List<DailyBoxOffice>) : Single<NaverMovieListDTO> {

        Logger.d("getNaverAPI List")
        val query = t.get(0).movieNm

        Logger.d("getNaverAPI List query : {$query}")
        for(item in t) {

            Logger.d("getNaverAPI List query : ${item.movieNm}")
            return APIClient.naverApi.getMovieList(
                "qzbpCZyFMh8t4evQM71u",
                "4kJ0rub2ub",
                item.movieNm,
                100
            )
        }
        return Single.create {  }

//        return Single<NaverMovieListDTO>().a
    }


    fun updateList() {
        Logger.d("movieList size : ${movieList.size}")

        viewpager.adapter = MyRecyclerViewAdaper(this, movieList)

        runOnUiThread(Runnable {
            this.viewpager.adapter?.notifyDataSetChanged()
        })
    }
}