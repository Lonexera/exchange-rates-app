package com.hfad.exchangerates.data.common

import com.hfad.exchangerates.domain.service.RetrofitServices
import com.hfad.exchangerates.data.retrofit.RetrofitClient

object Common {
    private val BASE_URL = "https://www.nbrb.by/API/"
    val retrofitService: RetrofitServices
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitServices::class.java)
}
