package com.example.chatapp.model.network

import android.content.Context
import android.util.Log
import com.example.chatapp.BuildConfig
import com.example.chatapp.helper.isConnectedToInternet
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder

import com.google.gson.Gson




class ApiClient {

    private var mRetrofit: Retrofit? = null
    private var mRetrofitWithToken: Retrofit? = null
    private val mLoggingInterceptor = HttpLoggingInterceptor()


    /***
     * Without token
     */

    private val certificatePinner: CertificatePinner = CertificatePinner.Builder()
        .add("inthenameofholygod.com", "sha256/Ko8tivDrEjiY90yGasP6ZpBU4jwXvHqVvQI0GS3GNdA=")
        .build()

    private val httpLogClient: OkHttpClient
        get() {
            if (BuildConfig.DEBUG) {
                mLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(mLoggingInterceptor)
                .certificatePinner(certificatePinner)
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
        }

    val client: Retrofit
        get() {
            if (mRetrofit == null) {
                mRetrofit = Retrofit.Builder()
                    .baseUrl(APIConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpLogClient)
                    .build()
            }
            return mRetrofit!!
        }


    /***
     * With token
     */

    private fun getHttpLogClientWithToken(context: Context): OkHttpClient {
        if (BuildConfig.DEBUG) {
            mLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cache(getCache(context))
            .certificatePinner(certificatePinner)
            .addInterceptor(mLoggingInterceptor)
            .addInterceptor(TokenRenewInterceptor(context))
            .addNetworkInterceptor(provideCacheInterceptor(context))
            .addInterceptor(provideOfflineCacheInterceptor(context))
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    fun getClientWithToken(context: Context): Retrofit {
        if (mRetrofitWithToken == null) {
            mRetrofitWithToken = Retrofit.Builder()
                .baseUrl(APIConstants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpLogClientWithToken(context))
                .build()
        }
        return mRetrofitWithToken!!
    }

    private fun getCache(context: Context): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.cacheDir, "http-cache"), 80 * 1024 * 1024)
        } catch (e: java.lang.Exception) {
            Log.e("Cache", "Error in creating  Cache!")
        }

        return cache
    }

    private fun provideCacheInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl = if (context.isConnectedToInternet()) {
                CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build()
            } else {
                CacheControl.Builder()
                    .maxStale(8, TimeUnit.DAYS)
                    .build()
            }
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }


    private fun provideOfflineCacheInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!context.isConnectedToInternet()) {
                val cacheControl: CacheControl = CacheControl.Builder()
                    .maxStale(8, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    companion object {
        fun getInstance(context: Context): ApiInterface = ApiClient().getClientWithToken(context)
            .create(ApiInterface::class.java)

        const val HEADER_CACHE_CONTROL = "Cache-Control"
        const val HEADER_PRAGMA = "Pragma"
    }
}