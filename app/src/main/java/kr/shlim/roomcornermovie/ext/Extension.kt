package kr.shlim.roomcornermovie.ext

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


// Rx Extension
operator fun CompositeDisposable.plusAssign(d: Disposable) {
    this.add(d)
}

fun <T> Observable<T>.base() = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.base() = subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())