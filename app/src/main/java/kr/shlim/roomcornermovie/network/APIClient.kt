package kr.shlim.roomcornermovie.network

import kr.shlim.roomcornermovie.network.api.KMDBApi
import kr.shlim.roomcornermovie.network.api.KobisApi
import kr.shlim.roomcornermovie.network.api.NaverApi
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieHandler
import java.net.CookieManager

object APIClient {

    val naverApi by lazy {
        getRetrofitClient("https://openapi.naver.com").create(NaverApi::class.java)
    }

    val kmdbApi by lazy {
        getRetrofitClient("http://api.koreafilm.or.kr").create(KMDBApi::class.java)
    }

    val kobisApi by lazy {
        getRetrofitClient("http://www.kobis.or.kr").create(KobisApi::class.java)
    }


    fun getRetrofitClient(baseUrl : String) : Retrofit {
        return Retrofit.Builder()
            .client(getOkhttpClient())
            .addConverterFactory(GsonConverterFactory.create())
//            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }


    fun getOkhttpClient() : OkHttpClient{
        val cookieHandler : CookieHandler = CookieManager()
        val builder : OkHttpClient.Builder = OkHttpClient.Builder()
//        if(BuildConfig.DEBUG) {
//            val httpInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//            builder.addInterceptor(httpInterceptor)
//        }

        builder.cookieJar(JavaNetCookieJar(cookieHandler))
        return builder.build()
    }

}