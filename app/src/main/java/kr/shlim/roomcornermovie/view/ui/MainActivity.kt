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
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kr.shlim.roomcornermovie.BaseActivity
import kr.shlim.roomcornermovie.BuildConfig
import kr.shlim.roomcornermovie.R
import kr.shlim.roomcornermovie.model.DailyBoxOffice
import kr.shlim.roomcornermovie.model.KobisDailyBoxOfficeListDTO
import kr.shlim.roomcornermovie.model.naver.NaverMovieItem
import kr.shlim.roomcornermovie.model.naver.NaverMovieListDTO
import kr.shlim.roomcornermovie.databinding.ActivityMainBinding
import kr.shlim.roomcornermovie.ext.base
import kr.shlim.roomcornermovie.ext.plusAssign
import kr.shlim.roomcornermovie.network.APIClient
import kr.shlim.roomcornermovie.view.dialog.DatePickerDialogFragment
import kr.shlim.roomcornermovie.viewmodel.MainViewModel
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main, MainViewModel::class) {
    val compositeDisposable : CompositeDisposable = CompositeDisposable()

    val maxCount : Int = 20

    private val viewMdoel : MainViewModel by viewModels()       // activity-ktx

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.d("[timecheck] MainActivity onCrete Start")
        super.onCreate(savedInstanceState)

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
            page.scaleY = 1 - (0.15f * Math.abs(position))
            // If you want a fading effect uncomment the next line:
            page.alpha = 0.25f + (1 - Math.abs(position))
        }

        viewpager.setPageTransformer(pageTransformer)

        val currentDate = getCurrentYYMMDD()
        date_edit.setText(currentDate)
        date_edit.setOnClickListener {
            val editDate = date_edit.text.toString()

            val dateType = SimpleDateFormat("yyyy-MM-dd", Locale(Locale.KOREAN.language, Locale.KOREAN.country)).parse(editDate)
            val cal = Calendar.getInstance()

            val t = editDate.split("-")

            cal.set(t.get(0).toInt(), t.get(1).toInt(), t.get(2).toInt())

            val callback = fun (year : Int, month : Int, day : Int){
//                Toast.makeText(this, "$year, $month, $day", Toast.LENGTH_SHORT).show()

                val cal = Calendar.getInstance()
                cal.set(year, month, day)

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                val simpleDateFormatRequest = SimpleDateFormat("yyyyMMdd")

                val result = simpleDateFormat.format(cal.time)
                val targetDt = simpleDateFormatRequest.format(cal.time)
                date_edit.setText(result)
                getMovieList(targetDt)
            }

            DatePickerDialogFragment(cal.year, cal.month-1, cal.dayOfMonth, callback).show(supportFragmentManager, "")
        }

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd")
        val targetDt = simpleDateFormat.format(calendar.time)
        getMovieList(targetDt)

        Logger.d("[timecheck] MainActivity onCrete end")


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
                val t1 = getBoxOffice("20200830")
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

                val boxOffice = getBoxOffice("20200830")

                var index = 0

                boxOffice.toObservable()
                    .map{ it -> it.boxOfficeResult.dailyBoxOfficeList }
                    .repeat(10)
                    .map{ it ->
                        it.get(index++)}
                    .flatMap {
                        Logger.d("it.movieNm : ${it.movieNm}")
                        getNaverAPI(it.movieNm).toObservable().base().take(10)
                    }.base().subscribe(
                        {
                            Logger.d("naverAPI : ${it.items.get(0).title}")
                            movieList.add(it)
                        },
                        {

                        }, {
                            updateList()
                        }
                    )
//                getBoxOffice().base().map { it -> it.boxOfficeResult.dailyBoxOfficeList }
//                    .subscribe({
//                        for (i in 0..it.size - 1) {
//                            val data = it.get(i)
//                            Logger.d("rnumb : ${data.rnum}, rank :  ${data.rank}, movieNm :  ${data.movieNm}")
//                        }
//
//                        it.forEach {
//
//                            getNaverAPI(it.movieNm).base().subscribe({
//                                Logger.d("naverAPI : ${it.items.get(0).title}")
//                                movieList.add(it)
//                                updateList()
//
//                            }, {
//                                Logger.e("NaverAPI Exception : ${it.localizedMessage}")
//
//                            })
//                        }
//
//                    }, {
//                        Logger.e("getBoxOffice fail : ${it.message}")
//                    })
            }
        }
    }

    fun getMovieList(targetDt : String) {
        movie_loading.startLoadingAnimation()

        movieList.clear()

        val boxOffice = getBoxOffice(targetDt)

        var index = 0

        boxOffice.toObservable()
            .map{ it -> it.boxOfficeResult.dailyBoxOfficeList }
            .repeat(maxCount.toLong())
            .map{ it ->
                it.get(index++)}
            .flatMap {
                Logger.d("it.movieNm : ${it.movieNm}")
                getNaverAPI(it.movieNm).toObservable().base().take(maxCount.toLong())
            }.base().subscribe(
                {
                    Logger.d("naverAPI : ${it.items.get(0).title}")
                    movieList.add(it)
                },
                {
                    Logger.e("naverAPI exception : ${it.message}")
                    updateList()
                }, {
                    updateList()
                }
            )
    }

    fun getBoxOffice(targetDt : String) : Single<KobisDailyBoxOfficeListDTO>{
        val apiKey = BuildConfig.kobisApiKey
        return APIClient.kobisApi.getDailyBoxOffice(
            apiKey,
            targetDt,
            maxCount
        )
    }

    fun getNaverAPI() : Single<NaverMovieListDTO> {
        val query = "주식"
        return APIClient.naverApi.getMovieList("qzbpCZyFMh8t4evQM71u", "4kJ0rub2ub", query, 100)
    }

    fun getNaverAPI(title: String) : Single<NaverMovieListDTO> {
        val clientId: String = BuildConfig.clientId
        val clientSecret: String = BuildConfig.clientSecret
//        return APIClient.naverApi.getMovieList("qzbpCZyFMh8t4evQM71u", "4kJ0rub2ub", title, 100)
        return APIClient.naverApi.getMovieList(clientId, clientSecret, title, 100)

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

        val defaultImageUrl = "https://movie.naver.com/movie/bi/mi/photoViewPopup.nhn?movieCode="

        val t1 = Observable.range(0, movieList.size)
            .base()
            .map { it -> movieList.get(it) }
            .map { it -> it.items.get(0) }
            .flatMap { getImageUrl(it) }
            .flatMap { getTitle(it) }
//            .map { it -> Jsoup.parse(it.title).text() }
            .subscribe (
                {
//                    val code = it.link.substringAfterLast("=")
//                    val codeUrl = defaultImageUrl + code
//
//                    Thread(Runnable {
//                        var doc = Jsoup.connect(codeUrl).get()
//                        val element = doc.select("img")
//
//                        Logger.d("element : ${element.toString()}")
//
//                        val imageUrl = element.get(0).absUrl("src")
//
//                        Logger.d("element imageUrl : ${imageUrl}")
//
//                        it.image = imageUrl
//
//                        it.title = Jsoup.parse(it.title).text()
//
//                    }).start()
                },{

                },{
                    Logger.d("viewpager start")
                    viewpager.adapter =
                        MyRecyclerViewAdaper(
                            this,
                            movieList
                        ) { view, url ->

//                            val pair_thumb = Pair(view, view.getTransitionName())
                            val pair_thumb = Pair(view, view.transitionName)
                            val optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity, pair_thumb)

                            val intent = Intent(this, MovieDetailActivity::class.java)
                            intent.putExtra("imgUrl", url)
                            startActivity(intent, optionsCompat.toBundle())
//                            startActivity(intent)

                        }

                    runOnUiThread(Runnable {
                        this.viewpager.adapter?.notifyDataSetChanged()

                        movie_loading.stopLoadingAnimation()
                    })
                },{

                }
            )
    }


    /**
     * image url Observable
     * @param data
     * @return
     */
    fun getImageUrl(data : NaverMovieItem) : Observable<NaverMovieItem> {
        val defaultImageUrl = "https://movie.naver.com/movie/bi/mi/photoViewPopup.nhn?movieCode="

        val imageObservable = Observable.fromCallable{
            val code = data.link.substringAfterLast("=")
            val codeUrl = defaultImageUrl + code

            var doc = Jsoup.connect(codeUrl).get()
            val element = doc.select("img")

            Logger.d("element : ${element.toString()}")

            if(false == element.isEmpty())  {
                val imageUrl = element.get(0).absUrl("src")
                Logger.d("element imageUrl : ${imageUrl}")
                data.image = imageUrl
            }

            return@fromCallable data
        }.base()

        return imageObservable
    }

    /**
     * get title Observable
     * @param data
     * @return
     */
    fun getTitle(data : NaverMovieItem) :  Observable<NaverMovieItem> {
        val titleObservable = Observable.fromCallable{
            data.title = Jsoup.parse(data.title).text()

            return@fromCallable data
        }.base()
        return titleObservable
    }

    /**
     * 현재 날짜 리턴
     */
    fun getCurrentYYMMDD() : String {

        val calendar = Calendar.getInstance()

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val result = simpleDateFormat.format(calendar.time)
        return result
    }

}