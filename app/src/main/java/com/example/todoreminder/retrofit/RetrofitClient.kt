    package com.example.todoreminder.retrofit

    import com.squareup.moshi.Moshi
    import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
    import retrofit2.Retrofit
    import retrofit2.converter.moshi.MoshiConverterFactory


    object RetrofitClient { //essendo un object è un singleton
        private const val BASE_URL = "http://10.147.237.217:5000" //CAMBIARE OGNI VOLTA CHE SI FA DA HOTSPOT

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val api: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ApiService::class.java)
        }
    }
