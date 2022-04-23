package com.axel_stein.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.axel_stein.auth.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers.io
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Single.fromCallable {
            val authService = getAuthService()
            authService.auth().execute().body()!!
        }
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe(
                {
                    binding.token.text = it.d.GetContextWebInformation.FormDigestValue
                },
                Throwable::printStackTrace
            )
    }
}

private fun getAuthService(): AuthService {
    val interceptor = HttpLoggingInterceptor()
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

    val auth = NTLMAuthenticator(
        BuildConfig.LOGIN,
        BuildConfig.PASSWORD,
        BuildConfig.DOMAIN
    )

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .authenticator(auth)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://185.144.200.114/")
        .client(client)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(AuthService::class.java)
}