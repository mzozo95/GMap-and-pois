package com.tier.network

import io.reactivex.Completable
import io.reactivex.CompletableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface RxSchedulers {
    fun <T> applySchedulers(): SingleTransformer<T, T>?
    fun applyCompletableSchedulers(): CompletableTransformer?

    companion object {
        val DEFAULT: RxSchedulers = object : RxSchedulers {
            override fun <T> applySchedulers(): SingleTransformer<T, T> {
                return SingleTransformer { single: Single<T> ->
                    single
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
            }

            override fun applyCompletableSchedulers(): CompletableTransformer {
                return CompletableTransformer { completable: Completable ->
                    completable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                }
            }
        }
        val TEST_SCHEDULER: RxSchedulers = object : RxSchedulers {
            override fun <T> applySchedulers(): SingleTransformer<T, T> {
                return SingleTransformer { single: Single<T> ->
                    single
                        .subscribeOn(Schedulers.trampoline())
                        .observeOn(Schedulers.trampoline())
                }
            }

            override fun applyCompletableSchedulers(): CompletableTransformer {
                return CompletableTransformer { completable: Completable ->
                    completable
                        .subscribeOn(Schedulers.trampoline())
                        .observeOn(Schedulers.trampoline())
                }
            }
        }
    }
}
