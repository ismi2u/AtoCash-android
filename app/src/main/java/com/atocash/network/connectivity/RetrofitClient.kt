package com.atocash.network.connectivity

import com.atocash.BuildConfig
import com.atocash.utils.Keys
import com.atocash.utils.extensions.printLog
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

enum class RetrofitClient {
    instance;

    @Volatile
    private var sRetrofit: Retrofit? = null
    private val baseUrlWithPath = Keys.Api.BASE_PATH
    lateinit var apiService: ApiServices

    init {
//        sRetrofit = initRetrofit(baseUrl)
//        apiService = sRetrofit!!.create(ApiServices::class.java)
    }

    fun setBaseUrlForLoggedInUser(url: String) {
        sRetrofit = initRetrofit(url)
        apiService = sRetrofit!!.create(ApiServices::class.java)
    }

    fun formBaseUrl(userName: String?): String {
        val reformedBaseUrl = if (userName.isNullOrEmpty()) {
            "https://${Keys.Api.BASE_PATH}"
        } else {
            val domainArr = userName.split("@")
            val dns = domainArr[1].getBaseUrl()

            printLog("domain name ${domainArr[1]}")
            printLog("domain dns $dns")

            val url = "https://$dns/api/"

            printLog("reformedBaseUrl $url")

            printLog("Reformed BASE URL $url")
            Keys.Api.BASE_URL = url
            Keys.Api.IMAGE_PREFIX = url
            url
        }

        sRetrofit = initRetrofit(reformedBaseUrl)
        apiService = sRetrofit!!.create(ApiServices::class.java)

        return reformedBaseUrl
    }

    private fun initRetrofit(baseUrl: String): Retrofit {

        val okHttpBuilder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(interceptor)
        }
        val httpClient = okHttpBuilder.addInterceptor(Interceptor { chain ->
            val request = chain.request()
            val requestWithUserAgent = request.newBuilder()
                .header("User-Agent", "Android")
                .build()
            val response = chain.proceed(requestWithUserAgent)
//            if (response.code >= 500 || response.code == 405) {
//                val jsonObject = JSONObject()
//                try {
//                    jsonObject.put("message", "There is an issue with your request. Please try again.")
//                    jsonObject.put("isSuccess", false)
//                    jsonObject.put("status", response.code)
//                    jsonObject.put("timestamp", 0L)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//
//                val contentType = response.body!!.contentType()
//                val body = ResponseBody.create(contentType, jsonObject.toString())
//                return@Interceptor response.newBuilder().body(body).build()
//            }
            response
        }).build()
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
//            .addConverterFactory(
//                GsonConverterFactory.create(
//                    GsonBuilder().setLenient().serializeNulls().create()
//                )
//            )
        return builder
            .client(
                httpClient.newBuilder().connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES).build()
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().serializeNulls().create()
                )
            )
            .build()
    }

    companion object {

        fun getPartFromString(string: String): RequestBody {
            return string.toRequestBody("text/plain".toMediaTypeOrNull())
        }

        fun getPartFromImage(imagePath: String, paramName: String): MultipartBody.Part {
            val mainImageFile = File(imagePath)
            val mainImageRequestBody: RequestBody =
                mainImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(
                paramName,
                mainImageFile.name,
                mainImageRequestBody
            )
        }

        fun getPartFromFile(imagePath: String, paramName: String): MultipartBody.Part {
            val mainImageFile = File(imagePath)
            val mainImageRequestBody: RequestBody =
                mainImageFile.asRequestBody("*/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(
                paramName,
                mainImageFile.name,
                mainImageRequestBody
            )
        }
    }

}